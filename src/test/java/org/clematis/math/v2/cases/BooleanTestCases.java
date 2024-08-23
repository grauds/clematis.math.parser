// Created: 05.12.2006 T 16:26:12
package org.clematis.math.v2.cases;

/**
 * Checks up boolean logic
 */
public class BooleanTestCases extends AlgorithmTestCases {
    private static final String[] tests = new String[]
        {
            "$r=int(1+rint(5));" +
                "$l=int(2+rint(5));" +
                "$c0=4*$l*$r^2;" +
                "$c=int(switch(rint(3), $c0-1-rint($c0-1), $c0, $c0+1+rint($c0)));" +
                "$b1=$c/$r;" +
                "$c1=$l*$c;" +
                "$d=$b1^2-4*$c1;" +
                "$s=int(not(gt($d, 0))+lt($d, 0)+1)"
        };

    public String[] getTests() {
        return tests;
    }
}
