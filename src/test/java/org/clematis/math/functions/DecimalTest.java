package org.clematis.math.functions;

import org.clematis.math.v2.functions.Decimal;
import org.clematis.math.v2.functions.Sig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class DecimalTest {

    @Test
    public void testDecimal() {
        Assertions.assertEquals("38763475.80943000",
            Decimal.round("38763475.809430000", -8));
        Assertions.assertEquals("0.80943000", Decimal.round("0.809430000", -8));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -8));

        Assertions.assertEquals("38763475.8094300", Decimal.round("38763475.809430000", -7));
        Assertions.assertEquals("0.8094300", Decimal.round("0.809430000", -7));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -7));

        Assertions.assertEquals("38763475.809430", Decimal.round("38763475.809430000", -6));
        Assertions.assertEquals("0.809430", Decimal.round("0.809430000", -6));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -6));

        Assertions.assertEquals("38763475.80943", Decimal.round("38763475.809430000", -5));
        Assertions.assertEquals("0.80943", Decimal.round("0.809430000", -5));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -5));

        Assertions.assertEquals("38763475.8094", Decimal.round("38763475.809430000", -4));
        Assertions.assertEquals("0.8094", Decimal.round("0.809430000", -4));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -4));

        Assertions.assertEquals("38763475.809", Decimal.round("38763475.809430000", -3));
        Assertions.assertEquals("0.809", Decimal.round("0.809430000", -3));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -3));

        Assertions.assertEquals("38763475.81", Decimal.round("38763475.809430000", -2));
        Assertions.assertEquals("0.81", Decimal.round("0.809430000", -2));
        Assertions.assertEquals("387630000", Decimal.round("387630000", -2));

        Assertions.assertEquals("38763476", Decimal.round("38763475.809430000", 0));
        Assertions.assertEquals("1", Decimal.round("0.809430000", 0));
        Assertions.assertEquals("387630000", Decimal.round("387630000", 0));

        Assertions.assertEquals("38763480", Decimal.round("38763475.809430000", 1));
        Assertions.assertEquals("0.809430000", Decimal.round("0.809430000", 1));
        Assertions.assertEquals("387630000", Decimal.round("387630000", 1));

        Assertions.assertEquals("38763500", Decimal.round("38763475.809430000", 2));
        Assertions.assertEquals("0.809430000", Decimal.round("0.809430000", 2));
        Assertions.assertEquals("387630000", Decimal.round("387630000", 2));

        Assertions.assertEquals("0", Decimal.round("2.00E-9", -5));
        Assertions.assertEquals("23000", Sig.getSigDigits("22994.11", 2));
    }


}
