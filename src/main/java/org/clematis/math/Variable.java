// $Id: Variable.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 13, 2003 T 6:20:41 PM

package org.clematis.math;

import java.io.Serializable;
import java.util.List;

import org.clematis.math.algorithm.IParameterProvider;
import org.clematis.math.algorithm.IVariableProvider;
import org.clematis.math.operations.Multiplication;
import org.clematis.math.operations.Power;
import org.clematis.math.utils.MathUtils;
import org.clematis.math.utils.StringUtils;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Representation of x or y variables in formulas.
 *
 * @version 1.0
 */
@Getter
public class Variable implements IExpressionItem, Serializable {

    /**
     * Variable name.
     */
    private String name;
    /**
     * Variable multiplier.
     */
    @Setter
    private double multiplier = 1.0;

    /**
     * The assigned value to this variable.
     */
    private AbstractConstant currentResult = null;

    public Variable() {

    }
    /**
     * Constructor.
     *
     * @param name String containing the variable name.
     */
    public Variable(String name) {
        this.name = name;
    }

    /**
     * Create either variable or multiplication, it depends on
     * the name in arguments.
     *
     * @param name for future variable
     * @return variable or multiplication
     */
    public static IExpressionItem create(String name) {
        return Variable.create(null, name);
    }

    /**
     * Create either variable or multiplication, it depends on
     * the name in arguments.
     *
     * @param name for future variable
     * @return variable or multiplication
     */
    public static IExpressionItem create(IVariableProvider varProvider, String name) {
        List<String> arr = StringUtils.tokenizeReg(name, "[:digit:]+|[:alpha:]+[:alnum:]*", false);

        if (arr.size() > 1) {
            String arg = arr.get(0);
            IExpressionItem argItem;

            // number
            if (MathUtils.isDigit(arg.charAt(0))) {
                argItem = new Constant(Double.parseDouble(arg));
            } else {
                // variable
                argItem = new Variable(arg);
            }

            for (int i = 1; i < arr.size(); i++) {
                IExpressionItem argItem1;
                String arg1 = arr.get(i);

                // number
                if (!arg1.isEmpty() && MathUtils.isDigit(arg1.charAt(0))) {
                    try {
                        argItem1 = new Constant(Double.parseDouble(arg1));
                    } catch (NumberFormatException ex) {
                        // variable
                        argItem1 = applyVariableProvider(varProvider, arg1);
                    }
                } else {
                    // variable
                    argItem1 = applyVariableProvider(varProvider, arg1);
                }

                argItem = new Multiplication(argItem, argItem1);
            }

            return argItem;
        } else {
            return applyVariableProvider(varProvider, name);
        }
    }

    /**
     * Apply variable provider in process of variable creation
     *
     * @param varProvider provider of variables
     * @param name        of variable
     * @return either variable or constant
     */
    private static IExpressionItem applyVariableProvider(IVariableProvider varProvider, String name) {
        if (varProvider != null) {
            AbstractConstant constant = varProvider.getVariableConstant(name);
            if (constant != null) {
                return constant.copy();
            }
        }
        return new Variable(name);
    }

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() {
        if (getCurrentResult() == null) {
            return this;
        } else {
            return getCurrentResult().copy();
        }
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
        if (getCurrentResult() == null) {
            return this;
        } else {
            return getCurrentResult().copy();
        }
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public IExpressionItem add(IExpressionItem item) {
        if (item instanceof Variable v) {
            if (v.getName().equals(name)) {
                multiplier += v.getMultiplier();
                return this;
            }
        }
        return null;
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element apply = new Element("apply", IExpressionItem.NS_MATH);

        Element times = new Element("times", IExpressionItem.NS_MATH);
        apply.addContent(times);

        Element cn = new Element("cn", IExpressionItem.NS_MATH);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element ci = new Element("ci", IExpressionItem.NS_MATH);
        ci.setAttribute("type", "real");
        ci.setText(this.getName());
        apply.addContent(ci);

        return apply;
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

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
        IExpressionItem result = null;

        if (item instanceof Constant c) {

            multiplier *= c.getNumber();
            result = this;
        } else if (item instanceof Variable v && this.aKindOf(v)) {

            Power p = new Power(this, new Constant(2));
            p.setMultiplier(getMultiplier() * v.getMultiplier());
            result = p;
        } else if (item instanceof Power p) {

            /*
             * Compare roots
             */
            if (this.aKindOf(p.getOperand1())) {
                p.changeExponent("1");
                p.setMultiplier(getMultiplier() * p.getMultiplier());
                result = p.calculate();
            }
        }
        return result;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean aKindOf(IExpressionItem item) {
        if (item instanceof Variable) {
            String itemName = ((Variable) item).getName();
            return itemName.equalsIgnoreCase(this.getName());
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
        if (item instanceof Variable) {
            String itemName = ((Variable) item).getName();
            return itemName.equalsIgnoreCase(this.getName())
                   && (getMultiplier() == item.getMultiplier());
        }
        return false;
    }

    /**
     * Sets current result violently
     *
     * @param currentResult
     */
    public void setCurrentResult(AbstractConstant currentResult) {
        if (currentResult != null) {
            this.currentResult = currentResult.copy();
        } else {
            this.currentResult = null;
        }
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * 2x, 50y etc
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getMultiplier() != 1) {
            sb.append(new Constant(getMultiplier()));
        }
        sb.append(name);
        return sb.toString();
    }
}
