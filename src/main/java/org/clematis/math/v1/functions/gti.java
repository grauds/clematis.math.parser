// $Id: gti.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Mar 25, 2003 T 1:44:21 PM

package org.clematis.math.v1.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * gti (a, b) returns a >= b ? 1.0 : 0.0
 */
@SuppressWarnings("checkstyle:TypeName")
public class gti extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'gti': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            IExpressionItem a2 = arguments.get(1).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                gt retvalue = new gt();
                retvalue.setSignature("gti");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            return new Constant(((c1.getNumber() >= c2.getNumber()) ? 1.0 : 0.0) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
