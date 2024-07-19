// Created: 24.01.2007 T 10:37:13
package org.clematis.math;

import java.io.StringReader;

import org.clematis.math.parsers.SimpleNode;
import org.clematis.math.parsers.string.ParseException;
import org.clematis.math.parsers.string.StringMathParser;
import org.clematis.math.reference.ExpressionParser;
import org.junit.jupiter.api.Test;

public class StringParserPerformanceTest
{
    private String[] performanceTests = new String[]
    {
        "(35+rint(20))",
        "$n + 1",
        "rint(20)",
        "$n/720",
        "sig(2, sqrt(19.6*$d2t*sin($ang)-(2*$Etht/$m)))",
        "(35+rint(20))",
        "$n-1",
        "($n*$n1*$n2*$n3*$n4*$n5)/720",
        "int(65+5*rint(5))",
        "int(16*$t)",
        "int($t-60)",
        "$t/10",
        "eq($test, int($test))",
        "int(switch($t0, $t2, 5, 10))",
        "int(16*$t0)",
        "int($t+$t0)",
        "int($t8*$t2)",
        "rand(45,55,3)",
        "rand(1,2,3)",
        "(6*$x2^2)/(4*pi*($x1*10^(-6))^2)",
        "2600*4*pi/3*($x1*10^(-6))^3",
        "1000000*((6*$x2^2)/(4*pi*($x1*10^(-6))^2))*(2600*4*pi/3*($x1*10^(-6))^3)",
        "lsu(2,$a)",
        "$cra+$ca",
        "int(rand(10, $P-10))",
        "int($P-$xcr)",
        "int($cr*$P)",
        "-$cra*$xca+$cap",
        "-$B+$cr*$P",
        "sig(2, if($static, 600, rand(500, 700, 2)))",
        "sig(2, if($static, 20, rand(15, 25, 2)))",
        "sig(2, if($static, 20, rand(15, 30, 2)))",
        "sig(2, if($static, 8, sqrt(9.8*$c*$R+19.6*$R*(1-cos($ang)))))",
        "0.5*$vB*$vB",
        "$KB-9.8*$R*(1-cos($ang))",
        "sig(2, sqrt(2*$KA1))",
        "sig(2, sqrt(2*$KB2))",
        "sig(2, if($static, 700, rand(1.3*$W1, 1.6*$W1, 2)))",
        "lsu(2,$vA1 )",
    };

    @Test
    public void testPerformance()
    {
        for (int i = 0; i < performanceTests.length; i++)
        {
            try
            {
                System.out.println("--------------------");
                System.out.println("--------------------");
                System.out.println( performanceTests[i] );
                System.out.println("--------------------");
                long start = System.nanoTime();
                probeExpression(performanceTests[i]);
                long end = System.nanoTime();
                double n = (end - start);
            /*    System.out.println("New parser: " + n + " nanosecs");
                System.out.println("--------------------");     */
                start = System.nanoTime();
                new ExpressionParser(performanceTests[i]).parse();
                end = System.nanoTime();
                double o = (end - start);
              /*  System.out.println("Legacy parser: " + o + " nanosecs");
                System.out.println("--------------------");    */
                System.out.print(" *** " + (double)(n / o)*100 + "%");
                System.out.println("--------------------");
                System.out.println("--------------------");  
            }
            catch(Exception ex)
            {
              /*  System.out.println("Cannot parse " + performanceTests[i]);
                System.out.println("--------------------");
                System.out.println("--------------------"); */
            }
        }
    }

    private void probeExpression(String test) throws ParseException
    {
        StringMathParser parser = new StringMathParser(new StringReader(test));
        SimpleNode n = parser.Start();
    }
}
