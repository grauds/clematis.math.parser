// Created: 30.01.2006 T 14:56:42
package org.clematis.math.algorithm;

import java.io.StringReader;

import org.clematis.math.parsers.string.ParseException;
import org.clematis.math.parsers.string.SimpleNode;
import org.clematis.math.parsers.string.StringMathParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StringMathParserLineTest {

    @SuppressWarnings("checkstyle:LineLength")
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

    private void parseLine(String test) throws ParseException {

        StringMathParser parser = new StringMathParser(new StringReader(test));
        SimpleNode n = parser.Start();
        Assertions.assertNotNull(n);
        Assertions.assertNotNull(n.getExpressionItem());
    }

    @Test
    public void testExpression0() throws ParseException {
        parseLine(tests[0]);
    }

    @Test
    public void testExpression1() throws ParseException {
        parseLine(tests[1]);
    }

    @Test
    public void testExpression2() throws ParseException {
        parseLine(tests[2]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression3() throws ParseException {
        parseLine(tests[3]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression4() throws ParseException {
        parseLine(tests[4]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression5() throws ParseException {
        parseLine(tests[5]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression6() throws ParseException {
        parseLine(tests[6]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression7() throws ParseException {
        parseLine(tests[7]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression8() throws ParseException {
        parseLine(tests[8]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression9() throws ParseException {
        parseLine(tests[9]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression10() throws ParseException {
        parseLine(tests[10]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression11() throws ParseException {
        parseLine(tests[11]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression12() throws ParseException {
        parseLine(tests[12]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression13() throws ParseException {
        parseLine(tests[13]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression14() throws ParseException {
        parseLine(tests[14]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression15() throws ParseException {
        parseLine(tests[15]);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression16() throws ParseException {
        parseLine(tests[16]);
    }

    @Test
    public void testAll() throws ParseException {
        for (String test : tests) {
            parseLine(test);
        }
    }
}
