// Created: Feb 14, 2003 T 11:29:25 AM
package org.clematis.math.reference.operations;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.iExpressionItem;
import org.jdom2.Element;
/**
 * Addition operation.
 */
public class Addition extends aOperation
{
   /**
     * Public constructor for addition operation.
     * @param in_operand1 first operand
     * @param in_operand2 second operand
     */
    public Addition( iExpressionItem in_operand1, iExpressionItem in_operand2 )
    {
        super( in_operand1, in_operand2 );
    }
    public iExpressionItem calculate() throws AlgorithmException
    {
        /**
         * First try to calculate each operand
         */
        iExpressionItem a = getOperand1().calculate();
        iExpressionItem b = getOperand2().calculate();
        iExpressionItem result = a.add( b );

        if (result == null)
        {
            result = new Addition(a, b);
        }

        return result.multiply(new Constant(getMultiplier()));
    }
    /**
     * Adds new expression item to this sum. If this expression item
     * cannot be added to any of sum operands, we return new sum with
     * this sum as operand and item as second operand.
     * @param item
     * @return this or new addition
     */
    public iExpressionItem add(iExpressionItem item) throws AlgorithmException
    {
       if ( item instanceof Addition)
       {
           Addition a_item = ( Addition ) item;

           iExpressionItem res1 = getOperand1().add(a_item.getOperand1());
           iExpressionItem res2 = null;

           if (res1 == null)
           {
               res1 = getOperand2().add(a_item.getOperand1());
               res2 = getOperand1().add(a_item.getOperand2());
           }
           else
           {
               res2 = getOperand2().add(a_item.getOperand2());
           }
           if ( res1 != null && res2 != null )
           {
               Addition retvalue = new Addition(res1, res2);
               retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
               return retvalue;
           }
           else
           {
               return null;
           }
       }
       else
       {
           iExpressionItem result = getOperand1().add(item);

           if (result == null)
           {
               result = getOperand2().add(item);
               if (result == null)
               {
                   return null;
               }
               else
               {
                   Addition retvalue = new Addition(getOperand1(), result);
                   retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                   return retvalue;
               }
           }
           else
           {
               Addition retvalue = new Addition(result, getOperand2());
               retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
               return retvalue;
           }
       }
    }
    /**
     * Multiplies both operands by item, if any multiplication fails,
     * we should produce a simple multiplication without any removal of
     * sum braskets.
     * @param item
     * @return addition or multiplication.
     */
    public iExpressionItem multiply(iExpressionItem item) throws AlgorithmException
    {
         if ( item instanceof Constant )
         {
             this.setMultiplier( ((Constant) item).getNumber() * getMultiplier());
             return this;
         }
         else if ( item instanceof Addition )
         {
             if ( item.equals(this) )
             {
                 return new Power ( this, new Constant( "2" ) );
             }
         }
         else if ( item instanceof Power )
         {
             Power p = ( Power ) item;
             if ( p.getOperand1().equals(this) )
             {
                 p.changeExponent( "1" );
                 return p.calculate();
             }
         }
         return null;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean aKindOf(iExpressionItem item)
    {
        if ( item instanceof Addition )
        {
            Addition add_item = (Addition) item;
            iExpressionItem op1 = add_item.getOperand1();
            iExpressionItem op2 = add_item.getOperand2();
            if ( op1.aKindOf(getOperand1()) && op2.aKindOf(getOperand2())
                 ||
                 op2.aKindOf(getOperand1()) && op1.aKindOf(getOperand2()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(iExpressionItem item)
    {
        if ( item instanceof Addition )
        {
            Addition add_item = (Addition) item;
            iExpressionItem op1 = add_item.getOperand1();
            iExpressionItem op2 = add_item.getOperand2();
            if ( ( op1.equals(getOperand1()) && op2.equals(getOperand2()) )
                 ||
                 ( op2.equals(getOperand1()) && op1.equals(getOperand2()) ) )
            {
                return getMultiplier() == item.getMultiplier();
            }
        }
        return false;
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML()
    {
        Element apply = new Element("apply");

        Element times = new Element("times");
        apply.addContent( times );
        Element cn = new Element("cn");
        cn.setText( Double.toString(getMultiplier()) );
        apply.addContent(cn);

        Element apply2 = new Element("apply");
        apply2.addContent(new Element("plus"));
        apply2.addContent( getOperand1().toMathML() );
        apply2.addContent( getOperand2().toMathML() );

        apply.addContent( apply2 );

        return apply;
    }

    /**
     * Returns a string representation of the object.
     *
     *   x + y or 2 (x + y)
     *
     * @return  a string representation of the object.
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if ( getMultiplier() != 1)
        {
            sb.append(new Constant(getMultiplier()).toString());
            sb.append("*");
        }
        sb.append("(");
        sb.append(getOperand1().toString());
        sb.append("+");
        sb.append(getOperand2().toString());
        sb.append(")");
        return sb.toString();
    }
}
