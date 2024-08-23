// Created: 11.02.2005 T 15:53:22
package org.clematis.math.v2.parsers.string;

import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;
import org.clematis.math.v2.parsers.StringMathParserTreeConstants;

/**
 * Prints nodes in Web Tex format
 *
 * @version 1.0
 */
public class WebTexPrinter {
    public static void print(SimpleNode node, StringBuilder sb) {
        switch (node.getId()) {
            /** void */
            case StringMathParserTreeConstants.JJTVOID:
                break;
            /** expression */
            case StringMathParserTreeConstants.JJTEXPR:
            case StringMathParserTreeConstants.JJTEQUALS_EXPR:
                WebTexPrinter.printChildren(node, sb);
                break;
            case StringMathParserTreeConstants.JJTVECTOR_EXPR:
                WebTexPrinter.printVector(node, sb);
                break;
            /** unary minus */
            case StringMathParserTreeConstants.JJTUNARY_MINUS:
                WebTexPrinter.printUnaryMinus(sb, node);
                break;
            /** plus */ /** minus */ /** mult */
            case StringMathParserTreeConstants.JJTPLUS_EXPR:
            case StringMathParserTreeConstants.JJTMINUS_EXPR:
            case StringMathParserTreeConstants.JJTMULT_EXPR:
            case StringMathParserTreeConstants.JJTINDEX_EXPR:
                WebTexPrinter.printSequence(node, sb);
                break;
            /** div */
            case StringMathParserTreeConstants.JJTDIV_EXPR:
                WebTexPrinter.printDiv(node, sb);
                break;
            /** power expression */
            case StringMathParserTreeConstants.JJTPOWER_EXPR:
                WebTexPrinter.printPowerExpression(node, sb);
                break;
            /** br expression */
             /* case StringMathParserTreeConstants.JJTBR_EXPR:
                  WebTexPrinter.printBrChildren( node, sb ); break;*/
            /** var */ /** number */
            case StringMathParserTreeConstants.JJTVAR:
            case StringMathParserTreeConstants.JJTNUMBER:
            case StringMathParserTreeConstants.JJTSTRING:
            case StringMathParserTreeConstants.JJTIDENTIFIER:
                sb.append(node);
                break;
            /** line */
             /* case StringMathParserTreeConstants.JJTLINE:
                  WebTexPrinter.printLine( node, sb ); break; */
            /** function */
            case StringMathParserTreeConstants.JJTFUNCTION:
                WebTexPrinter.printFunction(node, sb);
                break;
            /** arg */
              /*case StringMathParserTreeConstants.JJTARG:
                  WebTexPrinter.printArgument( node, sb ); break; */
        }
    }

    static void printUnaryMinus(StringBuilder sb, SimpleNode node) {
        sb.append("-");
        WebTexPrinter.printChildren(node, sb);
    }

    /**
     * vec(v)_f
     * <p>
     * VECTOR_EXPR [token=vec]
     * ARG [token=]
     * BR_EXPR [token=]
     * LINE [token=(v)]
     * IDENTIFIER [token=f]
     */
    static void printVector(SimpleNode node, StringBuilder sb) {
        sb.append(node.toString());
        WebTexPrinter.printChildren(node, sb);
    }

    static void printPowerExpression(SimpleNode node, StringBuilder sb) {
        if (node.getChildren() != null) {
            Node expression = node.getChildren()[0];
            Node exponent = node.getChildren()[node.getChildren().length - 1];
            Node expression_inside_braces = null;
            boolean sqrt = ((SimpleNode) exponent).getTokenName().equals("0.5");
            /** exponent is (0.5) */
          /*  if ( (expression_inside_braces = getBrExpressionCore(exponent)) != null )
            {
                if ( ((SimpleNode) expression_inside_braces).getTokenName().equals("0.5") )
                {
                    sqrt = true;
                }
            }   */

            /** exponent is 0.5 */
            /*else*/
            if (sqrt) {
                sb.append("\\sqrt{");
                /** print expression */
/*                 if ( (expression_inside_braces = getBrExpressionCore(expression)) != null )
                 {
                     WebTexPrinter.print( (SimpleNode)expression_inside_braces, sb );
                 }
                 else
                 {*/
                for (int i = 0; i < node.getChildren().length - 1; i++) {
                    SimpleNode n = (SimpleNode) node.getChildren()[i];
                    if (n != null) {
                        WebTexPrinter.print(n, sb);
                    }
                }
                /*}*/
                sb.append("}");
                return;
            }

            /** special print for exponent */
            if (((SimpleNode) expression).getTokenName().equals("e")) {
                sb.append("e^{");
                /** print sequence */
                WebTexPrinter.print((SimpleNode) exponent, sb);
                sb.append("}");
                return;
            }

            /** print function */
            WebTexPrinter.printSequence(node, sb);
        }
    }
    /**
     * Return the core of braced expression
     *
     * @param brExpression braced expression
     * @return the core of braced expression
     */
  /*  static Node getBrExpressionCore(Node brExpression)
    {
        if ( ((SimpleNode) brExpression).id == StringMathParserTreeConstants.JJTBR_EXPR )
        {
         /** take middle child */
      /*      if ( brExpression.jjtGetNumChildren() == 1 )
            {
                return brExpression.jjtGetChild(0);
            }
            else
            {
                return brExpression.jjtGetChild(1);
            }
        }
        return null;
    }     */

    /**
     * Prints sequences
     * <p>
     * a + b + c
     * a - b - c
     * a * b * c
     *
     * @param node
     */
    static String printSequence(SimpleNode node, StringBuilder sb) {
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().length; i++) {
                SimpleNode n = (SimpleNode) node.getChildren()[i];
                if (n != null) {
                    WebTexPrinter.print(n, sb);
                }
                if (i != node.getChildren().length - 1) {
                    sb.append(node.getTokenName());
                }
            }
        }
        return sb.toString();
    }

    /**
     * Prints functions
     * <p>
     * signature()
     *
     * @param node
     * @param sb
     */
    static String printFunction(SimpleNode node, StringBuilder sb) {
        boolean parentPowerFunction = (((SimpleNode) node.jjtGetParent()).getId()
            == StringMathParserTreeConstants.JJTPOWER_EXPR);
        if (parentPowerFunction) {
            sb.append("{");
        }
        /** turn exp functions into e^(args) */
        if (node.getTokenName().equalsIgnoreCase("exp")) {
            sb.append("e^");
        } else {
            /** signature */
            sb.append("\\" + node.getTokenName());
        }

        /** arguments */
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().length; i++) {
                SimpleNode n = (SimpleNode) node.getChildren()[i];
                if (n != null) {
                    WebTexPrinter.print(n, sb);
                }
                if (i != node.getChildren().length - 1) {
                    sb.append(" ");
                }
            }
        }

        if (parentPowerFunction) {
            sb.append("}");
        }

        return sb.toString();
    }

    /**
     * Prints division
     * <p>
     * \frac()()
     *
     * @param node
     * @param sb
     */
    static String printDiv(SimpleNode node, StringBuilder sb) {
        /** signature */
        sb.append("\\frac");
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().length; i++) {
                SimpleNode n = (SimpleNode) node.getChildren()[i];
                if (n != null) {
                    sb.append("(");
                    WebTexPrinter.print(n, sb);
                    sb.append(")");
                }
                sb.append(" ");
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
    static String printChildren(SimpleNode node, StringBuilder sb) {
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().length; i++) {
                SimpleNode n = (SimpleNode) node.getChildren()[i];
                if (n != null) {
                    WebTexPrinter.print(n, sb);
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
  /*  static String printBrChildren(SimpleNode node, StringBuilder sb)
    {
        if (node.children != null)
        {
          for (int i = 0; i < node.children.length; i++)
          {
              SimpleNode n = (SimpleNode)node.children[i];
              if (n != null )
              {
                  if ( skipBraces(n)
                       &&
                       ( i ==0 || i == node.children.length-1 )
                       &&
                       ( n.id == StringMathParserTreeConstants.JJTOPEN_BR
                         ||
                         n.id == StringMathParserTreeConstants.JJTCLOSE_BR )
                     )
                  {
                      sb.append(" ");
                      continue;
                  }
                  WebTexPrinter.print( n, sb );
              }
          }
        }

        return sb.toString();
    }     */

    /**
     * Prints children in braces
     *
     * @param node
     * @param sb
     */
    static String printLine(SimpleNode node, StringBuilder sb) {
        SimpleNode parent = (SimpleNode) node.jjtGetParent();
        // check to see if we skip braces for parent (mean parent is braces expression)
        boolean skipBraces = skipBraces(parent);
        String str = node.toString();
        if (skipBraces) {
            if (str.startsWith("(")) {
                str = str.substring(1);
            }
            if (str.endsWith(")")) {
                str = str.substring(0, str.length() - 1);
            }
            sb.append(" " + str + " ");
        } else {
            sb.append(node);
        }
        return sb.toString();
    }

    private static boolean skipBraces(SimpleNode node) {
        Node parent = node.jjtGetParent();
        /** in this case skip braced expression */
        return ((SimpleNode) parent).getId() == StringMathParserTreeConstants.JJTFUNCTION
            &&
            (
                ((SimpleNode) parent.jjtGetParent()).getId() == StringMathParserTreeConstants.JJTDIV_EXPR ||
                    ((SimpleNode) parent.jjtGetParent()).getTokenName().equalsIgnoreCase("sqrt")
            );
    }

    /**
     * Prints arguments
     *
     * @param node
     * @param sb
     */
    static String printArgument(SimpleNode node, StringBuilder sb) {
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().length; i++) {
                SimpleNode n = (SimpleNode) node.getChildren()[i];
                if (n != null) {
                    WebTexPrinter.print(n, sb);
                }
            }
        }

        return sb.toString();
    }
}
