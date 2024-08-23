// Created: 05.12.2006 T 15:52:02
package org.clematis.math.v2.cases;

import org.clematis.math.v2.functions.Decimal;
import org.clematis.math.v2.functions.Sig;

/**
 * Test cases that check up decimal formatting
 */
public class DecimalTestCases extends AlgorithmTestCases {
    public String[] getTests() {
        return tests;
    }

    public void testDecimal() throws Exception {
        for (int i = -8; i < 8; i++) {
            System.out.println("FIRST: " + Decimal.round("38763475.809430000", i));
            System.out.println("SECOND: " + Decimal.round("0.809430000", i));
            System.out.println("THIRD: " + Decimal.round("387630000", i));
        }

        System.out.println(Decimal.round("2.00E-9", -5));
        System.out.println(Sig.getSigDigits("22994.11", 2));
    }


    private static final String[] tests = new String[]
        {
            "$p=decimal(0, -0.42604665773);",

            "$a=int(rand(200,500));" +
                "$p=decimal(3,(rand(1,2)));" +
                "$k1=$a*$p;" +
                "$k=int($a*$p);",

            "$x=decimal(2, (rand(1, 1.24)));" +
                "$shis=sig(5,190.00*$x);" +
                "$ssel=sig(5,217.00*$x);",

            "$cra = decimal(1, rand(0.1, 0.4));" +
                "$ca=decimal(1, rand(0.1, 0.4));" +
                "$cr=$cra+$ca;" +
                "$P=int(10*int(rand(3, 8)));" +
                "$xcr=int(rand(10, $P-10));" +
                "$xca=int($P-$xcr);" +
                "$cap=int($cr*$P);" +
                "$B=-$cra*$xca+$cap;" +
                "$bcap=-$B+$cr*$P;",

            "$a=rand(0, 10);" +
                "$b=rand(-1,1);" +
                "$c=rand(0,99);" +
                "$a1=rint(2);" +
                "$a2=decimal($a1, $a);" +
                "$b2=decimal($a1, $b);" +
                "$c2=decimal($a1, $c);" +
                "$answer=($a2+$b2)*$c2;",
        };
}
