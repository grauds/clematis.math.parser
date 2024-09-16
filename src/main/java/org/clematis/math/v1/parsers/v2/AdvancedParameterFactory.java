package org.clematis.math.v1.parsers.v2;

import org.clematis.math.v1.IParameterFactory;
import org.clematis.math.v1.algorithm.Parameter;

public class AdvancedParameterFactory implements IParameterFactory {

    @Override
    public Parameter createParameter(String name, String code) {
        Parameter parameter = new Parameter(name, code);
        parameter.setParameterCalculator(new ParameterCalculator());
        return parameter;
    }
}
