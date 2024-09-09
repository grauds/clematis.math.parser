// Created: 30.03.2004 T 17:04:01
package org.clematis.math.v1.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * Returns 1.0 if a is all arguments equal to 1.0, otherwise returns 0.0;
 * <p>
 * i.e. it returns a == 1.0 && b == 1.0 && ... ? 1.0 : 0.0.
 */
@SuppressWarnings("checkstyle:TypeName")
public class and extends aFunction2 {
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
                if (!AlgorithmUtils.isGoodNumericArgument(a)) {
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
                    result &= value == 1.0;
                }
            }

            if (!goodArguments) {
                and retvalue = new and();
                retvalue.setSignature("and");

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
