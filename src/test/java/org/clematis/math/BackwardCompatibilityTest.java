// Created: 07.07.2005 T 10:00:19
package org.clematis.math;

import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.test.AlgorithmLoader;
import org.clematis.math.test.cases.AlgorithmTestCases;
import org.clematis.math.test.cases.BooleanTestCases;
import org.clematis.math.test.cases.DecimalTestCases;
import org.clematis.math.test.cases.GenericFunctionsTestCases;
import org.clematis.math.test.cases.ImplicitMultiplicationTestCases;
import org.clematis.math.test.cases.ParameterAssignTestCases;
import org.clematis.math.test.cases.PlainTestCases;
import org.clematis.math.test.cases.SigDigitsTestCases;
import org.clematis.math.test.cases.StringsTestCases;
import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmReader;
import org.clematis.math.v1.algorithm.Algorithm;
import org.clematis.math.v1.algorithm.AlgorithmFactory;
import org.clematis.math.v1.algorithm.Parameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Backward compatibility tests, compares output from adopted to work separately and current Mathematics Parser
 */
@Disabled
public class BackwardCompatibilityTest {

    /**
     * Iteration counter
     */
    private static final int CNT = 30;

    /**
     * Instance of naive algorithm reader
     */
    private final AlgorithmReader reader = new AlgorithmReader();


    /**
     * Probe algorithm against reference algorithm
     *
     * @param sAlg algorithm body
     * @throws Exception in case algorithm can't be processed
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    void probeAlgorithm(String sAlg) throws Exception {

        Algorithm algorithm = reader.createAlgorithm(sAlg);

        for (int i = 0; i < CNT; i++) {

            algorithm.calculateParameters();

            /* save algorithm and initial conditions */
            String alg = AlgorithmFactory.toXML(algorithm);
            String save = AlgorithmFactory.saveAlgorithm(algorithm);

            /* create reference algorithms */
            AlgorithmLoader loader = new AlgorithmLoader();
            org.clematis.math.v1.algorithm.Algorithm reference = loader.getAlgorithmReference(alg, save);

            /* go through tested algorithm parameters */
            Parameter[] params = algorithm.getParameters();
            for (Parameter p : params) {
                if (p != null && reference.getParameter(p.getName()) != null) {
                    /* new and old abstract constants */
                    AbstractConstant currentResult = p.getCurrentResult();
                    if (reference.getParameter(p.getName()).getCurrentResult() != null) {
                        String newString;
                        String oldString;

                        newString = currentResult.getValue(new OutputFormatSettings());
                        oldString = loader.getAlgorithmReference(alg, save).getParameter(p.getName()).
                            getCurrentResult().getValue(loader.getOutputFormatSettings());

                        Assertions.assertEquals(newString, oldString);

                        newString = currentResult.getValue();
                        oldString = loader.getAlgorithmReference(alg, save).getParameter(p.getName())
                            .getCurrentResult().
                            getValue(null);

                        Assertions.assertEquals(newString, oldString);
                    }
                }
            }
        }

    }

    /**
     * Tests groups of algorithm test cases
     *
     * @param algorithmTestCases groups of algorithm test cases
     */
    public void testAlgorithmCases(AlgorithmTestCases algorithmTestCases) throws Exception {
        for (String sAlg : algorithmTestCases.getTests()) {
            probeAlgorithm(sAlg);
        }
    }

    @Test
    public void testBooleanTestCases() throws Exception {
        testAlgorithmCases(new BooleanTestCases());
    }

    @Test
    public void testDecimalTestCases() throws Exception {
        testAlgorithmCases(new DecimalTestCases());
    }

    @Test
    public void testGenericFunctionTestCases() throws Exception {
        testAlgorithmCases(new GenericFunctionsTestCases());
    }

    @Test
    public void testImplicitMultiplicationTestCases() throws Exception {
        testAlgorithmCases(new ImplicitMultiplicationTestCases());
    }

    @Test
    public void testParameterAssignTestCases() throws Exception {
        testAlgorithmCases(new ParameterAssignTestCases());
    }

    @Test
    public void testPlainTestCases() throws Exception {
        testAlgorithmCases(new PlainTestCases());
    }

    @Test
    public void testSigDigitsTestCases() throws Exception {
        testAlgorithmCases(new SigDigitsTestCases());
    }

    @Test
    public void testStringsTestCases() throws Exception {
        testAlgorithmCases(new StringsTestCases());
    }

    @Test
    public void testSimpleFractionProductError() throws Exception {
        String err = "$t1=rand(21,22,5);"
            + "$t2=sig(5,($t1+rand(2,7,5)));"
            + "$mass=rand(0.1,0.9,4);"
            + "$ans=sig(4,-1981.0000);"
            + "$tol=lsu(4,$ans);"
            + "$hc=sig(4,(-$ans*(1E+3)*($mass/480.9)/($t2-$t1)));";
        probeAlgorithm(err);
    }
}