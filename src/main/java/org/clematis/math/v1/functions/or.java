// Created: 30.03.2004 T 17:15:03

package org.clematis.math.v1.functions;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * Returns 1.0 if a is any of arguments is equal to 1.0, otherwise returns 0.0;
 * <p>
 * i.e. it returns a == 0.0 || b == 1.0 ||& ... ? 1.0 : 0.0.
 */
@SuppressWarnings("checkstyle:TypeName")
public class or extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            boolean goodArguments = true;
            boolean result = true;

            for (int i = 0; i < arguments.size(); i++) {
                IExpressionItem a = arguments.get(i).calculate();
                if (!(a instanceof Constant)) {
                    goodArguments = false;
                    break;
                }
                Constant constant = AlgorithmUtils.getNumericArgument(a);
                double value = constant.getNumber();
                if (value != 1.0 && value != 0.0) {
                    goodArguments = false;
                    break;
                }
                if (i == 0) {
                    result = value == 1.0;
                } else {
                    result |= value == 1.0;
                }
            }

            if (!goodArguments) {
                or retvalue = new or();
                retvalue.setSignature("or");

                for (IExpressionItem argument : arguments) {
                    retvalue.addArgument(argument);
                }

                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            return new Constant((result ? 1.0 : 0.0) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
