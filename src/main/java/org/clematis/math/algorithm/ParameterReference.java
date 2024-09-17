// Created: 16.07.2004 T 12:49:06
package org.clematis.math.algorithm;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Class contains an instance of {@link Parameter} and serves as a reference to it.
 */
@Getter
@Setter
public class ParameterReference implements IExpressionItem {

    Parameter origin;

    public ParameterReference() {
    }

    public ParameterReference(Parameter origin) {
        this.origin = origin;
    }

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        IExpressionItem result = null;
        if (origin != null) {
            if (origin.getCurrentResult() == null) {
                origin.calculate(origin.getContainer(), 0);
            }
            result = origin.getCurrentResult();
        }
        return result;
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter
     *                          and functions provider
     * @return expression item instance
     */
    public IExpressionItem calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        return calculate();
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
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
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
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
    public boolean aKindOf(IExpressionItem item) {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().aKindOf(item);
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
    public boolean equals(IExpressionItem item) {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().equals(item);
        }
        return false;
    }

    /**
     * Return constant coefficient
     *
     * @return constant coefficient
     */
    public double getMultiplier() {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().getMultiplier();
        }
        return 1.0;
    }

    /**
     * Sets constant multiplier, the expression will be multiplied by this number in calculation
     *
     * @param multiplier for the expression
     */
    public void setMultiplier(double multiplier) {
        if (origin.getExpressionRoot() != null) {
            origin.getExpressionRoot().setMultiplier(multiplier);
        }
    }

    /**
     * Provides mathml formatted element, representing expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        if (origin.getExpressionRoot() != null) {
            return origin.getExpressionRoot().toMathML();
        }
        return null;
    }

    /**
     * Add another argument to this expression item
     *
     * @param argument to this expression item
     */
    @Override
    public void addArgument(IExpressionItem argument) {

    }

    /**
     * Sets another argument to required position
     *
     * @param argument to add
     * @param i        - number of position to add, zero based
     */
    @Override
    public void setArgument(IExpressionItem argument, int i) {

    }
}
