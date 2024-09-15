// Created: 05.12.2006 T 16:30:13
package org.clematis.math.test.cases;

/**
 * Test cases with implicit multiplication
 */
public class ImplicitMultiplicationTestCases extends AlgorithmTestCases {

    private static final String[] TESTS = new String[] {
        "$x=72;"
            + "$answer=$x*79.7;"
            + "$i=9;"
            + "$p=1/2+1/2$i"
    };

    public String[] getTests() {
        return TESTS;
    }
}
