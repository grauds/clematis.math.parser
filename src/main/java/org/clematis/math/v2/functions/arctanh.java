// Created: 29.06.2005 T 16:12:45
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * arctanh = (1/2) (log(1 + z) - log(1 - z))
 */
public class arctanh extends aFunction {
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
                    "Invalid number of arguments in function 'arctanh': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                arctanh retvalue = new arctanh();
                retvalue.setSignature("arctanh");
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(0.5 * (Math.log(1 + c1.getNumber()) - Math.log(1 - c1.getNumber())));
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
