// Created: Feb 14, 2003 T 11:36:06 AM
package org.clematis.math.v1.operations;

import static org.clematis.math.XMLConstants.APPLY_ELEMENT_NAME;
import static org.clematis.math.XMLConstants.TIMES_ELEMENT_NAME;
import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.Constant;
import org.jdom2.Element;

/**
 * Multiplication operation
 */
public class Multiplication extends AbstractOperation {

    public static final String MULTIPLY_SIGN = "*";

    /**
     * Common constructor. Note, that constructor does
     * not simplify the expression.
     *
     * @param operand an array of operands
     */
    public Multiplication(IExpressionItem... operand) {
        super(operand);
        /*
         * Optimize multipliers
         */
        for (IExpressionItem op : operand) {
            setMultiplier(getMultiplier() * op.getMultiplier());
            op.setMultiplier(1);
        }
    }

    /**
     * Try to make a simplier expression or just multiply coefficients.
     *
     * @return new or modified expression.
     */
    public IExpressionItem calculate() throws AlgorithmException {
        IExpressionItem a = null;
        for (IExpressionItem op : this.getOperands()) {
            if (op != null) {
                IExpressionItem b = op.calculate();
                a = a != null ? a.multiply(b) : b;
                if (a == null) {
                    a = new Multiplication(a, b);
                    break;
                }
            }
        }
        if (a != null) {
            a.setMultiplier(this.getMultiplier() * a.getMultiplier());
        }
        return a;
    }

    /**
     * Adds another expression to this multiplication.
     *
     * @param item expression item to add
     * @return new expression item as a product of addition
     */
    public IExpressionItem add(IExpressionItem item) {

        if (this.aKindOf(item)) {
            /*
             * Case: 5xy + 7yx. We get 12xy.
             */
            Multiplication m = (Multiplication) item;
            setMultiplier(getMultiplier() + m.getMultiplier());
            return this;
        } else {
            /*
             * All other cases like: 5xy + 7yz.
             */
            return null;
        }
    }

    /**
     * This method holds all cases of multiplication.
     *
     * @param item expression item to multiply
     * @return new expression item as a product of multiplication
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {

        if (item instanceof Constant) {
            /*
             * Case C * xy
             */
            this.setMultiplier(((Constant) item).getNumber() * getMultiplier());
            return this;
        } else if (this.aKindOf(item)) {
            /*
             * Case: 7xy * 5yx. We get 35 * (x^2) * (y^2)
             */
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
        } else {
            /*
             * All other cases like: 5xy * 7yz.
             */
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
    public boolean aKindOf(IExpressionItem item) {
        if (item instanceof Multiplication multiplication) {

            IExpressionItem op1 = multiplication.getOperand1();
            IExpressionItem op2 = multiplication.getOperand2();

            return op1.aKindOf(getOperand1()) && op2.aKindOf(getOperand2())
                || op2.aKindOf(getOperand1()) && op1.aKindOf(getOperand2());
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
        if (item instanceof Multiplication mitem) {

            IExpressionItem op1 = mitem.getOperand1();
            IExpressionItem op2 = mitem.getOperand2();

            if (op1.equals(getOperand1()) && op2.equals(getOperand2())
                || op2.equals(getOperand1()) && op1.equals(getOperand2())) {
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

        Element apply = new Element(APPLY_ELEMENT_NAME, IExpressionItem.NS_MATH);
        Element times = new Element(TIMES_ELEMENT_NAME, IExpressionItem.NS_MATH);
        apply.addContent(times);

        Element cn = new Element("cn", IExpressionItem.NS_MATH);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element(APPLY_ELEMENT_NAME, IExpressionItem.NS_MATH);
        apply2.addContent(new Element(TIMES_ELEMENT_NAME, IExpressionItem.NS_MATH));
        apply2.addContent(getOperand1().toMathML());
        apply2.addContent(getOperand2().toMathML());

        apply.addContent(apply2);

        return apply;
    }

    /**
     * Add another argument to this expression item
     *
     * @param argument to this expression item
     */
    @Override
    public void addArgument(IExpressionItem argument) {
        this.addOperand(argument);
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
            sb.append("(");
        }

        for (int i = 0; i < getOperands().size(); i++) {

            if (getOperands().get(i) != null) {
                if (i > 0) {
                    sb.append(MULTIPLY_SIGN);
                }
                sb.append(getOperands().get(i).toString());
            }

        }

        if (getMultiplier() != 1) {
            sb.append(")");
        }
        return sb.toString();
    }
}
