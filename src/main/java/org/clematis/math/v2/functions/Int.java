// Created: Aug 26, 2003 T 12:23:40 PM
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

/**
 * int ( x )
 */
public class Int extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'int': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                Int retvalue = new Int();
                retvalue.setSignature("Int");
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            assert c1 != null;
            return new Constant(c1.getNumber());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
