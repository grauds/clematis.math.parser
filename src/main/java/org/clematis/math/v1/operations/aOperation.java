// $Id: aOperation.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 14, 2003 T 11:22:45 AM

package org.clematis.math.v1.operations;

import java.io.Serializable;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.algorithm.IParameterProvider;

import lombok.Getter;
import lombok.Setter;

/**
 * Abstract operation class. This is a base class for all operations
 * like addition, multiplication and etc.
 */
@SuppressWarnings("checkstyle:TypeName")
@Getter
@Setter
public abstract class aOperation implements IExpressionItem, Serializable {
    /**
     * Operands
     */
    private IExpressionItem[] operands = new IExpressionItem[2];
    /**
     * Operation multiplier. For instance, it could be constant 2 in 2 * 5 ^ x
     */
    private double multiplier = 1.0;

    protected aOperation() { }

    public aOperation(IExpressionItem operand1, IExpressionItem operand2) {
        setOperand1(operand1);
        setOperand2(operand2);
    }

    public void addOperand(IExpressionItem item) {
        /*
         * Temp array to store data
         */
        IExpressionItem[] temp = new IExpressionItem[operands.length];
        System.arraycopy(operands, 0, temp, 0, operands.length);
        /*
         * Increase array by one and restore data
         */
        operands = new IExpressionItem[temp.length + 1];
        System.arraycopy(temp, 0, operands, 0, temp.length);
        /*
         * Write the last element
         */
        operands[operands.length - 1] = item;
    }

    public IExpressionItem getOperand1() {
        return operands[0];
    }

    public void setOperand1(IExpressionItem operand1) {
        operands[0] = operand1;
    }

    public IExpressionItem getOperand2() {
        return operands[1];
    }

    public void setOperand2(IExpressionItem operand2) {
        operands[1] = operand2;
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter
     *                          and functions provider
     * @return expression item instance
     */
    public IExpressionItem calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        return calculate();
    }
}
