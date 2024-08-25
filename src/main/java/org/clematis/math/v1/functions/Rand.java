// Created: Feb 14, 2003 T 12:26:09 PM
package org.clematis.math.v1.functions;

import java.util.Random;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.v1.algorithm.AlgorithmUtils;

/**
 * rand(m,n)
 * Returns a randomly chosen real number between m and n (inclusive).
 * <p>
 * rand(m,n,k)
 * Returns a randomly chosen real number between m and n expressed to k significant places.
 */
public class Rand extends aFunction2 {

    public static final String SIGNATURE = "Rand";

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final int ARGUMENTS_NUMBER = 3;

    /**
     * Returns a randomly chosen real number between m and n (inclusive) in
     * case of two arguments and expressed to k significant places in case of
     * three arguments.
     *
     * @return <code>Constant</code> with randomly chosen real number.
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() <= 1 || arguments.size() > ARGUMENTS_NUMBER) {
                throw new AlgorithmException("Invalid number of arguments in function 'Rand': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            IExpressionItem a2 = arguments.get(1).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                Rand retvalue = new Rand();
                retvalue.setSignature(SIGNATURE);
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            IExpressionItem a3 = null;
            if (arguments.size() == ARGUMENTS_NUMBER) {
                a3 = arguments.get(2).calculate();
                if (!AlgorithmUtils.isGoodNumericArgument(a3)) {
                    Rand retvalue = new Rand();
                    retvalue.setSignature(SIGNATURE);
                    retvalue.addArgument(a1);
                    retvalue.addArgument(a2);
                    retvalue.addArgument(a3);
                    retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                    return retvalue;
                }
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            Constant c3 = null;
            if (a3 != null) {
                c3 = AlgorithmUtils.getNumericArgument(a3);
            }
            double rd = RANDOM.nextDouble();
            double newValue = c1.getNumber() * (1.0 - rd) + c2.getNumber() * rd;
            if (c3 != null) {
                int sig = (int) c3.getNumber();
                Constant retvalue = new Constant(newValue, sig);
                retvalue.setMultiplier(getMultiplier());
                retvalue.setSdEnable(true);
                return retvalue;
            } else {
                return new Constant(newValue * getMultiplier());
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
