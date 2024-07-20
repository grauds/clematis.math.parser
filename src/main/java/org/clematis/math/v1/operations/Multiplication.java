// Created: Feb 14, 2003 T 11:36:06 AM
package org.clematis.math.v1.operations;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.iExpressionItem;
import org.jdom2.Element;

/**
 * Multiplication operation
 */
public class Multiplication extends aOperation {
    /**
     * Common constructor. Note, that constructor does
     * not simplify the expression.
     *
     * @param in_operand1 first argument
     * @param in_operand2 second argument
     */
    public Multiplication(iExpressionItem in_operand1, iExpressionItem in_operand2) {
        super(in_operand1, in_operand2);
        /**
         * Set multiplier
         */
        setMultiplier(getMultiplier() * getOperand1().getMultiplier()
            * getOperand2().getMultiplier());
        /**
         * Set all other multipliers to 1.
         */
        getOperand1().setMultiplier(1);
        getOperand2().setMultiplier(1);
    }

    /**
     * Try to make a simplier expression or just multiply coefficients.
     *
     * @return new or modified expression.
     */
    public iExpressionItem calculate() throws AlgorithmException {
        /**
         * Try to make multiplication
         */
        iExpressionItem a = getOperand1().calculate();
        iExpressionItem b = getOperand2().calculate();
        iExpressionItem result = a.multiply(b);
        /**
         * We failed
         */
        if (result == null) {
            result = new Multiplication(a, b);
        }
        /**
         * Take into account multiplier of this operation
         */
        result.setMultiplier(this.getMultiplier() * result.getMultiplier());
        return result;
    }

    /**
     * Adds another expression to this multiplication.
     *
     * @param item expression item to add
     * @return new expression item as a product of addition
     */
    public iExpressionItem add(iExpressionItem item) {
        /**
         * Case: 5xy + 7yx. We get 12xy.
         */
        if (this.aKindOf(item)) {
            Multiplication m = (Multiplication) item;
            setMultiplier(getMultiplier() + m.getMultiplier());
            return this;
        }
        /**
         * All other cases like: 5xy + 7yz.
         */
        else {
            return null;
        }
    }

    /**
     * This method holds all cases of multiplication.
     *
     * @param item expression item to multiply
     * @return new expression item as a product of multiplication
     */
    public iExpressionItem multiply(iExpressionItem item) throws AlgorithmException {
        /**
         * Case C * xy
         */
        if (item instanceof Constant) {
            this.setMultiplier(((Constant) item).getNumber() * getMultiplier());
            return this;
        }
        /**
         * Case: 7xy * 5yx. We get 35 * (x^2) * (y^2)
         */
        else if (this.aKindOf(item)) {
            Multiplication m = (Multiplication) item;
            setMultiplier(getMultiplier() * m.getMultiplier());

            setOperand1(getOperand1().multiply(m.getOperand1()));
            setOperand2(getOperand2().multiply(m.getOperand2()));

            if (getOperand1() == null || getOperand2() == null) {
                setOperand1(getOperand1().multiply(m.getOperand2()));
                setOperand2(getOperand2().multiply(m.getOperand1()));
            }

            if (getOperand1() == null || getOperand2() == null) {
                return null;
            } else {
                return this;
            }
        }
        /**
         * All other cases like: 5xy * 7yz.
         */
        else {
            return null;
        }
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean aKindOf(iExpressionItem item) {
        if (item instanceof Multiplication mitem) {
            iExpressionItem op1 = mitem.getOperand1();
            iExpressionItem op2 = mitem.getOperand2();
            return op1.aKindOf(getOperand1()) && op2.aKindOf(getOperand2())
                ||
                op2.aKindOf(getOperand1()) && op1.aKindOf(getOperand2());
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
    public boolean equals(iExpressionItem item) {
        if (item instanceof Multiplication mitem) {
            iExpressionItem op1 = mitem.getOperand1();
            iExpressionItem op2 = mitem.getOperand2();
            if (op1.equals(getOperand1()) && op2.equals(getOperand2())
                ||
                op2.equals(getOperand1()) && op1.equals(getOperand2())) {
                return getMultiplier() == item.getMultiplier();
            }
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
        Element apply = new Element("apply", iExpressionItem.NS_MATH);

        Element times = new Element("times", iExpressionItem.NS_MATH);
        apply.addContent(times);
        Element cn = new Element("cn", iExpressionItem.NS_MATH);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element("apply", iExpressionItem.NS_MATH);
        apply2.addContent(new Element("times", iExpressionItem.NS_MATH));
        apply2.addContent(getOperand1().toMathML());
        apply2.addContent(getOperand2().toMathML());

        apply.addContent(apply2);

        return apply;
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * 2 xy
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getMultiplier() != 1) {
            sb.append(new Constant(getMultiplier()));
            sb.append("*");
        }
        sb.append(getOperand1().toString());
        sb.append("*");
        sb.append(getOperand2().toString());
        return sb.toString();
    }
}
