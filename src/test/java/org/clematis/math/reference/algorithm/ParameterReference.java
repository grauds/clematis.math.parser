// Created: 16.07.2004 T 12:49:06
package org.clematis.math.reference.algorithm;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.iExpressionItem;
import org.jdom2.Element;

/**
 */
public class ParameterReference implements iExpressionItem
{
    Parameter origin = null;

    public ParameterReference( Parameter origin )
    {
        this.origin = origin;
    }
    public Parameter getOrigin()
    {
        return origin;
    }
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().calculate();
        }
        return null;
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
     * Adds another expression item to this one.
     * @param item another expression item
     * @return result expression item
     */
    public iExpressionItem add( iExpressionItem item ) throws AlgorithmException
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().add( item );
        }
        return null;
    }
    /**
     * Multiplies this expression item by another one.
     * @param item another expression item
     * @return result expression item
     */
    public iExpressionItem multiply( iExpressionItem item ) throws AlgorithmException
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().multiply( item );
        }
        return null;
    }
    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean aKindOf( iExpressionItem item )
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().aKindOf( item );
        }
        return false;
    }
    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals( iExpressionItem item )
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().equals( item );
        }
        return false;
    }
    /**
     * Return constant coefficient
     * @return constant coefficient
     */
    public double getMultiplier()
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().getMultiplier();
        }
        return 1.0;
    }
    /**
     * Sets constant multiplier
     * @param multiplier
     */
    public void setMultiplier( double multiplier )
    {
        if( origin.getExpressionRoot() != null )
        {
            origin.getExpressionRoot().setMultiplier( multiplier );
        }
    }
    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML()
    {
        if( origin.getExpressionRoot() != null )
        {
            return origin.getExpressionRoot().toMathML();
        }
        return null;
    }
}
