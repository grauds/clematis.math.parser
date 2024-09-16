package org.clematis.math.algorithm;

import java.io.StringReader;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.parsers.string.ParseException;
import org.clematis.math.parsers.string.SimpleNode;
import org.clematis.math.parsers.string.StringMathParser;
import org.clematis.math.v1.parsers.v1.ExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TwoParsersTest {


    @Test
    public void testNaiveAndStringMathParsers() throws ParseException, AlgorithmException {

        String expression = "9 + 8 + 98 + 77 + sin(8) + pi";
        String answer = "196.130950900213";

        StringMathParser parser = new StringMathParser(new StringReader(expression));
        SimpleNode n = parser.Start();
        Assertions.assertNotNull(n);
        IExpressionItem root = n.getExpressionItem();
        Assertions.assertNotNull(root);
        Assertions.assertEquals(answer, root.calculate().toString());

        ExpressionParser expressionParser = new ExpressionParser(expression);
        root = expressionParser.parse();
        String secondAnswer = root.calculate().toString();
        Assertions.assertNotEquals(answer, secondAnswer);
        Assertions.assertEquals("192", secondAnswer);

    }
}
