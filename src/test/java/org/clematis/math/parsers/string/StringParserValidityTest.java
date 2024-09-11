// Created: 30.01.2006 T 14:56:42
package org.clematis.math.parsers.string;

import java.io.StringReader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringParserValidityTest {
    private final String[] tests = new String[] {
        "if(gt($b,0),\"+\", if(eq($b,0), \"\", \"-\"))",
        "si",
        "d_(were)+d_(9)+d_(k)",
        "switch(5, \"hhh$p\", \"hhh$p\")",
        "sin(3)",
        "x+(sin(${x}/2)+a/9 * x ^ 3)",
        "x_(rsgn)$x_(rsgn)*$x_a*(x*$x_r^(s0)*$x_a)*(x*$x_(rsgn)*$x_(abc))+$b*(x*$d^f*$x0_a^2)*(x*$x_r*$x_a)+$b*(x*$x_r*$x_a)+$b+sin(x)",
        "-ln($a)*pi/(x*(ln(x)^2)) + (1 - 8 )",
        "-ln(((($a+1)*($k-9))))*pi/(x*(ln(x)^2))-(x-y)",
        "-ln($a+cos($k*x))/$k + sin(x)",
        "1/(2*(x+1)^(0.5))",
        "2*x^sqrt(u)",
        "F_x*cos(d_(were)^(w))",
        "d_(were)*(w)",
        "F_x*cos(d_(were)^(w e))",
        "F*cos(\\theta)",
        "-F*sin(\\theta)",
        "F_x*cos(d_(were)^(w))*vec(d_(were)^(w))",
        // "t_(dcosf)",
        "cos(x)*if(4, \"cool\", \"hot\")",
        "switch(3, x, y, z) + a",
        // "acosx",
        "-x*4",
        "-(67 + x)*y",
        "+(67 + x)*y",
        "vec(2)"
    };

    private final String[] algTests = new String[] {
        "9 + 8 + 98 + 77 + sin(8) + pi",
        "t_(dcosf)",
        //"acosx",
        "decisin(89.876)",
        "decimal(0, 89.876)"
    };

    @Test
    public void testAlgorithm() throws Exception {
        for (String algTest : algTests) {
            StringMathParser parser = new StringMathParser(new StringReader(algTest));
            SimpleNode n = parser.Start();
            Assertions.assertNotNull(n);
            Assertions.assertNotNull(n.getExpressionItem());
            System.out.println(n.getExpressionItem().calculate());
        }
    }

    @Test
    public void testExpression0() {
        probeExpression(tests[0]);
    }

    @Test
    public void testExpression1() {
        probeExpression(tests[1]);
    }

    @Test
    public void testExpression2() {
        probeExpression(tests[2]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression3() {
        probeExpression(tests[3]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression4() {
        probeExpression(tests[4]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression5() {
        probeExpression(tests[5]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression6() {
        probeExpression(tests[6]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression7() {
        probeExpression(tests[7]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression8() {
        probeExpression(tests[8]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression9() {
        probeExpression(tests[9]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression10() {
        probeExpression(tests[10]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression11() {
        probeExpression(tests[11]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression12() {
        probeExpression(tests[12]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression13() {
        probeExpression(tests[13]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression14() {
        probeExpression(tests[14]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression15() {
        probeExpression(tests[15]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression16() {
        probeExpression(tests[16]);
    }

    @Test
    public void testAll() {
        for (String test : tests) {
            probeExpression(test);
        }
    }

    private void probeExpression(String test) {
        try {
            StringMathParser parser = new StringMathParser(new StringReader(test));
            SimpleNode n = parser.Start();
            System.out.println("------------");
        } catch (ParseException ex) {
            System.out.println(ex);
        }
    }
}
