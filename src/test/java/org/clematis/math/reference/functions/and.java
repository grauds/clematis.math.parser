// Created: 30.03.2004 T 17:04:01
package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

/**
 * Returns 1.0 if a is all arguments equal to 1.0, otherwise returns 0.0;
 *
 * i.e. it returns a == 1.0 && b == 1.0 && ... ? 1.0 : 0.0.
 */
public class and extends aFunction2
{
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
            boolean good_arguments = true;
            boolean result = true;

            for ( int i = 0; i < arguments.size(); i++ )
            {
                iExpressionItem a = arguments.get(i).calculate();
                if ( !AlgorithmUtils.isGoodNumericArgument(a) )
                {
                    good_arguments = false;
                    break;
                }
                Constant constant = AlgorithmUtils.getNumericArgument(a);
                double value = constant.getNumber();
                if ( value != 1.0 && value != 0.0 )
                {
                    good_arguments = false;
                    break;
                }
                if ( i == 0 )
                {
                    result = value == 1.0 ? true : false;
                }
                else
                {
                    result &= value == 1.0;
                }
            }

            if( !good_arguments )
            {
                and retvalue = new and();
                retvalue.setSignature("and");

                for ( int i = 0; i < arguments.size(); i++ )
                {
                    retvalue.addArgument( arguments.get( i ) );
                }

                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            return new Constant ( ( result ? 1.0 : 0.0 ) * getMultiplier() );
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
