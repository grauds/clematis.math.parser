// Created: 16.02.2005 T 11:16:58
package org.clematis.math.functions;

import org.apache.commons.math3.special.Erf;
import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.clematis.math.utils.AlgorithmUtils;

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
public class erf extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'erf': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                cos retvalue = new cos();
                retvalue.setSignature("erf");
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant((Erf.erf(c1.getNumber() / Math.sqrt(2)) + 1) / 2);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
