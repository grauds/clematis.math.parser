// $Id: Int.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Aug 26, 2003 T 12:23:40 PM

package org.clematis.math.v1.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * int ( x )
 */
public class Int extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'int': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                Int retvalue = new Int();
                retvalue.setSignature("Int");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(Double.valueOf(c1.getNumber() * getMultiplier()).intValue());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
