// Created: 25.03.2004 T 16:27:30

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

/**
 * Returns 1.0 if a is equal to 0.0, otherwise returns 1.0;
 * i.e. it returns a == 0.0 ? 1.0 : 0.0.
 */
public class not extends aFunction2
{
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
           if( arguments.size() > 1 )
                throw new AlgorithmException("Invalid number of arguments in function 'not': " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();

            if( !AlgorithmUtils.isGoodNumericArgument(a1) )
            {
                not retvalue = new not();
                retvalue.setSignature("not");
                retvalue.addArgument( a1 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant ( (( c1.getNumber() == 0.0 ) ? 1.0 : 0.0 ) * getMultiplier() );
        }
        catch(AlgorithmException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex.toString() );
        }
    }
}

