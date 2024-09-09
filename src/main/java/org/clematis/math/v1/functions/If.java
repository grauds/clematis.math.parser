// $Id: If.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Mar 25, 2003 T 1:24:37 PM

package org.clematis.math.v1.functions;

import java.util.ArrayList;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IOptions;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * if (a, b, c)    returns ( a != 0) ? b : c
 */
public class If extends AbstractMathMLFunction implements IOptions {

    public static final int NUMBER_OF_ARGUMENTS = 3;

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != NUMBER_OF_ARGUMENTS) {
                throw new AlgorithmException("Invalid number of arguments in function 'if': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            IExpressionItem a2 = arguments.get(1).calculate();
            IExpressionItem a3 = arguments.get(2).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                If retvalue = new If();
                retvalue.setSignature("If");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.addArgument(a3);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);

            IExpressionItem result = (c1.getNumber() != 0) ? a2 : a3;
            result.setMultiplier(result.getMultiplier() * getMultiplier());
            return result;
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }

    /**
     * Gets list of possible returned values.
     *
     * @return list of possible returned values.
     */
    public ArrayList<IExpressionItem> getOptions() {
        ArrayList<IExpressionItem> variants = new ArrayList<IExpressionItem>();
        if (arguments.size() >= 2) {
            for (int i = 1; i < arguments.size(); i++) {
                variants.add(arguments.get(i));
            }
        }
        return variants;
    }
}
