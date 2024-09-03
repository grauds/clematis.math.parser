// Created: 11.01.2007 T 11:06:56
package org.clematis.math.v2.operations;

import java.util.ArrayList;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.jdom2.Element;

/**
 * Substraction operation
 */
public class Substraction extends Addition {
    public Substraction() {
    }

    public Substraction(int i) {
        super(i);
    }

    /**
     * Public constructor for addition operation.
     *
     * @param in_operand1 first operand
     * @param in_operand2 second operand
     */
    public Substraction(Node in_operand1, Node in_operand2) {
        super(in_operand1, in_operand2);
    }

    /**
     * Calculates the values of children of this node. It calls calculate() on every child.
     *
     * @return the calculated first argument or result of operation
     * @throws AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculateArguments(IParameterProvider parameterProvider,
                                   ArrayList<Node> calculated_nodes) throws AlgorithmException {
        if (jjtGetNumChildren() > 0) {
            /**
             * Calculate all arguments
             */
            for (int i = 0; i < jjtGetNumChildren(); i++) {
                // get next argument and calculate it
                Node calculated_node = jjtGetChild(i).calculate(parameterProvider);
                // negate arguments, all but the first one
                if (i > 0) {
                    calculated_node = calculated_node.multiply(new Constant("-1"));
                }
                if (calculated_node != null) {
                    calculated_nodes.add(calculated_node);
                } else {
                    throw new AlgorithmException("Cannot calculate node: " + jjtGetChild(i));
                }
            }
            return calculated_nodes.get(0);
        }
        return null;
    }

    /**
     * Provides mathml formatted element, representing expression subtree.
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
        apply2.addContent(new Element("minus", Node.NS_MATH));

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
            sb.append("-");
            sb.append(getOperand2().toString());
            sb.append(")");
        } else {
            for (Node child : getChildren()) {
                sb.append(child.toString());
                sb.append("-");
            }
        }
        return sb.toString();
    }
}