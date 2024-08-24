// Created: 29.06.2005 T 15:35:57

package org.clematis.math.v1.functions;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.algorithm.AlgorithmUtils;
import org.clematis.math.v1.iExpressionItem;

/**
 * arcsinh(x)=log( x + (x^2 + 1)^(1/2) )
 */
public class arcsinh extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function " + signature
                    + ": " + arguments.size());
            }

            iExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                arcsinh retvalue = new arcsinh();
                retvalue.setSignature(this.signature);
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(Math.log10(c1.getNumber() +
                Math.sqrt(Math.pow(c1.getNumber(), 2) + 1)) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}