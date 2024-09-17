package org.clematis.math.operations;


import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.algorithm.IParameterProvider;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is for expression which holds one root expression item.
 * Some parsers may want this class as a starting point for parsing
 * equations, vector expressions or any other types of expressions.
 */
@Getter
@Setter
public class Expression implements IExpressionItem {

    private IExpressionItem root = null;

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    @Override
    public IExpressionItem calculate() throws AlgorithmException {
        if (root != null) {
            return root.calculate();
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
    @Override
    public IExpressionItem calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        if (root != null) {
            return root.calculate(parameterProvider);
        }
        return null;
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    @Override
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
        if (root != null) {
            return root.add(item);
        }
        return null;
    }

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    @Override
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
        if (root != null) {
            return root.multiply(item);
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
    @Override
    public boolean aKindOf(IExpressionItem item) {
        if (root != null) {
            return root.aKindOf(item);
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
    @Override
    public boolean equals(IExpressionItem item) {
        if (root != null) {
            return root.equals(item);
        }
        return false;
    }

    /**
     * Return constant coefficient
     *
     * @return constant coefficient
     */
    @Override
    public double getMultiplier() {
        return 1;
    }

    /**
     * Sets constant multiplier
     *
     * @param multiplier for expression item
     */
    @Override
    public void setMultiplier(double multiplier) {

    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    @Override
    public Element toMathML() {
        if (root != null) {
            return root.toMathML();
        }
        return null;
    }

    /**
     * This expression only has one argument which is replaced with
     * every method call
     *
     * @param argument to this expression
     */
    @Override
    public void addArgument(IExpressionItem argument) {
        this.root = argument;
    }

    /**
     * Sets another argument to one available position
     *
     * @param argument to add
     * @param i        - number of position to add, zero based, is not used
     */
    @Override
    public void setArgument(IExpressionItem argument, int i) {
        this.root = argument;
    }
}
