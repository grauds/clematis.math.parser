// Created: 25.03.2004 T 16:27:30
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * Returns 1.0 if a is equal to 0.0, otherwise returns 1.0;
 * i.e. it returns a == 0.0 ? 1.0 : 0.0.
 */
public class not extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() > 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'not': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                not retvalue = new not();
                retvalue.setSignature("not");
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant((c1.getNumber() == 0.0) ? "1" : "0");
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}

