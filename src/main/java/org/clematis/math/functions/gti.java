// Created: Mar 25, 2003 T 1:44:21 PM
package org.clematis.math.functions;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * gti (a, b) returns a >= b ? 1.0 : 0.0
 */
public class gti extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 1 || getArguments().size() > 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'gti': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                gt retvalue = new gt();
                retvalue.setSignature("gti");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            return new Constant((c1.getNumber() >= c2.getNumber()) ? "1" : "0");
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
