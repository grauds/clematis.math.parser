// Created: 29.06.2005 T 15:43:23
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * tanh(x) = sinh(x) / cosh(x) = ( e^x - e^(-x) ) / ( e^x + e^(-x) )
 */
public class tanh extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function " + getSignature()
                    + ": " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                tanh retvalue = new tanh();
                retvalue.setSignature(getSignature());
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            double a = Math.pow(Math.E, c1.getNumber()) - Math.pow(Math.E, -c1.getNumber());
            double b = Math.pow(Math.E, c1.getNumber()) + Math.pow(Math.E, -c1.getNumber());
            return new Constant(a / b);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
