package org.clematis.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MathUtilsTest {

    @Test
    public void testGroupingOfThousands() {
        Assertions.assertEquals("1,234,543,563.563", MathUtils.groupThousands("1234543563.563"));
        Assertions.assertEquals("1,234,543,563", MathUtils.groupThousands("1234543563"));
        Assertions.assertEquals("-1,234,543,563", MathUtils.groupThousands("-1234543563"));
        Assertions.assertEquals("-0.34234", MathUtils.groupThousands("-0.34234"));
    }

    @Test
    public void testUngroupingOfThousands() {
        Assertions.assertEquals("1234543563.563", MathUtils.ungroupThousands("1,234,543,563.563"));
        Assertions.assertEquals("1234543563", MathUtils.ungroupThousands("1,23,4,5,43,563"));
        Assertions.assertEquals("-1234543563", MathUtils.ungroupThousands("-1,234,543,563"));
        Assertions.assertEquals("-0.34234", MathUtils.ungroupThousands("-0.34234"));
    }

    @Test
    public void testJavaStringNumberFormat() {
        Assertions.assertTrue(MathUtils.checkJavaFormatString("123"));
        Assertions.assertTrue(MathUtils.checkJavaFormatString("123.45e-2"));
        Assertions.assertTrue(MathUtils.checkJavaFormatString("-123.45e-2"));
        Assertions.assertTrue(MathUtils.checkJavaFormatString("-123.45e2"));
        Assertions.assertTrue(MathUtils.checkJavaFormatString("-123234.45e-299"));
        Assertions.assertFalse(MathUtils.checkJavaFormatString("-134523234.45(299)"));
    }

    @Test
    public void testCorrectAndValidateInput() {
        Assertions.assertEquals(".123", MathUtils.correctAndValidateInput("00000.123"));
        Assertions.assertEquals(".124", MathUtils.correctAndValidateInput("+0.124"));
        Assertions.assertEquals(".124", MathUtils.correctAndValidateInput("0.124"));
        Assertions.assertEquals(".125", MathUtils.correctAndValidateInput("00000.125000"));
        Assertions.assertEquals(".128", MathUtils.correctAndValidateInput("0.128000"));
        Assertions.assertEquals("100000.126E-1",
            MathUtils.correctAndValidateInput("100000.126E-1")
        );
        Assertions.assertEquals(".00000127", MathUtils.correctAndValidateInput("00000127"));
    }

    @Test
    public void testCutTrailingZeros() {
        Assertions.assertEquals("983.345", MathUtils.cutTrailingZeros("983.3450000"));
        Assertions.assertEquals("9833450000", MathUtils.cutTrailingZeros("9833450000"));
        Assertions.assertEquals("-98334.5", MathUtils.cutTrailingZeros("-98334.50000"));
    }
}
