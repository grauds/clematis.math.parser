// $Id: Variable.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 13, 2003 T 6:20:41 PM

package org.clematis.math.reference;

import java.io.Serializable;
import java.util.List;

import org.clematis.math.reference.algorithm.iParameterProvider;
import org.clematis.math.reference.algorithm.iVariableProvider;
import org.clematis.math.reference.operations.Multiplication;
import org.clematis.math.reference.operations.Power;
import org.clematis.math.utils.StringUtils;
import org.jdom2.Element;

/**
 * Representation of x or y variables in formulas.
 * @version 1.0
 */
public class Variable implements iExpressionItem, Serializable
{
   /**
     * Varaiable name.
     */
    private String name = null;
    /**
     * Variable multiplier.
     */
    private double multiplier = 1.0;
    /**
     * Constructor.
     * @param in_name String containing the variable name.
     */
    private Variable( String in_name )
    {
        name = in_name;
    }
    /**
     * Create either variable or multiplication, it depends on
     * the name in arguments.
     *
     * @param name for future variable
     * @return variable or multiplication
     */
    public static iExpressionItem create( String name )
    {
        return Variable.create( null, name );
    }
    /**
     * Create either variable or multiplication, it depends on
     * the name in arguments.
     *
     * @param name for future variable
     * @return variable or multiplication
     */
    public static iExpressionItem create( iVariableProvider varProvider, String name )
    {
        List<String> arr = StringUtils.tokenizeReg( name, "[:digit:]+|[:alpha:]+[:alnum:]*", false);

        if ( arr.size() > 1 )
        {
            String arg = arr.get( 0 );
            iExpressionItem argItem = null;

            // number
            if ( MathUtils.isDigit(arg.charAt(0)) )
            {
                argItem = new Constant( Double.parseDouble( arg ) );
            }
            // variable
            else
            {
                argItem = new Variable( arg );
            }

            for ( int i = 1; i < arr.size(); i++ )
            {
                iExpressionItem argItem1;
                String arg1 = arr.get( i );

                // number
                if ( MathUtils.isDigit(arg1.charAt(0)) )
                {
                    try
                    {
                        argItem1 = new Constant( Double.parseDouble( arg1 ) );
                    }
                    // variable
                    catch(NumberFormatException ex)
                    {
                        argItem1 = applyVariableProvider( varProvider, arg1 );
                    }
                }
                // variable
                else
                {
                    argItem1 = applyVariableProvider( varProvider, arg1 );
                }

                argItem = new Multiplication( argItem, argItem1 );
            }

            return argItem;
        }
        else
        {
            return applyVariableProvider( varProvider, name );
        }
    }

    /**
     * Apply variable provider in process of variable creation
     *
     * @param varProvider provider of variables
     * @param name of variable
     * @return either variable or constant
     */
    private static iExpressionItem applyVariableProvider(iVariableProvider varProvider, String name)
    {
        if ( varProvider != null )
        {
            AbstractConstant constant = varProvider.getVariableConstant(name);
            if (constant != null)
            {
                return constant.copy();
            }
        }
        return new Variable( name );
    }
    /**
     * Gets the variable name.
     * @return String containing the variable name.
     */
    public String getName()
    {
        return name;
    }
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate()
    {
        return this;
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
        return this;
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
        if( item instanceof Variable )
        {
            Variable v = (Variable)item;
            if( v.getName().equals( name ) )
            {
                multiplier += v.getMultiplier();
                return this;
            }
        }
        return null;
    }
    /**
     * Set enable significant digits flag
     * @param flag
     */
    public void setSdEnabled(int flag) { }
    /**
     * Stub implementation of iExpressionItem interface.
     * Does nothing.
     * @param sdNumber the number of significant digits.
     */
    public void setSdNumber( int sdNumber ){}
    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML()
    {
        Element apply = new Element("apply", iExpressionItem.NS_MATH);

        Element times = new Element("times", iExpressionItem.NS_MATH);
        apply.addContent( times );

        Element cn = new Element("cn", iExpressionItem.NS_MATH);
        cn.setText( Double.toString(getMultiplier()) );
        apply.addContent(cn);

        Element ci = new Element("ci", iExpressionItem.NS_MATH);
        ci.setAttribute("type", "real");
        ci.setText( this.getName() );
        apply.addContent( ci );

        return apply;
    }

    /**
     * Multiplies this expression item by another one.
     * @param item another expression item
     * @return result expression item
     */
    public iExpressionItem multiply(iExpressionItem item) throws AlgorithmException
    {
        if( item instanceof Constant )
        {
            Constant c = (Constant) item;
            multiplier *= c.getNumber();
            return this;
        }
        else if ( item instanceof Variable )
        {
            Variable v = (Variable) item;
            if ( this.aKindOf(v) )
            {
                Power p = new Power(this, new Constant(2));
                p.setMultiplier( getMultiplier() * v.getMultiplier() );
                return p;
            }
        }
        else if ( item instanceof Power )
        {
            Power p = (Power)item;
            /**
             * Compare roots
             */
            if ( this.aKindOf( p.getOperand1() ) )
            {
                p.changeExponent( "1" );
                p.setMultiplier( getMultiplier() * p.getMultiplier() );
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
        if (item instanceof Variable)
        {
           String item_name = ((Variable) item).getName();
           return item_name.equalsIgnoreCase(this.getName());
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
        if (item instanceof Variable)
        {
           String item_name = ((Variable) item).getName();
           return (
                   item_name.equalsIgnoreCase(this.getName())
                    &&
                   ( getMultiplier() == item.getMultiplier() )
                   );
        }
        return false;
    }

    /**
     * Returns a string representation of the object.
     *
     *   2x, 50y etc
     *
     * @return  a string representation of the object.
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if ( getMultiplier() != 1)
        {
            sb.append(new Constant(getMultiplier()).toString());
        }
        sb.append(name);
        return sb.toString();
    }
}
