// $Id: arccos.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Mar 25, 2003 T 3:06:17 PM

package org.clematis.math.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.utils.AlgorithmUtils;

/**
 * arccos( a )
 */
@SuppressWarnings("checkstyle:TypeName")
public class arccos extends AbstractFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'arccos': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                arccos retvalue = new arccos();
                retvalue.setSignature("arccos");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);

            return new Constant(Math.acos(c1.getNumber()) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
