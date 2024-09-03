// Created: 25.03.2004 T 17:21:28
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

import java.util.ArrayList;

/**
 * Given a whole number 0, 1, 2, ... this returns the n'th item in the list
 * (the first item is in position 0, the second in position 1, etc). For example:
 * <p>
 * $prime = switch(rint(5), 2, 3, 5, 7, 11)
 * <p>
 * sets $prime to a random choice from the first 5 primes.
 */
public class Switch extends aFunction2 implements iChooserFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            Node a1 = getArguments().get(0).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                Switch retvalue = new Switch();
                retvalue.setSignature("switch");
                retvalue.addArgument(a1);
                for (int i = 1; i < getArguments().size(); i++) {
                    retvalue.addArgument(getArguments().get(i));
                }
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            /**
             * Arguments (n, option1, option2, ... optionn) - size (n + 1)
             */
            if (c1.getNumber() + 1 < getArguments().size()) {
                return getArguments().get(((int) c1.getNumber() + 1)).calculate(parameterProvider);
            } else {
                throw new AlgorithmException("Switch index out of bounds: " + c1.getNumber()
                    + " in array of size: " + (getArguments().size() - 1));
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }

    /**
     * Gets switched variants.
     *
     * @return switched variants list.
     */
    public ArrayList<Node> getVariants() {
        ArrayList<Node> variants = new ArrayList<Node>();
        if (getArguments() != null && getArguments().size() > 1) {
            for (int i = 1; i < getArguments().size(); i++) {
                variants.add(getArguments().get(i));
            }
        }
        return variants;
    }
}
