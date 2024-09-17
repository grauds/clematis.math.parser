package org.clematis.math.parsers.v2;

import org.clematis.math.IParameterFactory;
import org.clematis.math.algorithm.Parameter;

public class AdvancedParameterFactory implements IParameterFactory {

    @Override
    public Parameter createParameter(String name, String code) {
        Parameter parameter = new Parameter(name, code);
        parameter.setParameterCalculator(new ParameterCalculator());
        return parameter;
    }
}
