// Created: 05.07.2005 T 16:36:45

package org.clematis.math.v1.algorithm;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;

/**
 *
 */
public interface ISimpleParameterProvider {

    /**
     * Return parameter constant
     *
     * @param name parameter name
     * @return parameter value, string or double
     */
    AbstractConstant getParameterConstant(String name) throws AlgorithmException;

    /**
     * Provides the parameter.
     *
     * @param name the parameter name.
     * @return the parameter or null.
     */
    Parameter getParameter(String name);
}
