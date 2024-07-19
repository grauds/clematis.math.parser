// Created: 25.03.2004 T 16:25:43

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.iExpressionItem;

/**
 * Returns 1.0 if a and b are equal, otherwise returns 0.0;
 * i.e. it returns a == b ? 1.0 : 0.0.
 */
public class eq extends aFunction2
{
   /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
       if( arguments.size() <= 1 || arguments.size() > 2 )
            throw new AlgorithmException("Invalid number of arguments in function 'eq': " + arguments.size() );

        iExpressionItem a1 = arguments.get(0).calculate();
        iExpressionItem a2 = arguments.get(1).calculate();
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

        return new Constant ( ( a1.equals(a2) ? 1.0 : 0.0 ) * getMultiplier() );
    }
}
