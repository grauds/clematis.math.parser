package org.clematis.math.functions;

import org.clematis.math.AlgorithmReader;
import org.clematis.math.algorithm.Algorithm;
import org.clematis.math.parsers.v2.AdvancedParameterFactory;
import org.junit.jupiter.api.Test;

public class SwitchTest {

    /**
     * Instance of algorithm reader
     */
    private final AlgorithmReader reader = new AlgorithmReader();

    @SuppressWarnings("checkstyle:LineLength")
    @Test
    public void testIncorrectSwitch() throws Exception {
        String test = "$r=rint(100);"
            + "$rf = int($r-3*int($r/3));"
            + "$rf1 = $rf+1-3*int(($rf+1)/3);"
            + "$rf2 = $rf+2-3*int(($rf+2)/3);"
            + "$quest1 = \"the <i>y</i>-axis\";"
            + "$quest2 = \"the origin\";"
            + "$quest3 = \"the <i>x</i>-axis\";"
            + "$quest  = switch($rf, \"$quest1\",\"$quest2\",\"$quest3\");"
            + "$x_fsgn= switch($rf,\"-\",\"-\",\"\");"
            + "$y_fsgn= switch($rf,\"\",\"-\",\"-\");"
            + "$A = int(6+rint(15));"
            + "$B = 0.5*(1+rint(4));"
            + "$c = rint(3);"
            + "$c1 = $c+1-3*int(($c+1)/3);"
            + "$c2 = $c+2-3*int(($c+2)/3);"
            + "$fun1  =  \"$A*x^3*e^(-1.5*x)\";"
            + "$fun1x = \"-$A*x^3*e^(-1.5*x)\";"
            + "$fun1y = \"$A*(-x)^3*e^(1.5*x)\";"
            + "$fun10 = \"-$A*(-x)^3*e^(1.5*x)\";"
            + "$fun2  = \"$B*x\";"
            + "$fun2x = \"-$B*x\";"
            + "$fun2y = \"-$B*x\";"
            + "$fun20 = \"$B*x\";"
            + "$fun3  = \"x^(0.5)\";"
            + "$fun3x = \"-x^(0.5)\";"
            + "$fun3y = \"(-x)^(0.5)\";"
            + "$fun30 = \"-(-x)^(0.5)\";"
            + "$fun  = switch($c, \"$fun1\",\"$fun2\",\"$fun3\");"
            + "$f1   = switch($c1, \"$fun10\",\"$fun20\",\"$fun30\");"
            + "$f2   = switch($c2, \"$fun10\",\"$fun20\",\"$fun30\");"
            + "$sym  = switch($c, switch($rf,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf,\"$fun3y\",\"$fun30\",\"$fun3x\"));"
            + "$sym1  = switch($c, switch($rf1,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf1,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf1,\"$fun3y\",\"$fun30\",\"$fun3x\"));"
            + "$sym2  = switch($c, switch($rf2,\"$fun1y\",\"$fun10\",\"$fun1x\"),switch($rf2,\"$fun2y\",\"$fun20\",\"$fun2x\"),switch($rf2,\"$fun3y\",\"$fun30\",\"$fun3x\"));"
            + "$interval  = switch($rf, \"-10,0\",\"-10,0\",\"0,10\");"
            + "$interval1  = switch($rf1, \"-10,0\",\"-10,0\",\"0,10\");"
            + "$interval2  = switch($rf2, \"-10,0\",\"-10,0\",\"0,10\");";

        Algorithm algorithm = reader.createAlgorithm(test);
        algorithm.calculateParameters();
        algorithm.printParameters(System.out);

        reader.setParameterFactory(new AdvancedParameterFactory());
        Algorithm algorithm2 = reader.createAlgorithm(test);
        algorithm2.printParameters(System.out);
        algorithm2.calculateParameters();
    }
}
