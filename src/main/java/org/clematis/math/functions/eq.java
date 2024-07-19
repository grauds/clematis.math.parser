// Created: 25.03.2004 T 16:25:43
package org.clematis.math.functions;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;

/**
 * Returns 1.0 if a and b are equal, otherwise returns 0.0;
 * i.e. it returns a == b ? 1.0 : 0.0.
 */
public class eq extends aFunction2 {
    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 1 || getArguments().size() > 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'eq': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);
/*          if( !AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2) )
            {
                eq retvalue = new eq();
                retvalue.setSignature("eq");
                retvalue.addArgument( a1 );
                retvalue.addArgument( a2 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);*/

            return new Constant(a1.equals(a2) ? "1" : "0");
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception e) {
            //log.error( e, e );
        }
        return null;
    }
}
