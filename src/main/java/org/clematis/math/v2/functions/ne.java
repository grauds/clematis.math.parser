// Created: 25.03.2004 T 16:23:08
package org.clematis.math.v2.functions;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.clematis.math.v2.parsers.Node;

/**
 * ne (a, b) returns a != b ? 1.0 : 0.0
 */
public class ne extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 1 || getArguments().size() > 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'ne': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);

           /* if( !AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2) )
            {
                ne retvalue = new ne();
                retvalue.setSignature("ne");
                retvalue.addArgument( a1 );
                retvalue.addArgument( a2 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2); */

            //return new Constant ( (( c1.getNumber() != c2.getNumber() ) ? 1.0 : 0.0 ) * getMultiplier() );
            return new Constant(a1.equals(a2) ? "0" : "1");
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
