// Created: Mar 25, 2003 T 1:24:37 PM
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.utils.AlgorithmUtils;

import java.util.ArrayList;

/**
 * if (a, b, c)    returns ( a != 0) ? b : c
 */
public class If extends aFunction2 implements iChooserFunction {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 2 || getArguments().size() > 3) {
                throw new AlgorithmException("Invalid number of arguments in function 'if': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);
            Node a3 = getArguments().get(2).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1)) {
                If retvalue = new If();
                retvalue.setSignature("If");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.addArgument(a3);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return (c1.getNumber() != 0) ? a2 : a3;
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }

    /**
     * Gets list of possible returned values.
     *
     * @return list of possible returned values.
     */
    public ArrayList<Node> getVariants() {
        ArrayList<Node> variants = new ArrayList<Node>();
        if (getArguments().size() >= 2) {
            for (int i = 1; i < getArguments().size(); i++) {
                variants.add(getArguments().get(i));
            }
        }
        return variants;
    }
}
