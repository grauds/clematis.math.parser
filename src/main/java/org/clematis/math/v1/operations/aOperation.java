// $Id: aOperation.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 14, 2003 T 11:22:45 AM

package org.clematis.math.v1.operations;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.algorithm.IParameterProvider;
import org.clematis.math.v1.IExpressionItem;

import java.io.Serializable;

/**
 * Abstract operation class. This is a base class for all operations
 * like addition, multiplication and etc.
 */
public abstract class aOperation implements IExpressionItem, Serializable {
    /**
     * Operands
     */
    private IExpressionItem[] operands = new IExpressionItem[2];
    /**
     * Operation multiplier. For instance, it could be constant 2 in 2 * 5 ^ x
     */
    private double multiplier = 1.0;

    protected aOperation() {
    }

    public aOperation(IExpressionItem in_operand1, IExpressionItem in_operand2) {
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

    public IExpressionItem getOperand1() {
        return operands[0];
    }

    public void setOperand1(IExpressionItem m_operand1) {
        operands[0] = m_operand1;
    }

    public IExpressionItem getOperand2() {
        return operands[1];
    }

    public void setOperand2(IExpressionItem m_operand2) {
        operands[1] = m_operand2;
    }

    public void addOperand(IExpressionItem item) {
        /**
         * Temp array to store data
         */
        IExpressionItem[] temp = new IExpressionItem[operands.length];
        System.arraycopy(operands, 0, temp, 0, operands.length);
        /**
         * Increase array by one and restore data
         */
        operands = new IExpressionItem[temp.length + 1];
        System.arraycopy(temp, 0, operands, 0, temp.length);
        /**
         * Write the last element
         */
        operands[operands.length - 1] = item;
    }

    public IExpressionItem[] getOperands() {
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
    public IExpressionItem calculate(IParameterProvider parameterProvider)
        throws AlgorithmException {
        return calculate();
    }
}
