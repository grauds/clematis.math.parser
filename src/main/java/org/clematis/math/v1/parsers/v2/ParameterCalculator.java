package org.clematis.math.v1.parsers.v2;

import java.io.StringReader;

import static org.clematis.math.v1.algorithm.Parameter.COLON;
import static org.clematis.math.v1.algorithm.Parameter.EQUALS_SIGN;
import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.IParameterCalculator;
import org.clematis.math.parsers.string.SimpleNode;
import org.clematis.math.parsers.string.StringMathParser;
import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.algorithm.IFunctionProvider;
import org.clematis.math.v1.algorithm.IParameterProvider;
import org.clematis.math.v1.operations.SimpleFraction;

import lombok.Getter;
import lombok.Setter;

/**
 * Parameter is a product of one line of the algorithm
 */
@Setter
@Getter
public class ParameterCalculator implements IParameterCalculator {

    /**
     * Calculates parameter value.
     * It is an <code>AbstractConstant</code> object.
     */
    @Override
    public IExpressionItem calculate(String name,
                                     String code,
                                     IParameterProvider parameterProvider,
                                     IFunctionProvider functionProvider,
                                     int currentLine
    ) throws AlgorithmException {

        IExpressionItem result = null;
        if (code != null) {
            try {
                /* parse processed code string */
                StringMathParser parser = new StringMathParser(new StringReader(code));
                /* inherit parameters declared earlier */
                parser.setParameterProvider(parameterProvider);
                /* inherit functions declared earlier */
                parser.setFunctionProvider(functionProvider);
                /* start parsing */
                SimpleNode node = parser.Start();
                /* calculate expression root */
                if (node != null && node.getExpressionItem() != null) {
                    result = node.getExpressionItem().calculate(parameterProvider);
                }
            } catch (AlgorithmException ex) {
                throw new AlgorithmException("Error in "
                    + name
                    + EQUALS_SIGN
                    + code
                    + COLON
                    + ex.getMessage(), currentLine
                );
            } catch (Exception ex) {
                throw new AlgorithmException(
                    "Exception calculating line: " + code + " : " + ex.getMessage(), currentLine
                );
            }
            if (!(result instanceof AbstractConstant) && !(result instanceof SimpleFraction)) {
                throw new AlgorithmException("Parameter: "
                    + name
                    + EQUALS_SIGN
                    + code
                    + " is not a CONSTANT: "
                    + (result == null ? null : result.toString()), currentLine
                );
            }
        }
        return result;
    }
}
