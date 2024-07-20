// Created: 24.01.2007 T 11:39:22
package org.clematis.math;

import java.math.BigDecimal;

public class ErrorsTest extends BackwardCompatibilityTest {
    private static final String[] errors = new String[]
        {
            "$cra = decimal(1, rand(0.1, 0.4));" +
                "$ca=decimal(1, rand(0.1, 0.4));" +
                "$cr=$cra+$ca;" +
                "$P=int(10*int(rand(3, 8)));" +
                "$xcr=int(rand(10, $P-10));" +
                "$xca=int($P-$xcr);" +
                "$cap=int($cr*$P);" +
                "$B1=-$cra*$xca;" +
                "$B2=$B1+$cap;" +
                "$B=-$cra*$xca+$cap;" +
                "$bcap=-$B+$cr*$P;",
        };

    /**
     * Tests out the zero trail in $B parameter in new maths against old maths
     *
     * @throws Exception
     */
    public void testZeroTrail() throws Exception {
        /**
         * Big decimal retains the precision expressed in decimal places
         */
        BigDecimal bd1 = new BigDecimal("0.02");
        BigDecimal bd2 = new BigDecimal("200");
        System.out.println(bd1.multiply(bd2));
        System.out.println(new Double(2).intValue());
       /* for ( int i = 0; i < 50; i++ )
        {
            probeAlgorithm( errors[0] );
        }*/
    }
}
