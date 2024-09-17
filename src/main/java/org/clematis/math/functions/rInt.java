// Created: 09.12.2003 T 13:35:41

package org.clematis.math.functions;

import java.util.Random;

import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * Random integer between 0 and n
 * <p>
 * rint(n)
 */
@SuppressWarnings("checkstyle:TypeName")
public class rInt extends AbstractMathMLFunction {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'rInt': " + arguments.size());
            }
            IExpressionItem a1 = arguments.get(0).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                rInt retvalue = new rInt();
                retvalue.setSignature("rInt");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            int rd = RANDOM.nextInt((int) c1.getNumber());
            return new Constant(rd * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
