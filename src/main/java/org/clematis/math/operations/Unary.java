// Created: 11.01.2007 T 11:25:41
package org.clematis.math.operations;

import static org.clematis.math.XMLConstants.APPLY_ELEMENT_NAME;
import static org.clematis.math.XMLConstants.TIMES_ELEMENT_NAME;
import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Unary operation
 */
@Getter
@Setter
public class Unary extends AbstractOperation {
    /**
     * Unary minus operation
     */
    public static final int MINUS = 1;
    /**
     * Unary plus operation
     */
    public static final int PLUS = 2;
    /**
     * Type of the instance
     */
    private int kind;

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    @Override
    public IExpressionItem calculate() throws AlgorithmException {

        IExpressionItem a = getOperand1().calculate();
        /*
         * Since there can be only one argument, negate it or leave as it is
         */
        if (a != null && kind == Unary.MINUS) {
            return a.multiply(new Constant("-1"));
        } else {
            return a;
        }
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    @Override
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
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
        if (item instanceof Unary unary) {
            IExpressionItem op1 = unary.getOperand1();
            return op1.aKindOf(getOperand1()) && getKind() == unary.getKind();
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
        if (item instanceof Unary uitem) {
            IExpressionItem op1 = uitem.getOperand1();
            if (op1.equals(getOperand1()) && getKind() == uitem.getKind()) {
                return getMultiplier() == item.getMultiplier();
            }
        }
        return false;
    }

    public Element toMathML() {
        Element apply = new Element(APPLY_ELEMENT_NAME, IExpressionItem.NS_MATH);
        Element times = new Element(TIMES_ELEMENT_NAME, IExpressionItem.NS_MATH);
        apply.addContent(times);

        Element cn = new Element("cn", IExpressionItem.NS_MATH);
        cn.setText(((getKind() == Unary.MINUS) ? "-" : "") + getMultiplier());
        apply.addContent(cn);

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
}
