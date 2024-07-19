// Created: 29.06.2005 T 15:21:38
package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

/**
 * arccosh(x)=log( x + (x^2 - 1)^(1/2) )
 */
public class arccosh extends aFunction
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
                throw new AlgorithmException("Invalid number of arguments in function " + signature
                                                         + ": " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();
            if( !AlgorithmUtils.isGoodNumericArgument(a1) )
            {
                arccosh retvalue = new arccosh();
                retvalue.setSignature( this.signature );
                retvalue.addArgument( a1 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);

            return new Constant ( Math.log10( c1.getNumber() +
                                  Math.sqrt( Math.pow(c1.getNumber(), 2) - 1 ) ) * getMultiplier() );
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
