// Created: 16.07.2004 T 12:49:06
package org.clematis.math.v2.algorithm;

import org.clematis.math.v2.AlgorithmException;
import org.jdom2.Element;

import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;

/**
 * Reference to parameter
 */
public class ParameterReference extends SimpleNode {
    Parameter origin = null;

    public ParameterReference(Parameter origin) {
        super(0);
        this.origin = origin;
    }

    public ParameterReference(int i) {
        super(i);
    }

    public Parameter getOrigin() {
        return origin;
    }

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().calculate(parameterProvider);
        }
        return null;
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public Node add(Node item) throws AlgorithmException {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().add(item);
        }
        return null;
    }

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public Node multiply(Node item) throws AlgorithmException {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().multiply(item);
        }
        return null;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean isSimilar(Node item) {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().isSimilar(item);
        }
        return false;
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(Node item) {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().equals(item);
        }
        return false;
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().toMathML();
        }
        return null;
    }
}
