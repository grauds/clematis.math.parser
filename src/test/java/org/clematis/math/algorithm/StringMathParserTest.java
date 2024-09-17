package org.clematis.math.algorithm;

import org.clematis.math.AlgorithmReader;
import org.clematis.math.parsers.v2.AdvancedParameterFactory;
import org.clematis.math.test.cases.AlgorithmTestCases;
import org.clematis.math.test.cases.BooleanTestCases;
import org.clematis.math.test.cases.DecimalTestCases;
import org.clematis.math.test.cases.GenericFunctionsTestCases;
import org.clematis.math.test.cases.ImplicitMultiplicationTestCases;
import org.clematis.math.test.cases.ParameterAssignTestCases;
import org.clematis.math.test.cases.PlainTestCases;
import org.clematis.math.test.cases.SigDigitsTestCases;
import org.clematis.math.test.cases.StringsTestCases;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringMathParserTest {

    /**
     * Instance of algorithm reader
     */
    private final AlgorithmReader reader = new AlgorithmReader();

    /**
     * Tests groups of algorithm test cases
     *
     * @param algorithmTestCases groups of algorithm test cases
     */
    public void testAlgorithmCases(AlgorithmTestCases algorithmTestCases) {
        for (String sAlg : algorithmTestCases.getTests()) {
            Assertions.assertDoesNotThrow(() -> readAndParseAlgorithm(sAlg));
        }
    }

    private void readAndParseAlgorithm(String algorithmString) throws Exception {
        reader.setParameterFactory(new AdvancedParameterFactory());
        Algorithm algorithm = reader.createAlgorithm(algorithmString);
        algorithm.calculateParameters();
    }

    @Test
    public void testBooleanTestCases() {
        testAlgorithmCases(new BooleanTestCases());
    }

    @Test
    public void testDecimalTestCases() {
        testAlgorithmCases(new DecimalTestCases());
    }

    @Test
    public void testGenericFunctionTestCases() {
        testAlgorithmCases(new GenericFunctionsTestCases());
    }

    @Test
    public void testImplicitMultiplicationTestCases() {
        testAlgorithmCases(new ImplicitMultiplicationTestCases());
    }

    @Test
    public void testParameterAssignTestCases() {
        testAlgorithmCases(new ParameterAssignTestCases());
    }

    @Test
    public void testPlainTestCases() {
        testAlgorithmCases(new PlainTestCases());
    }

    @Test
    public void testSigDigitsTestCases() {
        testAlgorithmCases(new SigDigitsTestCases());
    }

    @Test
    public void testStringsTestCases() {
        testAlgorithmCases(new StringsTestCases());
    }
}
