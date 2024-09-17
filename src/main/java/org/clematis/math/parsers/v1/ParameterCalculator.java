package org.clematis.math.parsers.v1;

import static org.clematis.math.algorithm.Parameter.COLON;
import static org.clematis.math.algorithm.Parameter.EQUALS_SIGN;
import org.clematis.math.AbstractConstant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.IParameterCalculator;
import org.clematis.math.algorithm.IFunctionProvider;
import org.clematis.math.algorithm.IParameterProvider;
import org.clematis.math.operations.SimpleFraction;
import org.clematis.math.utils.AlgorithmUtils;

public class ParameterCalculator implements IParameterCalculator {

    @Override
    public IExpressionItem calculate(String name,
                                     String code,
                                     IParameterProvider parameterProvider,
                                     IFunctionProvider functionProvider,
                                     int currentLine
    )
        throws AlgorithmException {

        IExpressionItem result = null;

        if (code != null) {
            try {
                /* parse processed code string */
                ExpressionParser exprParser = new ExpressionParser(
                    AlgorithmUtils.replaceParameters(code, parameterProvider, currentLine, false),
                    parameterProvider,
                    null,
                    functionProvider
                );
                result = exprParser.parse();
                /* calculate expression root */
                if (result != null) {
                    result = result.calculate(parameterProvider);
                }
            } catch (AlgorithmException ex) {
                throw new AlgorithmException("Error in "
                    + name
                    + EQUALS_SIGN
                    + code
                    + COLON
                    + ex.getMessage());
            }
            if (!(result instanceof AbstractConstant) && !(result instanceof SimpleFraction)) {
                throw new AlgorithmException("Parameter: "
                    + name
                    + EQUALS_SIGN
                    + code
                    + " is not a CONSTANT: "
                    + (result == null ? null : result.toString()));
            }
        }

        return result;
    }
}
