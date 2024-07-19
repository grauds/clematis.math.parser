// Created: 09.12.2003 T 13:35:41

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

import java.util.Random;

/**
 * Random integer between 0 and n
 *
 * rint(n)
 */
public class rInt extends aFunction2
{
    private static Random rand = new Random( System.currentTimeMillis() );
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
       try
        {
            if( arguments.size() != 1 )
            {
                throw new AlgorithmException("Invalid number of arguments in function 'rInt': " + arguments.size() );
            }
            iExpressionItem a1 = arguments.get(0).calculate();

            if( !AlgorithmUtils.isGoodNumericArgument(a1) )
            {
                rInt retvalue = new rInt();
                retvalue.setSignature("rInt");
                retvalue.addArgument( a1 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            int rd = rand.nextInt( (int) c1.getNumber() );
            return new Constant( rd * getMultiplier() );
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
