// Created: Feb 14, 2003 T 12:27:38 PM
package org.clematis.math.v2.functions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import org.clematis.math.v2.FunctionFactory;
import org.clematis.math.v2.operations.Power;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;
import org.jdom2.Element;

/**
 * Abstract constant function.
 */
public abstract class aFunction extends SimpleNode implements Serializable {
    /**
     * Parent function factory
     */
    protected FunctionFactory functionFactory = new FunctionFactory();

    protected aFunction() {
        super(0);
    }

    public aFunction(int i) {
        super(i);
    }

    /**
     * Add argument to a function
     *
     * @param argument expression item as a parameter
     */
    public void addArgument(Node argument) {
        if (argument != null) {
            jjtAddChild(argument, jjtGetNumChildren());
        }
    }

    /**
     * Return a set of arguments of this function
     *
     * @return an Node set of arguments of this function
     */
    public ArrayList<Node> getArguments() {
        ArrayList<Node> arguments = new ArrayList<Node>();
        Collections.addAll(arguments, getChildren());
        return arguments;
    }

    public String getSignature() {
        return token;
    }

    public void setSignature(String signature) {
        this.token = signature;
    }

    public FunctionFactory getFunctionFactory() {
        return functionFactory;
    }

    public void setFunctionFactory(FunctionFactory functionFactory) {
        this.functionFactory = functionFactory;
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element apply = new Element("apply", Node.NS_MATH);

        Element function = new Element(token);
        apply.addContent(function);
        for (Node argument : getChildren()) {
            apply.addContent(argument.toMathML());
        }

        return apply;
    }

    public boolean equals(Node item) {
        if (this == item) {
            return true;
        }
        if (!(item instanceof aFunction aFunction)) {
            return false;
        }

        return Objects.equals(token, aFunction.token);
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean isSimilar(Node item) {
        if (this == item) {
            return true;
        }
        if (item instanceof aFunction) {
            return equals(item);
        } else if (item instanceof Power) {
            Node expression = ((Power) item).getOperand1();
            if (expression instanceof aFunction) {
                return equals(expression);
            }
        }
        return false;
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

        sb.append(token);
        if (getChildren() != null && getChildren().length > 0) {
            sb.append("(");
            int i = 0;
            for (Node argument : getChildren()) {
                sb.append(argument.toString());
                if (i + 1 != jjtGetNumChildren()) {
                    sb.append(",");
                }
                i++;
            }
            sb.append(")");
        }

        return sb.toString();
    }
}