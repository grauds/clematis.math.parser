// Created: Feb 14, 2003 T 11:39:03 AM
package org.clematis.math.v2.operations;

import java.math.BigDecimal;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.Variable;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;
import org.jdom2.Element;

/**
 * Power operation
 */
public class Power extends aOperation {
    public Power(int i) {
        super(i);
    }

    public Power() {
    }

    /**
     * Common constructor.
     *
     * @param in_operand1 first argument
     * @param in_operand2 second argument
     */
    public Power(Node in_operand1, Node in_operand2) {
        super(in_operand1, in_operand2);
    }

    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        Node base = getOperand1().calculate(parameterProvider);
        Node exponent = getOperand2().calculate(parameterProvider);

        Constant c_exponent = AlgorithmUtils.getNumericArgument(exponent);

        if (base != null && c_exponent != null) {
            if (AlgorithmUtils.isGoodNumericArgument(base)) {
                try {
                    if (((int) c_exponent.getNumber()) == c_exponent.getNumber()) {
                        return AlgorithmUtils.getNumericArgument(base).pow(c_exponent);
                    }
                } catch (ArithmeticException ex) {
                }
                /**
                 * Actually calculate
                 */
                double power = Math.pow(AlgorithmUtils.getNumericArgument(base).getNumber(), c_exponent.getNumber());
                Constant c = new Constant(power);
                c.setMultiplier(getMultiplier());
                return c;
            } else if (c_exponent.getNumber() == 0) {
                return new Constant(getMultiplier());
            }
        }
        /**
         * Create new power expression
         */
        Power power = new Power(base, exponent);
        power.setMultiplier(power.getMultiplier() * getMultiplier());
        return power;
    }

    /**
     * Return power of this operation
     *
     * @return Node power
     */
    public Node getPower() {
        return getOperand2();
    }

    /**
     * Sets new exponent
     *
     * @param exponent to set
     */
    public void setPower(Node exponent) {
        setOperand2(exponent);
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean isSimilar(Node item) {
        /**
         * We can multiply power functions only if
         * bases are similar.
         * Addition can be made only
         * if bases and powers are equal.
         */
        if (item instanceof Variable) {
            return getOperand1().isSimilar(item);
        } else if (item instanceof Power) {
            return getOperand1().isSimilar(((Power) item).getOperand1());
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
    public boolean equals(Node item) {
        if (item instanceof Power p) {
            return (getOperand1().equals(p.getOperand1()) &&
                (getPower().equals(p.getPower())) &&
                (getMultiplier() == p.getMultiplier()));
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
        Element apply = new Element("apply", Node.NS_MATH);

        Element times = new Element("times", Node.NS_MATH);
        apply.addContent(times);
        Element cn = new Element("cn", Node.NS_MATH);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element("apply", Node.NS_MATH);
        apply2.addContent(new Element("power", Node.NS_MATH));
        apply2.addContent(getOperand1().toMathML());
        apply2.addContent(getOperand2().toMathML());

        apply.addContent(apply2);

        return apply;
    }

    /**
     * Change the exponent of power function.
     *
     * @param difference can be positive or negative. If positive -
     *                   exponent is increased by difference, else decreased by difference.
     */
    public void changeExponent(String difference) throws AlgorithmException {
        if (getOperand2() instanceof Constant c) {
            c = (Constant) c.add(new Constant(new BigDecimal(difference)));
        }
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * 2 * x ^ 2
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getOperand1() != null && getOperand2() != null) {
            if (getMultiplier() != 1) {
                sb.append(new Constant(getMultiplier()));
                sb.append("*");
            }

            if (getOperand2() instanceof Constant && ((Constant) getOperand2()).getNumber() == 1) {
                sb.append(getOperand1().toString());
            } else {
                sb.append("(");
                sb.append(getOperand1().toString());
                sb.append(")");
                sb.append("^");
                sb.append(getOperand2().toString());
            }
        } else {
            for (Node child : getChildren()) {
                sb.append(child.toString());
                sb.append("^");
            }
        }

        return sb.toString();
    }
}
