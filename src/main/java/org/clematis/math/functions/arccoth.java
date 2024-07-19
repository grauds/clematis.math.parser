// Created: 11.05.2006 T 19:48:20
package org.clematis.math.functions;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * arccoth(x)=(1/2)*( log( 1 + 1 / x ) - log( 1 - 1 / x ) )
 * <p>
 * arccoth(0)=pi/2
 */
public class arccoth extends aFunction {
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
                arccoth retvalue = new arccoth();
                retvalue.setSignature(getSignature());
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            if (c1.getNumber() != 0) {
                return new Constant(0.5 * (Math.log(1 + 1 / c1.getNumber()) - Math.log(1 - 1 / c1.getNumber())));
            } else {
                return new Constant(Math.PI / 2);
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
