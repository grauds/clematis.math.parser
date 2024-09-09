// Created: Mar 25, 2003 T 2:41:07 PM
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * abs ( a )
 */
public class abs extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'abs': " + getArguments().size());
            }
            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                abs retvalue = new abs();
                retvalue.setSignature("abs");
                retvalue.addArgument(a1);
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(Math.abs(c1.getNumber()));
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
