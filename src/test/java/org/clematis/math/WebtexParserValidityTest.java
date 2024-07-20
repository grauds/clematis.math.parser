// Created: 28.02.2006 T 8:52:42
package org.clematis.math;

import java.io.IOException;
import java.io.StringReader;

import org.clematis.math.parsers.SimpleNode;
import org.clematis.math.parsers.webtex.ParseException;
import org.clematis.math.parsers.webtex.WebTex2MMLConverter;
import org.clematis.math.parsers.webtex.WebTexParser;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.junit.jupiter.api.Test;

public class WebtexParserValidityTest {
    private final String[] tests = new String[]
        {
            "F_x*cos(d_(were)^(w))*vec(d_(were)^(w))",
            "3*g^2*m*T^2*(1-cos(\\phi_m))/(16*\\pi^2)",
            "f_(fr)",
            "frac((T_(minute)^2)) ((T_(second)^2))",
            "(m*(vec(v)_f)-2)/t_(diff)",
            "\\deltx/\\deltten",
            "frac(g*\\delt(x))(sin(\\theta))",
            "vec(x_(ij)) + $r",
            "vec(2)",
            "vec(x)",
            "15x+130",
            "15*x+130",
            "x15+130",
            "sin(2)",
            "sqrt{x}",
            "root{2}{y}",
            "root{3}{y}",
            "log{e}{x}",
            "log{10}{x}",
            "log{\\pi}{x}",
            "\\pipi",
            "\\mu_(abc)",
            "\\pi",
            "frac(Gm_(earth)m_(raindrop))(r_(earth)^2)",
            "(as)^(def)",
            "(as)_(def)",
            "(5s)_(def)",
            "(a*s)_(def)",
            "(5*s)_(def)",
            "(5*s)_(def)^(2)",
            "s_i_1",
            "s_i1",
            "s_(i1)",
            "v_0sqrt(2g/h/g)",
            "a_i_j",
            //"a_(i_j)",
            "a_(ij)",
            //"a_(i^j)",
            "e-{2x+1}",
            "-1+x",
            "(8x+4)/ (-1+x)",
            "a{+{b-c{*{-y}}}-x}+5",
            "-x^i",
            "-(x+1)/(-2)",
            "e+e^e^e^e^e",
            "({x+1)}",
            "frac{{{v}_{0}^{2}}}{2g}",
            "frac{{v}_{0}^{2}}{2g}",
            //"{x+1}_{y+2}^{3}",
            "14*t+1",
            "f_1f_2",
            "a_b_c",
           // "a_{b_c}",
           // "a_{b_{c+1}}",
            "a1",
            "a_1",
           // "a_2+m_{i+1}^{2n}",
           // "L_{b^i}",
           // "x+\\frac{x}{2}+\\frac{a_2}{\\gamma^2}",
            "a_2_3+b-c-d+e+f/g/h*i*j",
           // "\\frac {a} {b}",
            //"\\sinx*a/(b-c -d) = 10",
           // "\\sinx*a/(b-ce -d*e) = 10",
            "a-bc-d",
           // "x^4\\cos{7x}-7x^5\\sin{7x}",
            "a+b_i\\cdotc_5",
            "2a-(-b)",
           // "6*e^(2*\\sin(3*x))*\\csc(3*\\pi)",
            "\\pie",
            "10e+10",
            "e+e^x+a^e^x",
           // "\\sqrt x",
           // "\\root y x",
            "\\alpha*x",
            "\\alpha_x",
            "\\alpha_(x)",
            "\\Pi\\Omega",
           // "6e^{2\\sin{3x}}\\cos{3x}",
            "a2+2a+A_i_j",
            "((a)/(b))*(m)_(0)*( tan(\\theta)- tan((\\theta)_(0)))",
            "3*g^2*m*T^2*(1-cos(\\phi_m))/(16*\\pi^2)",
            "E_(Al)*(r_0^2/r_1^2-1)*pi*r_0^2",
            "g*delt(t)/sin(\\theta)",
            "g*delt(t*x/3)/( sin(\\theta))",
            "g*cos(t)hat(t)cos(t)/( sin(\\theta))",
            "+a",
           // "\\cos (x)*\\if 4 \"cool\" \"hot\"",
            "switch 3 x y z + a",
            "switch {3} {x} {y} {z} a"
        };

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
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
    public void testAll() throws Exception {
        WebTex2MMLConverter conv = new WebTex2MMLConverter();

        for (String test : tests) {
            System.out.println("expr = " + test);
            Element mml = conv.parseUserWebTexExpr(test);
            System.out.println("MML");
            print(mml);
            System.out.println("\n---------------------------");

            System.out.println("\n---------------------------");
        }
    }


    private void probeExpression(String test) throws ParseException, IOException {
        WebTexParser parser = new WebTexParser(new StringReader(test));
        SimpleNode n = parser.Start();
        assert n != null;
        n.dump(" ");

        WebTex2MMLConverter conv = new WebTex2MMLConverter();
        System.out.println("expr = " + test);
        Element mml = conv.parseUserWebTexExpr(test);
        System.out.println("MML");
        print(mml);
        System.out.println("\n---------------------------");
    }

    private static void print(Element mml) throws IOException {
        XMLOutputter xout = new XMLOutputter();
        xout.output(mml, System.out);
        System.out.flush();
    }
}
