// Created: 30.01.2006 T 16:44:39
package org.clematis.math.v1.operations;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.jdom2.Element;

/**
 * Simple fraction is a division operation, waiting
 */
public class SimpleFraction extends aOperation {

    public SimpleFraction(IExpressionItem operand1, IExpressionItem operand2) {
        super(operand1, operand2);
    }

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        /*
         * First try to calculate each operand
         */
        IExpressionItem a = getOperand1().calculate();
        IExpressionItem b = getOperand2().calculate();
        /*
         * Do not divide simple fraction
         */
        return new SimpleFraction(a, b);
    }

    /**
     * Adds another expression item to this one.
     *
     * @param item another expression item
     * @return result expression item
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
        if (this.aKindOf(item)) {

            SimpleFraction sf = (SimpleFraction) item;
            IExpressionItem t1 = getOperand2().multiply(sf.getOperand1());
            IExpressionItem t2 = sf.getOperand2().multiply(getOperand1());

            this.setOperand1(t1.add(t2));
            this.setOperand2(getOperand2().multiply(sf.getOperand2()));

            return this;
        } else if (item instanceof Constant) {
            IExpressionItem t = getOperand2().multiply(item);
            if (t != null) {
                setOperand1(t.add(getOperand1()));
            }
            return this;
        }
        return null;
    }

    /**
     * Multiplies this expression item by another one.
     *
     * @param item another expression item
     * @return result expression item
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
        if (this.aKindOf(item)) {
            SimpleFraction sf = (SimpleFraction) item;
            setOperand1(getOperand1().multiply(sf.getOperand1()));
            setOperand2(getOperand2().multiply(sf.getOperand2()));
            return this;
        } else if (item instanceof Constant) {
            /*
             * Multiply term of fraction by constant
             */
            setOperand1(getOperand1().multiply(item));
            return this;
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
        return item instanceof SimpleFraction;
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(IExpressionItem item) {
        if (aKindOf(item)) {
            SimpleFraction sf = (SimpleFraction) item;
            if (getOperand1().equals(sf.getOperand1()) && getOperand2().equals(sf.getOperand2())) {
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
        return new Element("simple_fraction_stub");
    }

    /**
     * Returns product of division
     *
     * @return product of division
     */
    public Constant getProduct() throws AlgorithmException {
        /*
         * First try to calculate each operand
         */
        IExpressionItem a = getOperand1().calculate();
        IExpressionItem b = getOperand2().calculate();

        if (a instanceof Constant && b instanceof Constant) {
            return new Constant(((Constant) a).getNumber() / ((Constant) b).getNumber());
        }
        throw new AlgorithmException("Simple fraction does not contain constants: " + a + " / " + b);
    }

    /**
     * Sets multiplier
     *
     * @param multiplier to apply to the first operand
     */
    public void setMultiplier(double multiplier) {
        /*
         * Avoid trivial multiplication
         */
        if (multiplier != 1) {
            getOperand1().setMultiplier(multiplier);
        }
    }

    /**
     * Return multiplier
     *
     * @return constant coefficient
     */
    public double getMultiplier() {
        return 1;
    }
}
