// Created: 11.01.2007 T 11:25:41
package org.clematis.math.v2.operations;

import java.util.ArrayList;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.jdom2.Element;

/**
 * Unary operation
 */
public class Unary extends aOperation {
    /**
     * Unary minus operation
     */
    public static final int MINUS = 1;
    /**
     * Unary plus operation
     */
    public static final int PLUS = 2;

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        /**
         * Calculated nodes
         */
        ArrayList<Node> calculated_nodes = new ArrayList<Node>();
        /**
         * Calculate arguments
         */
        Node result = calculateArguments(parameterProvider, calculated_nodes);
        /**
         * Since there can be only one argument, negate it or leave as it is
         */
        if (result != null && kind == Unary.MINUS) {
            return result.multiply(new Constant("-1"));
        } else {
            return result;
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
        return (item instanceof Unary);
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(Node item) {
        if ((item instanceof Unary) && ((Unary) item).getTokenKind() == this.getTokenKind()) {
            if (jjtGetChild(0) != null && item.jjtGetChild(0) != null) {
                return jjtGetChild(0).equals(item.jjtGetChild(0));
            }
        }
        return false;
    }

    public Element toMathML() {
        Element apply = new Element("apply", Node.NS_MATH);

        Element times = new Element("times", Node.NS_MATH);
        apply.addContent(times);

        Element cn = new Element("cn", Node.NS_MATH);
        cn.setText(((getTokenKind() == Unary.MINUS) ? "-" : "") + getMultiplier());
        apply.addContent(cn);

        return apply;
    }
}
