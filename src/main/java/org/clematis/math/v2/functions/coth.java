// Created: 12.05.2006 T 17:10:12
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * coth(x) =  cosh(x) / sinh(x) = ( e^x + e^(-x) ) / ( e^x - e^(-x) )
 */
public class coth extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function " + getSignature()
                    + ": " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                coth retvalue = new coth();
                retvalue.setSignature(getSignature());
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            double a = Math.pow(Math.E, c1.getNumber()) + Math.pow(Math.E, -c1.getNumber());
            double b = Math.pow(Math.E, c1.getNumber()) - Math.pow(Math.E, -c1.getNumber());
            return new Constant(a / b);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
