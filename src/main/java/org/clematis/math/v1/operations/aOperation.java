// $Id: aOperation.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 14, 2003 T 11:22:45 AM

package org.clematis.math.v1.operations;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.algorithm.iParameterProvider;
import org.clematis.math.v1.iExpressionItem;

import java.io.Serializable;

/**
 * Abstract operation class. This is a base class for all operations
 * like addition, multiplication and etc.
 */
public abstract class aOperation implements iExpressionItem, Serializable {
    /**
     * Operands
     */
    private iExpressionItem[] operands = new iExpressionItem[2];
    /**
     * Operation multiplier. For instance, it could be constant 2 in 2 * 5 ^ x
     */
    private double multiplier = 1.0;

    protected aOperation() {
    }

    public aOperation(iExpressionItem in_operand1, iExpressionItem in_operand2) {
        setOperand1(in_operand1);
        setOperand2(in_operand2);
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

    public iExpressionItem getOperand1() {
        return operands[0];
    }

    public void setOperand1(iExpressionItem m_operand1) {
        operands[0] = m_operand1;
    }

    public iExpressionItem getOperand2() {
        return operands[1];
    }

    public void setOperand2(iExpressionItem m_operand2) {
        operands[1] = m_operand2;
    }

    public void addOperand(iExpressionItem item) {
        /**
         * Temp array to store data
         */
        iExpressionItem[] temp = new iExpressionItem[operands.length];
        System.arraycopy(operands, 0, temp, 0, operands.length);
        /**
         * Increase array by one and restore data
         */
        operands = new iExpressionItem[temp.length + 1];
        System.arraycopy(temp, 0, operands, 0, temp.length);
        /**
         * Write the last element
         */
        operands[operands.length - 1] = item;
    }

    public iExpressionItem[] getOperands() {
        return operands;
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter
     *                          and functions provider
     * @return expression item instance
     */
    public iExpressionItem calculate(iParameterProvider parameterProvider)
        throws AlgorithmException {
        return calculate();
    }
}
