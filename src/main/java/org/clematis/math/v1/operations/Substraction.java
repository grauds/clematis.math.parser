// Created: 11.01.2007 T 11:06:56
package org.clematis.math.v1.operations;

import java.util.ArrayList;
import static org.clematis.math.XMLConstants.APPLY_ELEMENT_NAME;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.Constant;
import org.jdom2.Element;

/**
 * Substraction operation
 */
public class Substraction extends Addition {

    public Substraction() {
    }

    /**
     * Public constructor for addition operation.
     *
     * @param operand array
     */
    public Substraction(IExpressionItem... operand) {
        super(operand);
    }

    /**
     * Provides mathml formatted element, representing expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element apply = new Element(APPLY_ELEMENT_NAME);

        Element times = new Element("times");
        apply.addContent(times);
        Element cn = new Element("cn");
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element(APPLY_ELEMENT_NAME);
        apply2.addContent(new Element("minus"));
        apply2.addContent(getOperand1().toMathML());
        apply2.addContent(getOperand2().toMathML());

        apply.addContent(apply2);

        return apply;
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * x - y or 2 * (x - y)
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getMultiplier() != 1) {
            sb.append(new Constant(getMultiplier()));
            sb.append("*");
            sb.append("(");
        }

        sb.append(getOperand1().toString());
        sb.append("-");
        sb.append(getOperand2().toString());

        if (getMultiplier() != 1) {
            sb.append(")");
        }
        return sb.toString();
    }
}