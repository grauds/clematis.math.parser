// Created: 29.06.2005 T 15:20:04

package org.clematis.math.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * cosh(x) = ( e^x + e^(-x) ) / 2
 */
@SuppressWarnings("checkstyle:TypeName")
public class cosh extends AbstractFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'cosh': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                cosh retvalue = new cosh();
                retvalue.setSignature("cosh");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(
                (Math.pow(Math.E, c1.getNumber()) + Math.pow(Math.E, -c1.getNumber())) * getMultiplier() / 2);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
