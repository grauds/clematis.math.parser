// Created: 16.02.2005 T 11:16:58

package org.clematis.math.v1.functions;

import org.apache.commons.math3.special.Erf;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.v1.algorithm.AlgorithmUtils;

/**
 * Returns the error function erf(x).
 * <p>
 * The implementation of this method is based on:
 * <ul>
 * <li>
 * <a href="http://mathworld.wolfram.com/Erf.html">
 * Erf</a>, equation (3).</li>
 * </ul>
 */
@SuppressWarnings("checkstyle:TypeName")
public class erf extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'erf': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                cos retvalue = new cos();
                retvalue.setSignature("erf");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            assert c1 != null;
            return new Constant(((Erf.erf(c1.getNumber() / Math.sqrt(2)) + 1) / 2) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
