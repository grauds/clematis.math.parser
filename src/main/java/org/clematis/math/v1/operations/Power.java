// Created: Feb 14, 2003 T 11:39:03 AM
package org.clematis.math.v1.operations;

import java.math.BigDecimal;

import static org.clematis.math.XMLConstants.APPLY_ELEMENT_NAME;
import static org.clematis.math.XMLConstants.TIMES_ELEMENT_NAME;
import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.Variable;
import org.clematis.math.v1.utils.AlgorithmUtils;
import org.jdom2.Element;

/**
 * Power operation
 */
public class Power extends AbstractOperation {

    public static final String POWER_ELEMENT_NAME = "power";

    public Power() {

    }
    /**
     * Common constructor. Note, that constructor does not simplify the expression.
     *
     * @param operand1 first argument
     * @param operand2 second argument
     */
    public Power(IExpressionItem operand1, IExpressionItem operand2) {
        super(operand1, operand2);
    }

    @SuppressWarnings({"checkstyle:NestedIfDepth", "checkstyle:ReturnCount"})
    public IExpressionItem calculate() throws AlgorithmException {

        IExpressionItem base = getOperand1().calculate();
        IExpressionItem exponent = getOperand2().calculate();

        if (!AlgorithmUtils.isGoodNumericArgument(exponent)) {
            return this;
        }

        Constant cExponent = AlgorithmUtils.getNumericArgument(exponent);

        if (base != null) {
            if (AlgorithmUtils.isGoodNumericArgument(base)) {
                try {
                    if (((int) cExponent.getNumber()) == cExponent.getNumber()) {
                        IExpressionItem item = AlgorithmUtils.getNumericArgument(base).pow(cExponent);
                        item.setMultiplier(getMultiplier());
                        return item;
                    }
                } catch (ArithmeticException ignored) {}

                double power = Math.pow(((Constant) base).getNumber(), cExponent.getNumber());
                Constant c = new Constant(power);
                c.setMultiplier(getMultiplier());

                return c;
            } else if (cExponent.getNumber() == 0) {
                return new Constant(getMultiplier());
            }
        }
        /*
         * Create new power expression
         */
        Power power = new Power(base, exponent);
        power.setMultiplier(power.getMultiplier() * getMultiplier());
        return power;
    }

    /**
     * Adds another expression to this power function
     *
     * @param item expression item to add
     * @return new expression item as a product of addition
     */
    public IExpressionItem add(IExpressionItem item) {
        /*
         * Case: y^2 + y^2.
         */
        if (this.aKindOf(item) && (item instanceof Power p)) {
            /*
             * Apply more strict limitations.
             */
            if (this.getPower().equals(p.getPower())) {
                this.setMultiplier(getMultiplier() + p.getMultiplier());
                return this;
            }
        }
        return null;
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
             * Case: C * y^3.
             */
            this.setMultiplier(((Constant) item).getNumber() * getMultiplier());
            return this;
        } else if (this.aKindOf(item) && (item instanceof Variable v)) {
            /*
             * Case: y * y^3.
             */
            this.setMultiplier(getMultiplier() * v.getMultiplier());
            setOperand2(getPower().add(new Constant(1)));
            return this;
        } else if (item instanceof Power p) {
            /*
             * Case: y^2 * y^3.
             */
            if (p.getOperand1().equals(getOperand1())) {
                this.setMultiplier(getMultiplier() * p.getMultiplier());
                setOperand2(getPower().add(p.getPower()));
                return this.calculate();
            }
        }

        return null;
    }

    /**
     * Return power of this operation
     *
     * @return iExpressionItem power
     */
    public IExpressionItem getPower() {
        return getOperand2();
    }

    /**
     * Sets new exponent
     *
     * @param exponent to set
     */
    public void setPower(IExpressionItem exponent) {
        setOperand2(exponent);
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public boolean aKindOf(IExpressionItem item) {
        /*
         * We can multiply power functions only if
         * bases are similar.
         * Addition can be made only
         * if bases and powers are equal.
         */
        if (item instanceof Variable) {
            return getOperand1().aKindOf(item);
        } else if (item instanceof Power) {
            return getOperand1().aKindOf(((Power) item).getOperand1());
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
        if (item instanceof Power p) {
            return (getOperand1().equals(p.getOperand1())
                && (getPower().equals(p.getPower()))
                && (getMultiplier() == p.getMultiplier()));
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
        apply2.addContent(new Element(POWER_ELEMENT_NAME, IExpressionItem.NS_MATH));
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

    }

    /**
     * Change the exponent of power function.
     *
     * @param difference can be positive or negative. If positive -
     *                   exponent is increased by difference, else decreased by difference.
     */
    public void changeExponent(String difference) throws AlgorithmException {
        if (getOperand2() instanceof Constant c) {
            c.add(new Constant(new BigDecimal(difference)));
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

        return sb.toString();
    }
}
