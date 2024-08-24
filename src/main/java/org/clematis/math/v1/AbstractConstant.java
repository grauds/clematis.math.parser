// $Id: AbstractConstant.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Mar 19, 2003 T 5:51:50 PM

package org.clematis.math.v1;

import java.io.Serializable;

import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.algorithm.iParameterProvider;
import org.jdom2.Element;

/**
 * Abstract constant, may be string or numeric
 */
public abstract class AbstractConstant extends SimpleValue implements iExpressionItem, Serializable {
    /**
     * This flag allows application of significant digits.
     * If set to 1, answers will be cut to required number
     * of significant digits.
     */
    protected boolean sdEnable = false;
    /**
     * Number of sig digits
     */
    protected int sdNumber = 0;
    /**
     * Link to embracing parameter with settings from algorithm
     */
    protected Parameter parameter = null;

    /**
     * Implementation of <code>calculate()</code> method of the
     * <code>iExpressionItem</code> interface.
     *
     * @return <code>this</code> reference.
     */
    public iExpressionItem calculate() {
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
    public iExpressionItem calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        return this;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public abstract boolean aKindOf(iExpressionItem item);

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public abstract boolean equals(iExpressionItem item);

    /**
     * Sets constant multiplier
     *
     * @param multiplier
     */
    public void setMultiplier(double multiplier) {
    }

    /**
     * Copies the constant.
     *
     * @return instance of some class extending <code>AbstractConstant</code> class.
     */
    public abstract AbstractConstant copy();

    /**
     * Factory method to create instance of <code>Constant</code> class.
     * If value starts with '"' then instance of <code>StringConstant</code> is created.
     * Otherwise instance of <code>NumberConstant</code> is created.
     *
     * @param value constant value.
     * @return instance of <code>StringConstant</code> or <code>NumberConstant</code> classes.
     */
    public static AbstractConstant create(String value) {
        if (value != null) {
            if (value.trim().equals("") || value.charAt(0) == '"') {
                return new StringConstant(value);
            }
            return new Constant(value);
        }
        return null;
    }

    /**
     * Factory method to create instance of <code>Constant</code> class.
     * If value starts with '"' then instance of <code>StringConstant</code> is created.
     * Otherwise instance of <code>NumberConstant</code> is created.
     *
     * @param element containing constant value.
     * @return instance of <code>StringConstant</code> or <code>NumberConstant</code> classes.
     */
    public static AbstractConstant create(Element element) {
        if (element != null) {
            String type = element.getAttributeValue("type");
            if ("number".equals(type)) {
                return new Constant(element);
            } else if ("string".equals(type)) {
                return new StringConstant(element);
            }
        }
        return new Constant(element);
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public abstract Element toXML();

    /**
     * Sets the number of significant digits.
     *
     * @param sdNumber the number of significant digits.
     */
    public void setSdNumber(int sdNumber) {
        this.sdNumber = sdNumber;
    }

    /**
     * Returns the number of significant digits.
     *
     * @return the number of significant digits.
     */
    public int getSdNumber() {
        if (parameter != null && parameter.isCorrectAnswer()) {
            return parameter.getSdNumber();
        } else {
            return this.sdNumber;
        }
    }

    /**
     * Set enable significant digits flag
     *
     * @param sdEnable 1 - enable, 0 - disable
     */
    public final void setSdEnable(boolean sdEnable) {
        this.sdEnable = sdEnable;
    }

    /**
     * Returns true if this constant is zero
     *
     * @return true if this constant is zero
     */
    public boolean isZero() {
        return false;
    }

    /**
     * Returns enable significant digits flag
     *
     * @return 1 - enable, 0 - disable, -1 - not applicable
     */
    public boolean getSdEnable() {
        if (parameter != null && parameter.isCorrectAnswer()) {
            return parameter.getSdEnable();
        } else {
            return this.sdEnable;
        }
    }

    /**
     * Set parameter for the constant
     *
     * @param parameter for the constant
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
