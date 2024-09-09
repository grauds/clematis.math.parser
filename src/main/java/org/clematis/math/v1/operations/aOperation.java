// $Id: aOperation.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 14, 2003 T 11:22:45 AM

package org.clematis.math.v1.operations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private final List<IExpressionItem> operands = new ArrayList<>(2);
    /**
     * Operation multiplier. For instance, it could be constant 2 in 2 * 5 ^ x
     */
    private double multiplier = 1.0;

    protected aOperation() { }

    public aOperation(IExpressionItem... operand) {
        this.operands.addAll(Arrays.asList(operand));

    }

    public void addOperand(IExpressionItem item) {
        this.operands.add(item);
    }

    public IExpressionItem getOperand1() {
        return operands.get(0);
    }

    public void setOperand1(IExpressionItem operand1) {
        operands.set(0, operand1);
    }

    public IExpressionItem getOperand2() {
        return operands.get(1);
    }

    public void setOperand2(IExpressionItem operand2) {
        operands.set(1, operand2);
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
