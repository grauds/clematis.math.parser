// Created: 04.10.2005 T 15:07:25

package org.clematis.math.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * Counts significant digits in argument
 */
@SuppressWarnings("checkstyle:TypeName")
public class cntSig extends AbstractMathMLFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'cntSig': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                sec retvalue = new sec();
                retvalue.setSignature("cntSig");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(Sig.countSigDigits(c1.getValue(null)) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
