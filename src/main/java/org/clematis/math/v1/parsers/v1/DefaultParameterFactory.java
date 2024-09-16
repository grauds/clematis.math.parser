package org.clematis.math.v1.parsers.v1;

import org.clematis.math.v1.IParameterFactory;
import org.clematis.math.v1.algorithm.Parameter;

public class DefaultParameterFactory implements IParameterFactory {

    @Override
    public Parameter createParameter(String name, String code) {
        return new Parameter(name, code);
    }
}
