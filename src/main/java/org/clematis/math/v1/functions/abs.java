// $Id: abs.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Mar 25, 2003 T 2:41:07 PM

package org.clematis.math.v1.functions;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.v1.utils.AlgorithmUtils;
/**
 * abs ( a )
 */
@SuppressWarnings("checkstyle:TypeName")
public class abs extends aFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 1) {
                throw new AlgorithmException("Invalid number of arguments in function 'abs': " + arguments.size());
            }
            IExpressionItem a1 = arguments.get(0).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                abs retvalue = new abs();
                retvalue.setSignature("abs");
                retvalue.addArgument(a1);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant(Math.abs(c1.getNumber()) * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }
}
