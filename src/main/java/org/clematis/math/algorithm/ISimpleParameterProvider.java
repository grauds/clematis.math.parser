// Created: 05.07.2005 T 16:36:45

package org.clematis.math.algorithm;

import org.clematis.math.AbstractConstant;

public interface ISimpleParameterProvider {

    /**
     * Return parameter constant
     *
     * @param name parameter name
     * @return parameter value, string or double or null
     */
    AbstractConstant getParameterConstant(String name);

    /**
     * Provides the parameter.
     *
     * @param name the parameter name.
     * @return the parameter or null.
     */
    Parameter getParameter(String name);
}
