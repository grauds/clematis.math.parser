// Created: 01.12.2003 T 12:25:43
package org.clematis.math.v2.algorithm;

import org.clematis.math.v2.AbstractConstant;

/**
 * Provider of variables for functions.
 */
public interface IVariableProvider {
    /**
     * Return variable constant
     *
     * @param name parameter name
     * @return parameter value, string or double
     */
    AbstractConstant getVariableConstant(String name);
}
