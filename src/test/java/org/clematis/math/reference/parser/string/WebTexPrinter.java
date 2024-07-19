// Created: 11.02.2005 T 15:53:22
package org.clematis.math.reference.parser.string;

/**
 * Prints nodes in Web Tex format
 */
class WebTexPrinter
{
     public static void print(SimpleNode node, StringBuilder sb)
     {
          switch( node.id )
          {
              /** void */
              case StringMathParserTreeConstants.JJTVOID: break;
              /** expression */
              case StringMathParserTreeConstants.JJTEXPR:
                  WebTexPrinter.printChildren( node, sb ); break;
              /** unary minus */
              case StringMathParserTreeConstants.JJTUNARY_MINUS:
                  WebTexPrinter.printUnaryMinus(sb, node); break;
              /** plus */ /** minus */ /** mult */
              case StringMathParserTreeConstants.JJTPLUS_EXPR:
              case StringMathParserTreeConstants.JJTMINUS_EXPR:
              case StringMathParserTreeConstants.JJTMULT_EXPR:
                  WebTexPrinter.printSequence( node, sb ); break;
              /** div */
              case StringMathParserTreeConstants.JJTDIV_EXPR:
                  WebTexPrinter.printDiv( node, sb ); break;
              /** power expression */
              case StringMathParserTreeConstants.JJTPOWER_EXPR:
                  WebTexPrinter.printPowerExpression(node, sb); break;
              /** br expression */
              case StringMathParserTreeConstants.JJTBR_EXPR:
                  WebTexPrinter.printBrChildren( node, sb ); break;
              /** var */ /** number */
              case StringMathParserTreeConstants.JJTVAR:
              case StringMathParserTreeConstants.JJTNUMBER:
              case StringMathParserTreeConstants.JJTOPEN_BR:
              case StringMathParserTreeConstants.JJTCLOSE_BR:
              case StringMathParserTreeConstants.JJTPI:
              case StringMathParserTreeConstants.JJTIDENTIFIER:
                  sb.append( node.toString() ); break;
              /** function */
              case StringMathParserTreeConstants.JJTFUNCTION:
                  WebTexPrinter.printFunction( node, sb ); break;
              /** arg */
              case StringMathParserTreeConstants.JJTARG:
                  WebTexPrinter.printArgument( node, sb ); break;
          }
     }

    static void printUnaryMinus(StringBuilder sb, SimpleNode node)
    {
        sb.append( "-" );
        WebTexPrinter.printChildren( node, sb );
    }

    static void printPowerExpression(SimpleNode node, StringBuilder sb)
    {
        if (node.children != null)
        {
            Node expression = node.children[ 0 ];
            Node exponent = node.children[ node.children.length - 1 ];
            Node expression_inside_braces = null;
            boolean sqrt = false;
            /** exponent is (0.5) */
            if ( (expression_inside_braces = getBrExpressionCore(exponent)) != null )
            {
                if ( ((SimpleNode) expression_inside_braces).getTokenName().equals("0.5") )
                {
                    sqrt = true;
                }
            }
            /** exponent is 0.5 */
            else if ( ((SimpleNode) exponent).getTokenName().equals("0.5") )
            {
                sqrt = true;
            }
            if ( sqrt )
            {
                 sb.append("\\sqrt{");
                 /** print expression */
                 if ( (expression_inside_braces = getBrExpressionCore(expression)) != null )
                 {
                     WebTexPrinter.print( (SimpleNode)expression_inside_braces, sb );
                 }
                 else
                 {
                     for (int i = 0; i < node.children.length - 1; i++)
                     {
                         SimpleNode n = (SimpleNode)node.children[i];
                         if (n != null)
                         {
                             WebTexPrinter.print( n, sb );
                         }
                     }
                 }
                 sb.append("}");
                 return;
            }

             /** special print for exponent */
             if ( ((SimpleNode) expression).getTokenName().equals("e") )
             {
                 sb.append("e^{");
                 /** print sequence */
                 WebTexPrinter.print( (SimpleNode) exponent, sb );
                 sb.append("}");
                 return;
             }

             /** print function */
             WebTexPrinter.printSequence( node, sb );
        }
    }
    /**
     * Return the core of braced expression
     *
     * @param brExpression braced expression
     * @return the core of braced expression
     */
    static Node getBrExpressionCore(Node brExpression)
    {
        if ( ((SimpleNode) brExpression).id == StringMathParserTreeConstants.JJTBR_EXPR )
        {
         /** take middle child */
            return brExpression.jjtGetChild(1);
        }
        return null;
    }
    /**
     * Prints sequences
     *
     * a + b + c
     * a - b - c
     * a * b * c
     *
     * @param node
     */
    static String printSequence(SimpleNode node, StringBuilder sb)
    {
        if (node.children != null)
        {
          for (int i = 0; i < node.children.length; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null)
              {
                  WebTexPrinter.print( n, sb );
              }
              if ( i != node.children.length - 1)
              {
                  sb.append( node.getTokenName() );
              }
          }
        }
        return sb.toString();
    }
    /**
     * Prints functions
     *
     * signature()
     *
     * @param node
     * @param sb
     */
    static String printFunction(SimpleNode node, StringBuilder sb)
    {
        boolean parentPowerFunction = (((SimpleNode) node.jjtGetParent()).id
                                     == StringMathParserTreeConstants.JJTPOWER_EXPR);
        if ( parentPowerFunction )
        {
           sb.append("{");
        }
        /** turn exp functions into e^(args) */
        if ( node.getTokenName().equalsIgnoreCase("exp") )
        {
            sb.append("e^");
        }
        else
        {
            /** signature */
            sb.append( "\\" + node.getTokenName() );
        }

        /** arguments */
        if (node.children != null)
        {
          for (int i = 0; i < node.children.length; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null)
              {
                  sb.append("{");
                  WebTexPrinter.print( n, sb );
                  sb.append("}");
              }
              if ( i != node.children.length - 1)
              {
                  sb.append(" ");
              }
          }
        }

        if ( parentPowerFunction )
        {
           sb.append("}"); 
        }

        return sb.toString();
    }
    /**
     * Prints division
     *
     * \frac{}{}
     *
     * @param node
     * @param sb
     */
    static String printDiv(SimpleNode node, StringBuilder sb)
    {
        /** signature */
        sb.append( "\\frac" );
        if (node.children != null)
        {
          for (int i = 0; i < node.children.length; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null)
              {
                  sb.append("{");
                  WebTexPrinter.print( n, sb );
                  sb.append("}");
              }
              sb.append( " " );
          }
        }
        return sb.toString();
    }
    /**
     * Prints children
     *
     * @param node
     * @param sb
     */
    static String printChildren(SimpleNode node, StringBuilder sb)
    {
        if (node.children != null)
        {
          for (int i = 0; i < node.children.length; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null)
              {
                  WebTexPrinter.print( n, sb );
              }
          }
        }

        return sb.toString();
    }
    /**
     * Prints children in braces
     *
     * @param node
     * @param sb
     */
    static String printBrChildren(SimpleNode node, StringBuilder sb)
    {
        int startChild = 0;
        int endChild = node.children.length;

        Node parent = node.jjtGetParent();
        /** in this case skip braced expression */
        if ( ((SimpleNode) parent).id == StringMathParserTreeConstants.JJTARG
              && 
              (
                ((SimpleNode) parent.jjtGetParent()).id == StringMathParserTreeConstants.JJTDIV_EXPR ||
                ((SimpleNode) parent.jjtGetParent()).getTokenName().equalsIgnoreCase( "sqrt" )
              )
           )
        {
            startChild++;
            endChild--;
        }

        if (node.children != null)
        {
          for (int i = startChild; i < endChild; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null)
              {
                  WebTexPrinter.print( n, sb );
              }
          }
        }

        return sb.toString();
    }
    /**
     * Prints arguments
     *
     * @param node
     * @param sb
     */
    static String printArgument(SimpleNode node, StringBuilder sb)
    {
        if (node.children != null)
        {
          for (int i = 0; i < node.children.length; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null)
              {
                  WebTexPrinter.print( n, sb );
              }
          }
        }

        return sb.toString();
    }
}
