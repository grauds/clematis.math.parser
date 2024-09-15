// Created: 05.12.2006 T 15:52:02
package org.clematis.math.test.cases;

/**
 * Test cases that check up decimal formatting
 */
public class DecimalTestCases extends AlgorithmTestCases {

    private static final String[] TESTS = new String[]
    {
        "$p=decimal(0, -0.42604665773);",

        "$a=int(rand(200,500));"
            + "$p=decimal(3,(rand(1,2)));"
            + "$k1=$a*$p;"
            + "$k=int($a*$p);",

        "$x=decimal(2, (rand(1, 1.24)));"
            + "$shis=sig(5,190.00*$x);"
            + "$ssel=sig(5,217.00*$x);",

        "$cra = decimal(1, rand(0.1, 0.4));"
            + "$ca=decimal(1, rand(0.1, 0.4));"
            + "$cr=$cra+$ca;"
            + "$P=int(10*int(rand(3, 8)));"
            + "$xcr=int(rand(10, $P-10));"
            + "$xca=int($P-$xcr);"
            + "$cap=int($cr*$P);"
            + "$B=-$cra*$xca+$cap;"
            + "$bcap=-$B+$cr*$P;",

        "$a=rand(0, 10);"
            + "$b=rand(-1,1);"
            + "$c=rand(0,99);"
            + "$a1=rint(2);"
            + "$a2=decimal($a1, $a);"
            + "$b2=decimal($a1, $b);"
            + "$c2=decimal($a1, $c);"
            + "$answer=($a2+$b2)*$c2;",
    };

    public String[] getTests() {
        return TESTS;
    }

}
