// Created: Feb 14, 2003 T 12:27:38 PM
package org.clematis.math.v1.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.XMLConstants;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.FunctionFactory;
import org.clematis.math.v1.IFunction;
import org.clematis.math.v1.algorithm.IParameterProvider;
import org.clematis.math.v1.operations.Power;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract constant function.
 */
@Setter
@Getter
@SuppressWarnings("checkstyle:TypeName")
public abstract class AbstractFunction implements IFunction, Serializable {

    /**
     * The list of arguments
     */
    protected ArrayList<IExpressionItem> arguments = new ArrayList<>();

    /**
     * Function factory
     */
    protected FunctionFactory functionFactory = new FunctionFactory();

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
     *
     * @param item expression item as a parameter
     */
    public void addArgument(IExpressionItem item) {
        arguments.add(item);
    }

    /**
     * Add arguments to a function
     *
     * @param arguments expression items as a parameter
     */
    public void addArguments(ArrayList<IExpressionItem> arguments) {
        this.arguments.addAll(arguments);
    }

    /**
     * Removes all arguments from the function
     */
    public void removeArguments() {
        this.arguments.clear();
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {

        Element apply = new Element(XMLConstants.APPLY_ELEMENT_NAME/*, Constants.NS_MATH*/);

        Element times = new Element("times"/*, Constants.NS_MATH*/);
        apply.addContent(times);
        Element cn = new Element("cn"/*, Constants.NS_MATH*/);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element(XMLConstants.APPLY_ELEMENT_NAME/*, Constants.NS_MATH*/);

        Element function = new Element(signature);
        apply2.addContent(function);
        for (IExpressionItem iExpressionItem : arguments) {
            Element argument = iExpressionItem.toMathML();
            apply2.addContent(argument);
        }

        apply.addContent(apply2);

        return apply;
    }

    public boolean equals(IExpressionItem item) {

        if (this == item) {
            return true;
        }

        if (!(item instanceof AbstractFunction aFunction)) {
            return false;
        }

        return Objects.equals(signature, aFunction.signature);
    }

    /**
     * Calculate a subtree of expression items with parameter and functions provider
     *
     * @param parameterProvider parameter
     *                          and functions provider
     * @return expression item instance
     */
    public IExpressionItem calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        return calculate();
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public boolean aKindOf(IExpressionItem item) {
        if (this == item) {
            return true;
        }
        if (item instanceof AbstractFunction) {
            return equals(item);
        } else if (item instanceof Power) {
            IExpressionItem expression = ((Power) item).getOperand1();
            if (expression instanceof AbstractFunction) {
                return equals(expression);
            }
        }
        return false;
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public IExpressionItem add(IExpressionItem item) {
        if (item instanceof AbstractFunction && item.aKindOf(this)) {
            this.setMultiplier(this.getMultiplier() + item.getMultiplier());
            return this;
        }
        return null;
    }

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
        if (item.aKindOf(this)) {
            if (item instanceof AbstractFunction) {
                /*
                 *  2 * sin ( x ) * 3 * sin ( x ) = 6 * sin ( x ) ^ 2
                 */
                Power power = new Power(this, new Constant(2));
                power.setMultiplier(this.getMultiplier() * item.getMultiplier());
                this.setMultiplier(1);
                return power;
            } else if (item instanceof Power power) {
                /*
                 *  2 * sin ( x ) * 3 * sin ( x ) ^ 3 = 6 * sin ( x ) ^ 4
                 */
                power.setMultiplier(this.getMultiplier() * item.getMultiplier());
                power.setPower(power.getPower().add(new Constant(1)));
                return power;
            }
        } else if (item instanceof Constant) {
            this.setMultiplier(this.getMultiplier() * ((Constant) item).getNumber());
            return this;
        }
        return null;
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * 2 * x ^ 2
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getMultiplier() != 1) {
            sb.append(new Constant(getMultiplier()));
            sb.append("*");
        }

        sb.append(signature);
        sb.append("(");
        for (int i = 0; i < arguments.size(); i++) {
            IExpressionItem arg = arguments.get(i);
            sb.append(arg.toString());
            if (i + 1 != arguments.size()) {
                sb.append(",");
            }
        }
        sb.append(")");

        return sb.toString();
    }
}
