// $Id: rList.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: 29.10.2003 T 15:54:27

package org.clematis.math.v1.functions;

import java.util.Random;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;

/**
 * Random list - chooses items from list randomly.
 */
@SuppressWarnings("checkstyle:TypeName")
public class rList extends aFunction2 {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() <= 0) {
                throw new AlgorithmException("Invalid number of arguments in function 'rList': " + 0);
            }

            int number = RANDOM.nextInt(arguments.size() - 1);
            IExpressionItem a = arguments.get(number).calculate();
            a.setMultiplier(a.getMultiplier() * getMultiplier());
            return a;
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
