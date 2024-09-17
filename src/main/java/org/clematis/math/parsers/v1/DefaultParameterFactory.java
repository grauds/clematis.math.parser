package org.clematis.math.parsers.v1;

import org.clematis.math.IParameterFactory;
import org.clematis.math.algorithm.Parameter;

public class DefaultParameterFactory implements IParameterFactory {

    @Override
    public Parameter createParameter(String name, String code) {
        return new Parameter(name, code);
    }
}
