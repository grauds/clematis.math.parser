package org.clematis.math;

import org.clematis.math.algorithm.Parameter;

public interface IParameterFactory {

    Parameter createParameter(String name, String code);
}
