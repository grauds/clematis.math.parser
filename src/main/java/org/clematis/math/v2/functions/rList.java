// Created: 29.10.2003 T 15:54:27
package org.clematis.math.v2.functions;

import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;

import java.util.Random;

/**
 * Random list - chooses items from list randomly.
 */
public class rList extends aFunction2 {
    private static final Random rand = new Random(System.currentTimeMillis());

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 0) {
                throw new AlgorithmException(
                    "Invalid number of arguments in function 'rList': " + getArguments().size());
            }

            int number = rand.nextInt(getArguments().size() - 1);
            return getArguments().get(number).calculate(parameterProvider);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
