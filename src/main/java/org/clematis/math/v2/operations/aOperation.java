// Created: Feb 14, 2003 T 11:22:45 AM
package org.clematis.math.v2.operations;

import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;

import java.io.Serializable;

/**
 * Abstract operation class. This is a base class for all operations like addition, multiplication and etc.
 */
public abstract class aOperation extends SimpleNode implements Serializable {
    /**
     * Operation multiplier. For instance, it could be constant 2 in 2 * 5 ^ x
     */
    private double multiplier = 1.0;

    public aOperation(int i) {
        super(i);
    }

    protected aOperation() {
    }

    public aOperation(Node in_operand1, Node in_operand2) {
        setOperand1(in_operand1);
        setOperand2(in_operand2);
    }

    /**
     * Shallow copy constructor. This does not copy parent and children nodes.
     *
     * @param node to copy
     */
    protected aOperation(aOperation node) {
        super(node);
        this.multiplier = node.multiplier;
    }

    /**
     * Return constant coefficient
     *
     * @return constant coefficient
     */
    public double getMultiplier() {
        return multiplier;
    }

    /**
     * Sets constant multiplier
     *
     * @param multiplier
     */
    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public Node getOperand1() {
        return jjtGetChild(0);
    }

    public void setOperand1(Node m_operand) {
        jjtAddChild(m_operand, 0);
    }

    public Node getOperand2() {
        return jjtGetChild(1);
    }

    public void setOperand2(Node m_operand) {
        jjtAddChild(m_operand, 1);
    }
}
