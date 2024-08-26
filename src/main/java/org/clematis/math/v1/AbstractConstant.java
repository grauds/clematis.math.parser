// $Id: AbstractConstant.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Mar 19, 2003 T 5:51:50 PM

package org.clematis.math.v1;

import java.io.Serializable;

import org.clematis.math.v1.algorithm.IParameterProvider;
import org.clematis.math.v1.algorithm.Parameter;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract constant, may be string or numeric
 */
@Getter
@Setter
public abstract class AbstractConstant extends SimpleValue implements IExpressionItem, Serializable {
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
    public IExpressionItem calculate() {
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
    public IExpressionItem calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        return this;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public abstract boolean aKindOf(IExpressionItem item);

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public abstract boolean equals(IExpressionItem item);

    /**
     * Sets constant multiplier
     *
     * @param multiplier for the result of this calculation
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
        AbstractConstant c = null;
        if (value != null) {
            if (value.trim().isEmpty() || value.charAt(0) == '"') {
                c = new StringConstant(value);
            } else {
                c = new Constant(value);
            }
        }
        return c;
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
        AbstractConstant c = new Constant(element);

        if (element != null) {
            String type = element.getAttributeValue("type");
            if ("number".equals(type)) {
                c = new Constant(element);
            } else if ("string".equals(type)) {
                c = new StringConstant(element);
            }
        }

        return c;
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public abstract Element toXML();

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
}
