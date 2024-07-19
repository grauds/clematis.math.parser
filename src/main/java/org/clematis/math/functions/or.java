// Created: 30.03.2004 T 17:15:03
package org.clematis.math.functions;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * Returns 1.0 if a is any of arguments is equal to 1.0, otherwise returns 0.0;
 * <p>
 * i.e. it returns a == 0.0 || b == 1.0 ||& ... ? 1.0 : 0.0.
 */
public class or extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            boolean good_arguments = true;
            boolean result = true;

            for (int i = 0; i < getArguments().size(); i++) {
                Node a = getArguments().get(i).calculate(parameterProvider);
                if (!(a instanceof Constant)) {
                    good_arguments = false;
                    break;
                }
                Constant constant = AlgorithmUtils.getNumericArgument(a);
                double value = constant.getNumber();
                if (value != 1.0 && value != 0.0) {
                    good_arguments = false;
                    break;
                }
                if (i == 0) {
                    result = value == 1.0;
                } else {
                    result |= value == 1.0;
                }
            }

            if (!good_arguments) {
                or retvalue = new or();
                retvalue.setSignature("or");

                for (int i = 0; i < getArguments().size(); i++) {
                    retvalue.addArgument(getArguments().get(i));
                }

                return retvalue;
            }

            return new Constant(result ? "1" : "0");
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
