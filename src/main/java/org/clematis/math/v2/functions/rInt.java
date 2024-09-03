// Created: 09.12.2003 T 13:35:41
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

import java.util.Random;

/**
 * Random integer between 0 and n
 * <p>
 * rint(n)
 */
public class rInt extends aFunction2 {
    private static final Random rand = new Random(System.currentTimeMillis());

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() != 1) {
                throw new AlgorithmException(
                    "Invalid number of arguments in function 'rInt': " + getArguments().size());
            }
            Node a1 = getArguments().get(0).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                rInt retvalue = new rInt();
                retvalue.setSignature("rInt");
                retvalue.addArgument(a1);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            int rd = rand.nextInt((int) c1.getNumber());
            return new Constant(rd);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
