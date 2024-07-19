// Created: 29.06.2005 T 15:03:21

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

/**
 *  sinh(x) = ( e^x - e^(-x) ) / 2
 */
public class sinh extends aFunction
{
    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
           if( arguments.size() != 1 )
                throw new AlgorithmException("Invalid number of arguments in function 'sinh': " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();
            if( !AlgorithmUtils.isGoodNumericArgument(a1) )
            {
                sinh retvalue = new sinh();
                retvalue.setSignature("sinh");
                retvalue.addArgument( a1 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant ( (Math.pow(Math.E, c1.getNumber()) - Math.pow(Math.E, -c1.getNumber())) * getMultiplier() / 2 );
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
