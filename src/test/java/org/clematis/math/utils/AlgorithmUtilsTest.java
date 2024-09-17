package org.clematis.math.utils;

import org.clematis.math.AlgorithmException;
import org.clematis.math.StringConstant;
import org.clematis.math.algorithm.DefaultParameterProvider;
import org.clematis.math.algorithm.Parameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AlgorithmUtilsTest {

    @Test
    public void testReplaceParameters() throws AlgorithmException {
        String str = "if(eq($global_a,1),\"\",\"$global_a\")";

        DefaultParameterProvider defaultParameterProvider = new DefaultParameterProvider();
        defaultParameterProvider.addParameter(new Parameter("$global_a", new StringConstant("1")));

        Assertions.assertEquals("if(eq(1,1),\"\",\"1\")",
            AlgorithmUtils.replaceParameters(str, defaultParameterProvider, 0, false)
        );

        Assertions.assertEquals("if(eq(1,1),\"\",\"$global_a\")",
            AlgorithmUtils.replaceParameters(str, defaultParameterProvider, 0, true)
        );
    }
}
