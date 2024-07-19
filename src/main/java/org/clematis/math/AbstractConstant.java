// Created: Mar 19, 2003 T 5:51:50 PM
package org.clematis.math;

import java.io.Serializable;

import org.clematis.math.algorithm.Parameter;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.jdom2.Element;

/**
 * Abstract constant, may be string or numeric
 */
public abstract class AbstractConstant extends SimpleValue implements Serializable {
    /**
     * This flag controls application of significant digits (SD).
     * If set to true, the constant value will be cut to required number
     * of significant digits.
     */
    protected boolean sdEnable = false;
    /**
     * This flag forbids the application of SD in case of absolute tolerance or exact value
     */
    protected boolean sdApplicable = true;
    /**
     * Number of sig digits
     */
    protected int sdNumber = 0;
    /**
     * Link to embracing parameter with settings from algorithm
     */
    protected Parameter parameter = null;

    /**
     * Empty public constructor
     */
    protected AbstractConstant() {
    }

    protected AbstractConstant(int i) {
        super(i);
    }

    /**
     * Shallow copy constructor. This does not copy parent, parameter and children nodes.
     *
     * @param node to copy
     */
    protected AbstractConstant(AbstractConstant node) {
        super(node);
        this.sdNumber = node.sdNumber;
        this.sdApplicable = node.sdApplicable;
        this.sdEnable = node.sdEnable;
    }

    /**
     * Implementation of <code>calculate()</code> method of the
     * <code>Node</code> interface.
     *
     * @param parameterProvider
     * @return <code>this</code> reference.
     */
    public Node calculate(iParameterProvider parameterProvider) {
        return copy();
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public abstract boolean isSimilar(Node item);

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public abstract boolean equals(Node item);

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

    public boolean getSdEnable() {
        if (parameter != null && parameter.isCorrectAnswer()) {
            return parameter.getSdEnable();
        } else {
            return this.sdEnable;
        }
    }

    public boolean isSdApplicable() {
        if (parameter != null && parameter.isCorrectAnswer()) {
            return parameter.isSdApplicable();
        } else {
            return this.sdApplicable;
        }
    }

    public void setSdApplicable(boolean sdApplicable) {
        this.sdApplicable = sdApplicable;
    }

    /**
     * Set parameter for the constant
     *
     * @param parameter for the constant
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public abstract Element toXML();
}
