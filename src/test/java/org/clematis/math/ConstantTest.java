package org.clematis.math;

import org.clematis.math.algorithm.Algorithm;
import org.clematis.math.algorithm.Parameter;
import org.clematis.math.io.OutputFormatSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:MultipleStringLiterals"})
public class ConstantTest {


    @Test
    public void testConstant() throws AlgorithmException {

        Constant c = new Constant(-3.14154879879E45, 8);
        c.setMultiplier(1);
        c.setSdEnable(false);

        Assertions.assertEquals("-3.14154879879E45", c.getValue());
        Assertions.assertNotEquals("-3.14154879879E+45", c.getValue());

        c.setSdEnable(true);

        Assertions.assertEquals("-3.1415488E45", c.getValue());
        Assertions.assertNotEquals("-3.1415488E+45", c.getValue());

        OutputFormatSettings out = new OutputFormatSettings();
        Parameter p = new Parameter("$d", c);
        p.setCorrectAnswer(true);
        p.setSdNumber(c.getSdNumber());

        Algorithm a = new Algorithm();
        a.addParameter(p);
        a.setFormatSettings(out);

        out.setSdEnabled(false);
        a.setFormatSettings(out);

        Assertions.assertEquals("-3.14154879879E45", p.getOutputValue(true));
        Assertions.assertEquals("-3.14154879879E+45", p.getOutputValue(true));

        out.setSdEnabled(true);
        a.setFormatSettings(out);

        Assertions.assertEquals("-3.1415488E45", p.getOutputValue(true));
        Assertions.assertEquals("-3.1415488E+45", p.getOutputValue(true));

        p.setSdApplicable(false);

        Assertions.assertEquals("-3.14154879879E45", p.getOutputValue(true));
        Assertions.assertEquals("-3.14154879879E+45", p.getOutputValue(true));

        p.setSdApplicable(true);

        Assertions.assertEquals("-3.1415488E45", p.getOutputValue(true));
        Assertions.assertEquals("-3.1415488E+45", p.getOutputValue(true));

    }
}
