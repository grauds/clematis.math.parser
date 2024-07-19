// Created: 05.07.2005 T 16:36:45

package org.clematis.math.reference.algorithm;

import org.clematis.math.reference.AbstractConstant;
import org.clematis.math.reference.AlgorithmException;
/**
 */
public interface iSimpleParameterProvider
{
    /**
     * Return parameter constant
     * @param in_varName parameter name
     * @return parameter value, string or double
     */
    AbstractConstant getParameterConstant( String in_varName ) throws AlgorithmException;
    /**
     * Provides the parameter.
     * @param name the parameter name.
     * @return the parameter or null.
     */
    Parameter getParameter(String name);
}
