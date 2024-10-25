// Created: 05.12.2006 T 16:18:42
package org.clematis.math.test.cases;

/**
 * Assign and declarations of parameters test cases
 */
public class ParameterAssignTestCases extends AlgorithmTestCases {

    private static final String[] TESTS = new String[] {
      /*  "$d=rand(50, 70, 3);" +
            "$der=$d;",

        "$ar=sig(7, 1838000 + rint(25)*1000);" +
            "$allow=sig(4,8000 + rint(10)*100);" +
            "$ts=sig(8,26300000 + rint(10)*10000);" +
            "$rate=1.5 + (rint(5)/10);" +
            "$bd1=$ts*.75*($rate/100);" +
            "$bd=sig(6, $bd1);" +
            "$allow=sig(6,$bd+$allow);" +
            "$arn=sig(7,$ar-$allow);",
*/
        "$global_a=int(rand(1,10));"
            + "$global_da=if(eq($global_a,1),\"\",\"$global_a\");"
            + "$global_b=int(rand(1,10));"
            + "$global_db=if(eq($global_b,1),\"\",\"$global_b\");"
            + "$global_sc=switch(rint(2),\"+\",\"-\");"
            + "$global_c=int(rand(1,10));"
            + "$global_dc=if(eq($global_c,1),\"\",\"$global_c\");"
            + "$global_n=int(rand(3,7));"
            + "$globalnb=int($global_n*$global_b);"
            + "$globalna=int($global_n - 1);"
            + "$global_nc=int($global_n - 2);"
            + "$global_exp=if(eq($global_nc,1),\"\",\"^$global_nc\");"
            + "$global_nn1b=int($globalnb*$globalna);"
    };

    public String[] getTests() {
        return TESTS;
    }
}
