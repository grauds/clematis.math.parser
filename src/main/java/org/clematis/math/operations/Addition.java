// Created: Feb 14, 2003 T 11:29:25 AM
package org.clematis.math.operations;

import java.util.ArrayList;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.jdom2.Element;

/**
 * Addition operation.
 */
public class Addition extends aOperation {
    public Addition() {
    }

    public Addition(int i) {
        super(i);
    }

    /**
     * Shallow copy constructor. This does not copy parent and children nodes.
     *
     * @param node to copy
     */
    public Addition(aOperation node) {
        super(node);
    }

    /**
     * Public constructor for addition operation.
     *
     * @param in_operand1 first operand
     * @param in_operand2 second operand
     */
    public Addition(Node in_operand1, Node in_operand2) {
        super(in_operand1, in_operand2);
    }

    /**
     * Spawn resulting node or copy this addition out
     *
     * @param parameterProvider
     * @return resulting node or new addition
     * @throws AlgorithmException
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        /**
         * First node from calculated arguments
         */
        ArrayList<Node> calculated_nodes = new ArrayList<Node>();
        /**
         * Calculate arguments
         */
        Node result = calculateArguments(parameterProvider, calculated_nodes);
        /**
         * Subsequently add calculated arguments to the first one, assuming they are all constants
         */
        if (result != null && jjtGetNumChildren() > 0) {
            for (int i = 1; i < calculated_nodes.size(); i++) {
                Node sum = result.add(calculated_nodes.get(i));
                if (sum != null) {
                    // if addition is succeeded
                    result = sum;
                } else {
                    // if addition is failed, do not try to simplify expression
                    result = null;
                    break;
                }
            }
            /**
             * Return addition with calculated arguments, if constant cannot be yielded.
             */
            if (result == null) {
                Addition addition = new Addition();
                int i = 0;
                for (Node arg : calculated_nodes) {
                    addition.jjtAddChild(arg, i++);
                }
                return addition;
            }
            /**
             * Return result of addition - the constant
             */
            else {
                return result;
            }
        }
        /**
         * Return empty addition
         */
        else {
            return new Addition(this);
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
        if (item instanceof Addition add_item) {
            Node op1 = add_item.getOperand1();
            Node op2 = add_item.getOperand2();
            return op1.isSimilar(getOperand1()) && op2.isSimilar(getOperand2())
                ||
                op2.isSimilar(getOperand1()) && op1.isSimilar(getOperand2());
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
        if (item instanceof Addition add_item) {
            Node op1 = add_item.getOperand1();
            Node op2 = add_item.getOperand2();
            return (op1.equals(getOperand1()) && op2.equals(getOperand2()))
                ||
                (op2.equals(getOperand1()) && op1.equals(getOperand2()));
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
        apply2.addContent(new Element("plus", Node.NS_MATH));

        for (Node child : getChildren()) {
            apply2.addContent(child.toMathML());
        }

        apply.addContent(apply2);

        return apply;
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * x + y or 2 (x + y)
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
            sb.append("(");
            sb.append(getOperand1().toString());
            sb.append("+");
            sb.append(getOperand2().toString());
            sb.append(")");
        } else {
            for (Node child : getChildren()) {
                sb.append(child.toString());
                sb.append("+");
            }
        }
        return sb.toString();
    }
}
