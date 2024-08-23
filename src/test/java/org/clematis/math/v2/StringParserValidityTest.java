// Created: 30.01.2006 T 14:56:42
package org.clematis.math.v2;

import java.io.StringReader;

import org.clematis.math.v2.parsers.SimpleNode;
import org.clematis.math.v2.parsers.string.ParseException;
import org.clematis.math.v2.parsers.string.StringMathParser;
import org.clematis.math.v2.parsers.string.WebTexPrinter;
import org.junit.jupiter.api.Test;

public class StringParserValidityTest {
    private final String[] tests = new String[]
        {
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

    private final String[] alg_tests = new String[]
        {
            "9 + 8 + 98 + 77 + sin(8) + pi",
            "t_(dcosf)",
            //"acosx",
            "decisin(89.876)",
            "decimal(0, 89.876)"
        };

    @Test
    public void testAlgorithm() throws Exception {
        for (String alg_test : alg_tests) {
            StringMathParser parser = new StringMathParser(new StringReader(alg_test));
            SimpleNode n = parser.Start();
            assert n != null;
            System.out.println(n.calculate());
        }
    }

    @Test
    public void testExpression0() throws Exception {
        probeExpression(tests[0]);
    }

    @Test
    public void testExpression1() throws Exception {
        probeExpression(tests[1]);
    }

    @Test
    public void testExpression2() throws Exception {
        probeExpression(tests[2]);
    }

    @Test
    public void testExpression3() throws Exception {
        probeExpression(tests[3]);
    }

    @Test
    public void testExpression4() throws Exception {
        probeExpression(tests[4]);
    }

    @Test
    public void testExpression5() throws Exception {
        probeExpression(tests[5]);
    }

    @Test
    public void testExpression6() throws Exception {
        probeExpression(tests[6]);
    }

    @Test
    public void testExpression7() throws Exception {
        probeExpression(tests[7]);
    }

    @Test
    public void testExpression8() throws Exception {
        probeExpression(tests[8]);
    }

    @Test
    public void testExpression9() throws Exception {
        probeExpression(tests[9]);
    }

    @Test
    public void testExpression10() throws Exception {
        probeExpression(tests[10]);
    }

    @Test
    public void testExpression11() throws Exception {
        probeExpression(tests[11]);
    }

    @Test
    public void testExpression12() throws Exception {
        probeExpression(tests[12]);
    }

    @Test
    public void testExpression13() throws Exception {
        probeExpression(tests[13]);
    }

    @Test
    public void testExpression14() throws Exception {
        probeExpression(tests[14]);
    }

    @Test
    public void testExpression15() throws Exception {
        probeExpression(tests[15]);
    }

    @Test
    public void testExpression16() throws Exception {
        probeExpression(tests[16]);
    }

    @Test
    public void testAll() throws Exception {
        for (String test : tests) {
            probeExpression(test);
        }
    }

    private void probeExpression(String test) throws ParseException {
        StringMathParser parser = new StringMathParser(new StringReader(test));
        SimpleNode n = parser.Start();

        System.out.println("------------");

        System.out.println(test);
        StringBuilder sb = new StringBuilder();
        assert n != null;
        WebTexPrinter.print(n, sb);
        System.out.println("------------");
        System.out.println(sb);
        System.out.println("------------");
        n.dump("");
        System.out.println("------------");
    }
}
