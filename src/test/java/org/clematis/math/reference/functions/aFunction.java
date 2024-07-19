// Created: Feb 14, 2003 T 12:27:38 PM
package org.clematis.math.reference.functions;

import java.io.Serializable;
import java.util.ArrayList;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.FunctionFactory;
import org.clematis.math.reference.algorithm.iParameterProvider;
import org.clematis.math.reference.iExpressionItem;
import org.clematis.math.reference.iFunction;
import org.clematis.math.reference.operations.Power;
import org.jdom2.Element;

/**
 * Abstract constant function.
 *
 */
public abstract class aFunction implements iFunction, Serializable
{
    /**
     * Parent function factory
     */
    protected FunctionFactory functionFactory = new FunctionFactory();
   /**
    * The list of arguments
    */
    protected ArrayList<iExpressionItem> arguments = new ArrayList<iExpressionItem>();
    /**
     * Function signature
     */
    protected String signature = "";
   /**
    * Operation multiplier. For instance, it could be constant 2 in 2 * 5 ^ x
    */
    private double multiplier = 1.0;
    /**
     * Add argument to a function
     * @param argument expression item as a parameter
     */
    public void addArgument(iExpressionItem argument)
    {
        arguments.add(argument);
    }
    /**
     * Add arguments to a function
     * @param arguments expression items as a parameter
     */
    public void addArguments(ArrayList<iExpressionItem> arguments)
    {
        this.arguments.addAll( arguments );
    }
    /**
     * Removes all arguments from the function
     */
    public void removeArguments()
    {
        this.arguments.clear();
    }
    /**
     * Return a set of arguments of this function
     *
     * @return an iExpressionItem set of arguments of this function
     */
    public iExpressionItem[] getArguments()
    {
        iExpressionItem[] ret = new iExpressionItem[ arguments.size() ];
        System.arraycopy( arguments.toArray(), 0, ret, 0, arguments.size() );
        return ret;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public FunctionFactory getFunctionFactory()
    {
        return functionFactory;
    }
    public void setFunctionFactory(FunctionFactory functionFactory)
    {
        this.functionFactory = functionFactory;
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML()
    {
        Element apply = new Element("apply"/*, Constants.NS_MATH*/);

        Element times = new Element("times"/*, Constants.NS_MATH*/);
        apply.addContent( times );
        Element cn = new Element("cn"/*, Constants.NS_MATH*/);
        cn.setText( Double.toString(getMultiplier()) );
        apply.addContent(cn);

        Element apply2 = new Element("apply"/*, Constants.NS_MATH*/);

        Element function = new Element( signature );
        apply2.addContent( function );
        for ( int i = 0; i < arguments.size(); i++ )
        {
            Element argument = ((iExpressionItem)arguments.get( i )).toMathML();
            apply2.addContent( argument );
        }

        apply.addContent( apply2 );

        return apply;
    }

    public boolean equals(iExpressionItem item)
    {
        if (this == item) return true;
        if (!(item instanceof aFunction)) return false;

        final aFunction aFunction = (aFunction)item;

        if (signature != null ? !signature.equals(aFunction.signature) :
                                    aFunction.signature != null)
        {
            return false;
        }

        return true;
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter
     *                          and functions provider
     * @return expression item instance
     */
    public iExpressionItem calculate(iParameterProvider parameterProvider) throws AlgorithmException
    {
        return calculate();
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean aKindOf(iExpressionItem item)
    {
        if (this == item) return true;
        if ( item instanceof aFunction )
        {
            return equals( item );
        }
        else if ( item instanceof Power )
        {
            iExpressionItem expression = ((Power) item).getOperand1();
            if ( expression instanceof aFunction )
            {
                return equals( expression );
            }
        }
        return false;
    }
    /**
     * Return constant coefficient
     * @return constant coefficient
     */
    public double getMultiplier()
    {
        return multiplier;
    }
    /**
     * Sets constant multiplier
     * @param multiplier
     */
    public void setMultiplier(double multiplier)
    {
        this.multiplier = multiplier;
    }
    /**
     * Adds another expression item to this one.
     * @param item another expression item
     * @return result expression item
     */
    public iExpressionItem add(iExpressionItem item)
    {
        if ( item instanceof aFunction && item.aKindOf( this ) )
        {
            this.setMultiplier( this.getMultiplier() + item.getMultiplier() );
            return this;
        }
        return null;
    }

    /**
     * Multiplies this expression item by another one.
     * @param item another expression item
     * @return result expression item
     */
    public iExpressionItem multiply(iExpressionItem item) throws AlgorithmException
    {
        if ( item.aKindOf( this ) )
        {
            if ( item instanceof aFunction )
            {
                /**
                 *  2 * sin ( x ) * 3 * sin ( x ) = 6 * sin ( x ) ^ 2
                 */
                Power power = new Power( this, new Constant( 2 ) );
                power.setMultiplier( this.getMultiplier() * item.getMultiplier() );
                this.setMultiplier( 1 );
                return power;
            }
            else if ( item instanceof Power )
            {
                /**
                 *  2 * sin ( x ) * 3 * sin ( x ) ^ 3 = 6 * sin ( x ) ^ 4
                 */
                Power power = ( Power ) item;
                power.setMultiplier( this.getMultiplier() * item.getMultiplier() );
                power.setPower( power.getPower().add( new Constant( 1 ) ) );
                return power;
            }
        }
        else if ( item instanceof Constant )
        {
            this.setMultiplier( this.getMultiplier() * ((Constant) item).getNumber() );
            return this;
        }
        return null;
    }
    /**
     * Returns a string representation of the object.
     *
     *   2 * x ^ 2
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

        sb.append( signature );
        sb.append("(");
        for ( int i = 0; i < arguments.size(); i++ )
        {
            iExpressionItem arg = arguments.get( i );
            sb.append( arg.toString() );
            if ( i + 1 != arguments.size() )
            {
               sb.append( "," );
            }
        }
        sb.append(")");

        return sb.toString();
    }
}
