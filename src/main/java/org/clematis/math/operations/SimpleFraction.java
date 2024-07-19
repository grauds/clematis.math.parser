// Created: 30.01.2006 T 16:44:39
package org.clematis.math.operations;

import java.math.BigDecimal;
import java.math.MathContext;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.clematis.math.utils.AlgorithmUtils;
import org.jdom2.Element;

/**
 * Simple fraction is a division operation, waiting
 */
public class SimpleFraction extends aOperation {
    public SimpleFraction(int i) {
        super(i);
    }

    public SimpleFraction() {
    }

    public SimpleFraction(Node in_operand1, Node in_operand2) {
        super(in_operand1, in_operand2);
    }

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        /**
         * First try to calculate each operand
         */
        Node a = getOperand1().calculate(parameterProvider);
        Node b = getOperand2().calculate(parameterProvider);
        /**
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
    public Node add(Node item) throws AlgorithmException {
        if (this.isSimilar(item)) {
            SimpleFraction nFraction = new SimpleFraction();
            SimpleFraction sf = (SimpleFraction) item;
            Node t1 = getOperand2().multiply(sf.getOperand1());
            Node t2 = sf.getOperand2().multiply(getOperand1());
            nFraction.setOperand1(t1.add(t2));
            nFraction.setOperand2(getOperand2().multiply(sf.getOperand2()));
            return nFraction;
        } else if (item instanceof Constant) {
            SimpleFraction nFraction = new SimpleFraction();
            Node t = getOperand2().multiply(item);
            if (t != null) {
                nFraction.setOperand1(t.add(getOperand1()));
                nFraction.setOperand2(getOperand2().calculate());
                return nFraction;
            } else {
                nFraction.setOperand1(getOperand1().calculate());
                nFraction.setOperand2(getOperand2().calculate());
                return nFraction;
            }
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
        if (this.isSimilar(item)) {
            SimpleFraction nFraction = new SimpleFraction();
            SimpleFraction sf = (SimpleFraction) item;
            nFraction.setOperand1(getOperand1().multiply(sf.getOperand1()));
            nFraction.setOperand2(getOperand2().multiply(sf.getOperand2()));
            return nFraction;
        } else if (item instanceof Constant) {
            /**
             * Multiply term of fraction by constant
             */
            SimpleFraction nFraction = new SimpleFraction();
            nFraction.setOperand1(getOperand1().multiply(item));
            nFraction.setOperand2(getOperand2().calculate());
            return nFraction;
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
    public boolean isSimilar(Node item) {
        return item instanceof SimpleFraction;
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(Node item) {
        if (isSimilar(item)) {
            SimpleFraction sf = (SimpleFraction) item;
            return getOperand1().equals(sf.getOperand1()) && getOperand2().equals(sf.getOperand2());
        } else if (item instanceof Constant) {
            return item.equals(this);
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
        Node a = getOperand1().calculate();
        Node b = getOperand2().calculate();

        if (a instanceof SimpleFraction) {
            a = ((SimpleFraction) a).getProduct();
        }

        if (b instanceof SimpleFraction) {
            b = ((SimpleFraction) b).getProduct();
        }

        if (a instanceof Constant && b instanceof Constant) {
            BigDecimal f = new BigDecimal(((Constant) a).getValue());
            BigDecimal s = new BigDecimal(((Constant) b).getValue());
            // set the number of significant digits to B used in operation
            return new Constant(f.divide(s, new MathContext(17)));
        } else {
            return null;
        }
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getOperand1() != null && getOperand2() != null) {
            try {
                Constant c = AlgorithmUtils.getNumericArgument(this);
                if (c != null) {
                    return c.toString();
                }
            } catch (AlgorithmException e) {
                return ("");
            }
        } else {
            for (Node child : getChildren()) {
                sb.append(child.toString());
                sb.append("/");
            }
        }
        return sb.toString();
    }
}
