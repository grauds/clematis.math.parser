package org.clematis.math;

import org.clematis.math.v1.algorithm.IFunctionProvider;
import org.clematis.math.v1.algorithm.IParameterProvider;

public interface IParameterCalculator {
    IExpressionItem calculate(String name,
                              String code,
                              IParameterProvider parameterProvider,
                              IFunctionProvider functionProvider,
                              int currentLine
    ) throws AlgorithmException;
}
