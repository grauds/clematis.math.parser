// Created: Feb 14, 2003 T 11:29:25 AM
package org.clematis.math.v1.operations;

import static org.clematis.math.XMLConstants.APPLY_ELEMENT_NAME;
import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.Constant;
import org.jdom2.Element;

/**
 * Addition operation.
 */
public class Addition extends aOperation {
    /**
     * Public constructor for addition operation.
     *
     * @param operand to add to addition
     */
    public Addition(IExpressionItem... operand) {
        super(operand);
    }

    public IExpressionItem calculate() throws AlgorithmException {

        IExpressionItem a = getOperand1().calculate();
        for (IExpressionItem op : this.getOperands()) {
            IExpressionItem b = op.calculate();
            a = a.add(b);
            if (a == null) {
                a = new Addition(a, b);
                break;
            }
        }
        return a.multiply(new Constant(getMultiplier()));
    }

    /**
     * Adds new expression item to this sum. If this expression item
     * cannot be added to any of sum operands, we return new sum with
     * this sum as operand and item as second operand.
     *
     * @param item
     * @return this or new addition
     */
    @SuppressWarnings({"checkstyle:NestedIfDepth", "checkstyle:ReturnCount"})
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
        if (item instanceof Addition a_item) {

            IExpressionItem res1 = getOperand1().add(a_item.getOperand1());
            IExpressionItem res2;

            if (res1 == null) {
                res1 = getOperand2().add(a_item.getOperand1());
                res2 = getOperand1().add(a_item.getOperand2());
            } else {
                res2 = getOperand2().add(a_item.getOperand2());
            }
            if (res1 != null && res2 != null) {
                Addition retvalue = new Addition(res1, res2);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            } else {
                return null;
            }
        } else {
            IExpressionItem result = getOperand1().add(item);

            if (result == null) {
                result = getOperand2().add(item);
                if (result == null) {
                    return null;
                } else {
                    Addition retvalue = new Addition(getOperand1(), result);
                    retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                    return retvalue;
                }
            } else {
                Addition retvalue = new Addition(result, getOperand2());
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }
        }
    }

    /**
     * Multiplies both operands by item, if any multiplication fails,
     * we should produce a simple multiplication without any removal of
     * sum braskets.
     *
     * @param item to multiply
     * @return addition or multiplication.
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
        if (item instanceof Constant) {
            this.setMultiplier(((Constant) item).getNumber() * getMultiplier());
            return this;
        } else if (item instanceof Addition) {
            if (item.equals(this)) {
                return new Power(this, new Constant("2"));
            }
        } else if (item instanceof Power p) {
            if (p.getOperand1().equals(this)) {
                p.changeExponent("1");
                return p.calculate();
            }
        }
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
        if (item instanceof Addition add_item) {
            IExpressionItem op1 = add_item.getOperand1();
            IExpressionItem op2 = add_item.getOperand2();
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
    public boolean equals(IExpressionItem item) {
        if (item instanceof Addition add_item) {
            IExpressionItem op1 = add_item.getOperand1();
            IExpressionItem op2 = add_item.getOperand2();
            if ((op1.equals(getOperand1()) && op2.equals(getOperand2()))
                ||
                (op2.equals(getOperand1()) && op1.equals(getOperand2()))) {
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
        Element apply = new Element(APPLY_ELEMENT_NAME);

        Element times = new Element("times");
        apply.addContent(times);
        Element cn = new Element("cn");
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element(APPLY_ELEMENT_NAME);
        apply2.addContent(new Element("plus"));
        apply2.addContent(getOperand1().toMathML());
        apply2.addContent(getOperand2().toMathML());

        apply.addContent(apply2);

        return apply;
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * x + y or 2 * (x + y)
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
        sb.append("+");
        sb.append(getOperand2().toString());

        if (getMultiplier() != 1) {
            sb.append(")");
        }
        return sb.toString();
    }
}
