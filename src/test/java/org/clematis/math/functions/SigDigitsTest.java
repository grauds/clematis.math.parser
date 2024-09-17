package org.clematis.math.functions;

import static org.clematis.math.functions.Sig.getSigDigits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class SigDigitsTest {


    @Test
    public void testSig() {

        Assertions.assertEquals("0", Sig.getSigDigits("0", 3));
        Assertions.assertEquals(0.01, Lsu.getLSU("0", 3));

        String[][] sigDigits = {
            {"40000000", "0.8", "400000000" },
            {"39000000", "0.81", "390000000" },
            {"38800000", "0.809", "388000000" },
            {"38760000", "0.8094", "387600000" },
            {"38763000", "0.80943", "387630000" },
            {"38763500", "0.809430", "387630000" },
            {"38763480", "0.8094300", "387630000" },
            {"38763476", "0.80943000", "387630000" },
            {"38763475.8", "0.809430000", "387630000" },
            {"38763475.81", "0.8094300000", "387630000.0" },
            {"38763475.809", "0.80943000000", "387630000.00" },
            {"38763475.8094", "0.809430000000", "387630000.000" },
            {"38763475.80943", "0.8094300000000", "387630000.0000" },
            {"38763475.809430", "0.80943000000000", "387630000.00000" },
            {"38763475.8094300", "0.809430000000000", "387630000.000000" },
            {"38763475.80943000", "0.8094300000000000", "387630000.0000000" },
            {"38763475.809430000", "0.80943000000000000", "387630000.00000000" }
        };

        for (int i = 1; i < 18; i++) {
            Assertions.assertEquals(sigDigits[i - 1][0], Sig.getSigDigits("38763475.809430000", i));
            Assertions.assertEquals(sigDigits[i - 1][1], Sig.getSigDigits("0.809430000", i));
            Assertions.assertEquals(sigDigits[i - 1][2], Sig.getSigDigits("387630000", i));
        }
        Assertions.assertEquals("10.0", Sig.getSigDigits("10.0001", 3));
        Assertions.assertEquals("1.0",
            Sig.getSigDigits(Sig.getSigDigits("0.9999999999", 2), 2));
        Assertions.assertEquals("9000000",
            Sig.getSigDigits(Sig.getSigDigits("9000000", 2), 2));
        Assertions.assertEquals("10",
            Sig.getSigDigits(Sig.getSigDigits("9.9999999999", 2), 2));
        Assertions.assertEquals("10",
            Sig.getSigDigits(Sig.getSigDigits("9.99000", 2), 2));
    }

    @Test
    public void testNegativeNumbers() {
        Assertions.assertEquals("-1981", Sig.getSigDigits("-1981", 4));
        Assertions.assertEquals("-1981", getSigDigits("-1981", 4));
    }
}
