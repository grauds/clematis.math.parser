// Created: Mar 19, 2003 T 4:37:10 PM
package org.clematis.math.v1;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.io.OutputFormatSettings;
import org.jdom2.CDATA;
import org.jdom2.Element;

/**
 * Plain string constant
 */
public class StringConstant extends AbstractConstant {

    public static final String APOS = "\"";

    public StringConstant() {

    }

    /**
     * Constructor.
     *
     * @param value constant value.
     */
    public StringConstant(String value) {
        this.value = value;
    }

    /**
     * Constructor.
     *
     * @param element xml representation of constant
     */
    public StringConstant(Element element) {
        if (element != null) {
            this.value = element.getText();
        }
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
     * Gets constant value.
     *
     * @param fs
     * @return constant value.
     */
    public String getValue(OutputFormatSettings fs) {
        String returnString = this.value;
        if (returnString.startsWith(APOS)) {
            returnString = returnString.substring(1);
        }
        if (returnString.endsWith(APOS)) {
            returnString = returnString.substring(0, returnString.length() - 1);
        }
        return returnString;
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
        return null;
    }

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
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
        return (item instanceof StringConstant);
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(IExpressionItem item) {
        if (item instanceof StringConstant c) {
            return this.getValue(null).equals(c.getValue(null));
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
        Element ci = new Element("ci", NS_MATH);
        ci.setText(getValue(null));
        return ci;
    }

    /**
     * Copies the constant.
     *
     * @return instance of <code>StringConstant</code> class with the same value.
     */
    public AbstractConstant copy() {
        return new StringConstant(value);
    }

    /**
     * Returns textual representation of the <code>StringConstant</code> value.
     *
     * @return <code>String</code> containing the value of the constant.
     */
    public String toString() {
        return this.value;
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public Element toXML() {
        Element element = new Element("constant");
        element.setAttribute("type", "string");
        element.addContent(new CDATA(value));
        return element;
    }

    /**
     * Sets the number of significant digits.
     * Not used for strings.
     *
     * @param sdNumber the number of significant digits.
     */
    public void setSdNumber(int sdNumber) {}

    /**
     * Set enable significant digits flag.
     * Not used for strings.
     *
     * @param flag 1 - enable, 0 - disable, -1 - not applicable
     */
    public void setSdEnable(int flag) {}
}
