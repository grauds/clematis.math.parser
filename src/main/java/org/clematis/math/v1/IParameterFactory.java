package org.clematis.math.v1;

import org.clematis.math.v1.algorithm.Parameter;

public interface IParameterFactory {

    Parameter createParameter(String name, String code);
}
