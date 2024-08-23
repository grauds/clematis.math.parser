// Created: Mar 19, 2003 T 4:37:10 PM
package org.clematis.math.v2;

import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.clematis.math.v2.io.OutputFormatSettings;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;
import org.jdom2.CDATA;
import org.jdom2.Element;

/**
 * Plain string constant
 */
public class StringConstant extends AbstractConstant {
    public StringConstant(int i) {
        super(i);
    }

    /**
     * Shallow copy constructor. This does not copy parent, parameter and children nodes.
     *
     * @param node to copy
     */
    public StringConstant(StringConstant node) {
        super(node);
    }

    /**
     * Constructor.
     *
     * @param value constant value.
     */
    public StringConstant(String value) {
        this.token = value;
    }

    /**
     * Constructor.
     *
     * @param element xml representation of constant
     */
    public StringConstant(Element element) {
        if (element != null) {
            this.token = element.getText();
        }
    }

    /**
     * Calculate the value of this node.
     *
     * @param parameterProvider with precalculated values for some parameters
     * @return the calculated first argument or result of operation
     * @throws org.clematis.math.v2.algorithm.AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculate(iParameterProvider parameterProvider) {
        /**
         * Find parameter references
         */
        return new StringConstant(AlgorithmUtils.replaceParameters(getTokenName(), parameterProvider));
    }

    /**
     * Copies the constant.
     *
     * @return instance of <code>StringConstant</code> class with the same value.
     */
    public AbstractConstant copy() {
        return new StringConstant(this);
    }

    /**
     * Return constant coefficient
     *
     * @return constant coefficient
     */
    public double getMultiplier() {
        return Double.NaN;
    }

    /**
     * Sets the number of significant digits.
     * Not used for strings.
     *
     * @param sdNumber the number of significant digits.
     */
    public void setSdNumber(int sdNumber) {
    }

    /**
     * Set enable significant digits flag.
     * Not used for strings.
     *
     * @param flag 1 - enable, 0 - disable, -1 - not applicable
     */
    public void setSdEnable(int flag) {
    }

    /**
     * Gets constant value.
     *
     * @param fs
     * @return constant value.
     */
    public String getValue(OutputFormatSettings fs) {
        String returnString = this.token;
        if (returnString != null) {
            if (returnString.startsWith("\"")) {
                returnString = returnString.substring(1);
            }
            if (returnString.endsWith("\"")) {
                returnString = returnString.substring(0, returnString.length() - 1);
            }
        }
        return returnString;
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public Node add(Node item) throws AlgorithmException {
        // concatenate strings
        if (item instanceof StringConstant) {
            String result = this.getValue().concat(((StringConstant) item).getValue());
            return new StringConstant(result);
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
        return (item instanceof StringConstant);
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(Node item) {
        if (item instanceof StringConstant c) {
            return this.getValue().equals(c.getValue());
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
        Element ci = new Element("ci", Node.NS_MATH);
        ci.setText(getValue());
        return ci;
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public Element toXML() {
        Element element = new Element("constant");
        element.setAttribute("type", "string");
        element.addContent(new CDATA(token));
        return element;
    }

    /**
     * String value
     *
     * @return value
     */
    public String toString() {
        return getValue();
    }
}
