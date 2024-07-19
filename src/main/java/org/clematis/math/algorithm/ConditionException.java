// Created: 30.03.2004 T 11:57:40
package org.clematis.math.algorithm;

/**
 * Condition exception is thrown if some condition is not satisfied
 */
public class ConditionException extends AlgorithmException {
    String conditionName = "";
    String conditionCode = "";

    ConditionException(String conditionName, String conditionCode) {
        super("Condition failed in ");
        this.conditionName = conditionName;
        this.conditionCode = conditionCode;
    }

    ConditionException(String conditionName, String conditionCode, int line) {
        super("Condition failed in ", line);
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

    public String getConditionName() {
        return conditionName;
    }

    public String getConditionCode() {
        return conditionCode;
    }
}
