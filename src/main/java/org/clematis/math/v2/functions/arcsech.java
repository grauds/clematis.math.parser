// Created: 12.05.2006 T 10:39:21
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * arcsech(x) = log(1/x + sqrt(1/x - 1) sqrt(1/x + 1))
 */
public class arcsech extends aFunction {
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
                arcsech retvalue = new arcsech();
                retvalue.setSignature(getSignature());
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            if (c1.getNumber() != 0) {
                return new Constant(Math.log(
                    1 / c1.getNumber() + Math.sqrt(1 / c1.getNumber() - 1) * Math.sqrt(1 / c1.getNumber() + 1)));
            } else {
                throw new AlgorithmException("Zero argument in " + getSignature());
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }

    }
}
