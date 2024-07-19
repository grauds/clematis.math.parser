// $Id: If.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Mar 25, 2003 T 1:24:37 PM

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;
import org.clematis.math.reference.iMultivariantLogic;

import java.util.ArrayList;
/**
 * if (a, b, c)    returns ( a != 0) ? b : c
 *
 */
public class If extends aFunction2 implements iMultivariantLogic
{
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
            if( arguments.size() <= 2 || arguments.size() > 3 )
                throw new AlgorithmException("Invalid number of arguments in function 'if': " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();
            iExpressionItem a2 = arguments.get(1).calculate();
            iExpressionItem a3 = arguments.get(2).calculate();

            if( !AlgorithmUtils.isGoodNumericArgument(a1) )
            {
                If retvalue = new If();
                retvalue.setSignature("If");
                retvalue.addArgument( a1 );
                retvalue.addArgument( a2 );
                retvalue.addArgument( a3 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);

            iExpressionItem result = ( c1.getNumber() != 0 ) ? a2 : a3;
            result.setMultiplier( result.getMultiplier() * getMultiplier() );
            return result;
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
    /**
     * Gets list of possible returned values.
     * @return list of possible returned values.
     */
    public ArrayList<iExpressionItem> getVariants()
    {
        ArrayList<iExpressionItem> variants = new ArrayList<iExpressionItem>();
        if( arguments.size() >= 2 )
        {
            for( int i=1; i<arguments.size(); i++ )
            {
                variants.add(arguments.get(i));
            }
        }
        return variants;
    }
}
