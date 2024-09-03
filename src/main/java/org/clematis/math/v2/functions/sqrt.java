// Created: Mar 25, 2003 T 2:28:55 PM
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * sqrt ( a )
 */
public class sqrt extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException(
                    "Invalid number of arguments in function 'sqrt': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                sqrt retvalue = new sqrt();
                retvalue.setSignature("sqrt");
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            double root = Math.sqrt(c1.getNumber());
            if (!Double.isNaN(root)) {
                return new Constant(root);
            } else {
                throw new AlgorithmException("Square root of negative base: " + c1.getNumber());
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
