// Created: 30.03.2004 T 11:57:40
package org.clematis.math.v1.algorithm;

import org.clematis.math.AlgorithmException;

/**
 * Condition exception is thrown if some condition is not satisfied
 */
public class ConditionException extends AlgorithmException {

    public static final String CONDITION_FAILED_IN_MESSAGE = "Condition failed in ";

    String conditionName;
    String conditionCode;

    ConditionException(String conditionName, String conditionCode, int line) {
        super(CONDITION_FAILED_IN_MESSAGE, line);
        this.conditionName = conditionName;
        this.conditionCode = conditionCode;
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this <tt>Throwable</tt> instance
     * (which may be <tt>null</tt>).
     */
    public String getMessage() {
        return super.getMessage() + "condition: { " + conditionName + " " + conditionCode + " }";
    }
}
