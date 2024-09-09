// Created: Feb 14, 2003 T 12:26:09 PM
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

import java.util.Random;

/**
 * rand(m,n)
 * Returns a randomly chosen real number between m and n (inclusive).
 * <p>
 * rand(m,n,k)
 * Returns a randomly chosen real number between m and n expressed to k significant places.
 */
public class Rand extends aFunction2 {
    private static final Random rand = new Random(System.currentTimeMillis());

    /**
     * Returns a randomly chosen real number between m and n (inclusive) in
     * case of two arguments and expressed to k significant places in case of
     * three arguments.
     *
     * @param parameterProvider
     * @return <code>Constant</code> with randomly chosen real number.
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 1 || getArguments().size() > 3) {
                throw new AlgorithmException(
                    "Invalid number of arguments in function 'Rand': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                Rand retvalue = new Rand();
                retvalue.setSignature("Rand");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                return retvalue;
            }

            Node a3 = null;
            if (getArguments().size() == 3) {
                a3 = getArguments().get(2).calculate(parameterProvider);
                if (!AlgorithmUtils.isGoodNumericArgument(a3)) {
                    Rand retvalue = new Rand();
                    retvalue.setSignature("Rand");
                    retvalue.addArgument(a1);
                    retvalue.addArgument(a2);
                    retvalue.addArgument(a3);
                    return retvalue;
                }
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            Constant c3 = null;
            if (a3 != null) {
                c3 = AlgorithmUtils.getNumericArgument(a3);
            }
            double rd = rand.nextDouble();
            double newValue = c1.getNumber() * (1.0 - rd) + c2.getNumber() * rd;
            if (c3 != null) {
                int sig = (int) c3.getNumber();
                Constant retvalue = new Constant(newValue, sig);
                retvalue.setSdEnable(true);
                return retvalue;
            } else {
                return new Constant(newValue);
            }
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }

    public int getSigDigits() {
        if (getArguments() != null && getArguments().size() == 3) {
            Node sigArg = getArguments().get(2);
            if (sigArg instanceof Constant) {
                return (int) ((Constant) sigArg).getNumber();
            }
        }
        // undefined state
        return -1;
    }
}
