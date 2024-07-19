// $Id: rList.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: 29.10.2003 T 15:54:27

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.iExpressionItem;

import java.util.Random;
/**
 * Random list - chooses items from list randomly.
 */
public class rList extends aFunction2
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
            if( arguments.size() <= 0 )
            {
                throw new AlgorithmException("Invalid number of arguments in function 'rList': " + arguments.size() );
            }

            int number = rand.nextInt( arguments.size() - 1);
            iExpressionItem a = arguments.get(number).calculate();
            a.setMultiplier( a.getMultiplier() * getMultiplier() );
            return a;
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
