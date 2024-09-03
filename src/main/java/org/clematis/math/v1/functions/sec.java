// $Id: sec.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Mar 25, 2003 T 4:17:16 PM

package org.clematis.math.v1.functions;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.XMath;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * sec( a )
 */
@SuppressWarnings("checkstyle:TypeName")
public class sec extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'sec': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                sec retvalue = new sec();
                retvalue.setSignature("sec");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(XMath.sec(c1.getNumber()) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
