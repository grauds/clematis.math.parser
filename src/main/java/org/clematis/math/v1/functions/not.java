// Created: 25.03.2004 T 16:27:30

package org.clematis.math.v1.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * Returns 1.0 if a is equal to 0.0, otherwise returns 1.0;
 * i.e. it returns a == 0.0 ? 1.0 : 0.0.
 */
@SuppressWarnings("checkstyle:TypeName")
public class not extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() > 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'not': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                not retvalue = new not();
                retvalue.setSignature("not");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(((c1.getNumber() == 0.0) ? 1.0 : 0.0) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}

