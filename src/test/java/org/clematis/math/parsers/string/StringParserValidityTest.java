// Created: 30.01.2006 T 14:56:42
package org.clematis.math.parsers.string;

import java.io.StringReader;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.parsers.ExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringParserValidityTest {

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

    private final String[] answers = new String[] {
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    };

    @Test
    public void testAlgorithm() throws Exception {

        String expression = "9 + 8 + 98 + 77 + sin(8) + pi";
        String answer = "196.130950900213";

        StringMathParser parser = new StringMathParser(new StringReader(expression));
        SimpleNode n = parser.Start();
        Assertions.assertNotNull(n);
        Assertions.assertNotNull(n.getExpressionItem());
        Assertions.assertEquals(answer, n.getExpressionItem().calculate().toString());

        ExpressionParser expressionParser = new ExpressionParser(expression);
        IExpressionItem root = expressionParser.parse();
        String secondAnswer = root.calculate().toString();
        Assertions.assertNotEquals(answer, secondAnswer);
        Assertions.assertEquals("192", secondAnswer);

    }

    @Test
    public void testExpression0() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(0);
    }

    @Test
    public void testExpression1() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(1);
    }

    @Test
    public void testExpression2() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(2);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression3() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(3);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression4() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(4);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression5() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(5);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression6() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(6);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression7() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(7);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression8() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(8);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression9() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(9);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression10() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(10);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression11() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(11);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression12() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(12);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression13() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(13);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression14() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(14);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression15() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(15);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Test
    public void testExpression16() throws AlgorithmException, ParseException {
        testExpressionWithTwoParsers(16);
    }

    private void testExpressionWithTwoParsers(int i) throws AlgorithmException, ParseException {

        StringMathParser parser = new StringMathParser(new StringReader(tests[i]));
        SimpleNode n = parser.Start();
        Assertions.assertNotNull(n);
        Assertions.assertNotNull(n.getExpressionItem());
        Assertions.assertEquals(answers[i], n.getExpressionItem().calculate().toString());

        ExpressionParser expressionParser = new ExpressionParser(tests[i]);
        IExpressionItem root = expressionParser.parse();
        String secondAnswer = root.calculate().toString();
        Assertions.assertEquals(answers[i], secondAnswer);
    }
}
