// $Id: iExpressionItem.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 14, 2003 T 10:18:05 AM

package org.clematis.math.v1;

import org.clematis.math.v1.algorithm.iParameterProvider;
import org.jdom2.Element;
import org.jdom2.Namespace;

/**
 * Abstract expression item, that can add other expression items
 * to itself, multiply or calculate subtree of expression items.
 */
public interface iExpressionItem {
    /**
     * URI for the MATH XML namspace.
     */
    String NS_MATH_URI = "http://www.w3.org/1998/Math/MathML";
    /**
     * UNI XML namespace.
     */
    Namespace NS_MATH = Namespace.getNamespace("math", NS_MATH_URI);

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    iExpressionItem calculate() throws AlgorithmException;

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter
     *                          and functions provider
     * @return expression item instance
     */
    iExpressionItem calculate(iParameterProvider parameterProvider) throws AlgorithmException;

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    iExpressionItem add(iExpressionItem item) throws AlgorithmException;

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    iExpressionItem multiply(iExpressionItem item) throws AlgorithmException;

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    boolean aKindOf(iExpressionItem item);

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    boolean equals(iExpressionItem item);

    /**
     * Return constant coefficient
     *
     * @return constant coefficient
     */
    double getMultiplier();

    /**
     * Sets constant multiplier
     *
     * @param multiplier
     */
    void setMultiplier(double multiplier);

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    Element toMathML();
}
