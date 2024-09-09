// Created: 29.06.2005 T 15:43:23
package org.clematis.math.v1.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * tanh(x) = sinh(x) / cosh(x)
 */
@SuppressWarnings("checkstyle:TypeName")
public class tanh extends AbstractFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function " + signature
                    + ": " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                tanh retvalue = new tanh();
                retvalue.setSignature(this.signature);
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            double a = Math.pow(Math.E, c1.getNumber()) - Math.pow(Math.E, -c1.getNumber()) / 2;
            double b = Math.pow(Math.E, c1.getNumber()) + Math.pow(Math.E, -c1.getNumber()) / 2;
            return new Constant(a * getMultiplier() / b);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
