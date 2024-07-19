// $Id: exp.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Aug 26, 2003 T 12:20:50 PM

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

/**
 * exp ( x )
 *
 */
public class exp extends aFunction
{
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
           if( arguments.size() != 1 )
                throw new AlgorithmException("Invalid number of arguments in function 'exp': " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();
            if( !AlgorithmUtils.isGoodNumericArgument(a1) )
            {
                exp retvalue = new exp();
                retvalue.setSignature("exp");
                retvalue.addArgument( a1 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            return new Constant ( Math.exp(c1.getNumber()) * getMultiplier() );
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
