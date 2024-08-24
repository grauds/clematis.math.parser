// Created: 25.03.2004 T 17:21:28
package org.clematis.math.v1.functions;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.algorithm.AlgorithmUtils;
import org.clematis.math.v1.iExpressionItem;
import org.clematis.math.v1.iMultivariantLogic;

import java.util.ArrayList;

/**
 * Given a whole number 0, 1, 2, ... this returns the n'th item in the list
 * (the first item is in position 0, the second in position 1, etc). For example:
 * <p>
 * $prime = switch(rint(5), 2, 3, 5, 7, 11)
 * <p>
 * sets $prime to a random choice from the first 5 primes.
 */
public class Switch extends aFunction2 implements iMultivariantLogic {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException {
        try {
            iExpressionItem a1 = arguments.get(0).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                Switch retvalue = new Switch();
                retvalue.setSignature("switch");
                retvalue.addArgument(a1);
                for (int i = 1; i < arguments.size(); i++) {
                    retvalue.addArgument(arguments.get(i));
                }
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            /**
             * Arguments (n, option1, option2, ... optionn) - size (n + 1)
             */
            if (c1.getNumber() + 1 < arguments.size()) {
                iExpressionItem result = arguments.get(((int) c1.getNumber() + 1)).calculate();
                result.setMultiplier(result.getMultiplier() * getMultiplier());
                return result;
            } else {
                throw new AlgorithmException("Switch index out of bounds: " + c1.getNumber()
                    + " in array of size: " + (arguments.size() - 1));
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }

    /**
     * Gets switched variants.
     *
     * @return switched variants list.
     */
    public ArrayList<iExpressionItem> getVariants() {
        ArrayList<iExpressionItem> variants = new ArrayList<iExpressionItem>();
        if (arguments != null && arguments.size() > 1) {
            for (int i = 1; i < arguments.size(); i++) {
                variants.add(arguments.get(i));
            }
        }
        return variants;
    }
}