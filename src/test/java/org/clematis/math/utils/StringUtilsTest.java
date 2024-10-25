package org.clematis.math.utils;

import java.util.List;
import java.util.regex.Pattern;

import org.clematis.math.algorithm.Parameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings("checkstyle:MagicNumber")
public class StringUtilsTest {

    @Test
    public void testTokenizer() {
        String str = "if(eq($global_a,1),\"\",\"$global_a\")";
        List<String> groups = StringUtils.tokenizeReg(
            Pattern.compile(Parameter.FIND_EXPRESSION),
            str
        );
        Assertions.assertEquals(5, groups.size());
    }

    @Test
    public void testOneChar() {
        List<String> arr = StringUtils.tokenizeReg("A",
            "[:digit:]+|[:alpha:]+[:alnum:]*", false);
        Assertions.assertEquals(1, arr.size());
    }
}
