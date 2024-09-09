 // Created: Mar 25, 2003 T 1:44:21 PM
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * gt (a, b) returns a > b ? 1.0 : 0.0
 */
public class gt extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 1 || getArguments().size() > 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'gt': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                gt retvalue = new gt();
                retvalue.setSignature("gt");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            return new Constant((c1.getNumber() > c2.getNumber()) ? "1" : "0");
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
