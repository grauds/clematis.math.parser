// Created: Feb 14, 2003 T 11:36:06 AM
package org.clematis.math.operations;

import java.util.ArrayList;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.jdom2.Element;

/**
 * Multiplication operation
 */
public class Multiplication extends aOperation {
    public Multiplication(int i) {
        super(i);
    }

    /**
     * Shallow copy constructor. This does not copy parent and children nodes.
     *
     * @param node to copy
     */
    public Multiplication(aOperation node) {
        super(node);
    }

    public Multiplication() {
    }

    /**
     * Try to make a simplier expression or just multiply coefficients.
     *
     * @param parameterProvider
     * @return new or modified expression.
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        /**
         * Calculated nodes
         */
        ArrayList<Node> calculated_nodes = new ArrayList<Node>();
        /**
         * Calculate arguments
         */
        Node result = calculateArguments(parameterProvider, calculated_nodes);
        /**
         * Subsequently multiply first argument by next ones, assuming they are all constants
         */
        if (result != null && jjtGetNumChildren() > 0) {
            for (int i = 1; i < calculated_nodes.size(); i++) {
                Node product = result.multiply(calculated_nodes.get(i));
                if (product != null) {
                    // if multiplication is succeeded
                    result = product;
                } else {
                    // if multiplication is failed, do not try to simplify expression
                    result = null;
                    break;
                }
            }
            /**
             * Return multiplication with calculated arguments, if constant cannot be yielded.
             */
            if (result == null) {
                Multiplication multiplication = new Multiplication();
                int i = 0;
                for (Node arg : calculated_nodes) {
                    multiplication.jjtAddChild(arg, i++);
                }
                return multiplication;
            }
            /**
             * Return result of multiplication - the constant
             */
            else {
                return result;
            }
        }
        /**
         * Return empty multiplication
         */
        else {
            return new Multiplication(this);
        }
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean isSimilar(Node item) {
        if (item instanceof Multiplication mitem) {
            Node op1 = mitem.getOperand1();
            Node op2 = mitem.getOperand2();
            return op1.isSimilar(getOperand1()) && op2.isSimilar(getOperand2())
                ||
                op2.isSimilar(getOperand1()) && op1.isSimilar(getOperand2());
        }
        return false;
    }

    /**
     * Compares this expression item with a given one and returns true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(Node item) {
        if (item instanceof Multiplication mitem) {
            Node op1 = mitem.getOperand1();
            Node op2 = mitem.getOperand2();
            return op1.equals(getOperand1()) && op2.equals(getOperand2())
                ||
                op2.equals(getOperand1()) && op1.equals(getOperand2());
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
        apply2.addContent(new Element("times", Node.NS_MATH));
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
        if (getOperand1() != null && getOperand2() != null) {
            if (getMultiplier() != 1) {
                sb.append(new Constant(getMultiplier()));
                sb.append("*");
            }
            sb.append(getOperand1().toString());
            sb.append("*");
            sb.append(getOperand2().toString());
        } else {
            for (Node child : getChildren()) {
                sb.append(child.toString());
                sb.append("*");
            }
        }
        return sb.toString();
    }
}
