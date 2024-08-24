// Created: 01.12.2003 T 12:25:43
package org.clematis.math.v2.algorithm;

import org.clematis.math.v2.AbstractConstant;

/**
 * Provider of variables for functions.
 */
public interface iVariableProvider {
    /**
     * Return variable constant
     *
     * @param in_varName parameter name
     * @return parameter value, string or double
     */
    AbstractConstant getVariableConstant(String in_varName);
}