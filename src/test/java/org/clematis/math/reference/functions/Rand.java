// Created: Feb 14, 2003 T 12:26:09 PM
package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.iExpressionItem;

import java.util.Random;
/**
 * rand(m,n)
 * Returns a randomly chosen real number between m and n (inclusive).
 *
 * rand(m,n,k)
 * Returns a randomly chosen real number between m and n expressed to k significant places.
 */
public class Rand extends aFunction2
{
    private static Random rand = new Random( System.currentTimeMillis() );
    /**
     * Returns a randomly chosen real number between m and n (inclusive) in
     * case of two arguments and expressed to k significant places in case of
     * three arguments.
     * @return <code>Constant</code> with randomly chosen real number.
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
            if( arguments.size() <= 1 || arguments.size() > 3 )
                throw new AlgorithmException("Invalid number of arguments in function 'Rand': " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();
            iExpressionItem a2 = arguments.get(1).calculate();

            if( !AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2) )
            {
                Rand retvalue = new Rand();
                retvalue.setSignature("Rand");
                retvalue.addArgument( a1 );
                retvalue.addArgument( a2 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }

            iExpressionItem a3 = null;
            if( arguments.size() == 3 )
            {
                a3 = arguments.get(2).calculate();
                if( !AlgorithmUtils.isGoodNumericArgument(a3) )
                {
                    Rand retvalue = new Rand();
                    retvalue.setSignature("Rand");
                    retvalue.addArgument( a1 );
                    retvalue.addArgument( a2 );
                    retvalue.addArgument( a3 );
                    retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                    return retvalue;
                }
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            Constant c3 = null;
            if( a3 != null )
            {
                c3 = AlgorithmUtils.getNumericArgument(a3);
            }
            double rd = rand.nextDouble();
            double newValue = c1.getNumber()*(1.0 - rd) + c2.getNumber()*rd;
            if( c3 != null )
            {
                int sig = (int)c3.getNumber();
                Constant retvalue = new Constant( newValue, sig );
                retvalue.setMultiplier( getMultiplier() );
                retvalue.setSdEnable( true );
                return retvalue;
            }
            else
            {
                return new Constant( newValue * getMultiplier() );
            }
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

    public int getSigDigits()
    {
        if ( arguments != null  && arguments.size() == 3 )
        {
            iExpressionItem sigArg = arguments.get(2);
            if ( sigArg instanceof Constant )
            {
                return (int) ((Constant) sigArg).getNumber();
            }
        }
        // undefined state
        return -1;
    }
}
