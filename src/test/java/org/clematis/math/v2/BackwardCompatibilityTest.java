// Created: 07.07.2005 T 10:00:19
package org.clematis.math.v2;

import java.util.ArrayList;

import org.clematis.math.v2.algorithm.Algorithm;
import org.clematis.math.v2.algorithm.AlgorithmFactory;
import org.clematis.math.v2.algorithm.Parameter;
import org.clematis.math.v2.cases.AlgorithmTestCases;
import org.clematis.math.v2.cases.BooleanTestCases;
import org.clematis.math.v2.cases.DecimalTestCases;
import org.clematis.math.v2.cases.GenericFunctionsTestCases;
import org.clematis.math.v2.cases.ImplicitMultiplicationTestCases;
import org.clematis.math.v2.cases.ParameterAssignTestCases;
import org.clematis.math.v2.cases.PlainTestCases;
import org.clematis.math.v2.cases.SigDigitsTestCases;
import org.clematis.math.v2.cases.StringsTestCases;
import org.clematis.math.v2.io.OutputFormatSettings;
import org.junit.jupiter.api.Test;

/**
 * Backward compatibility tests, compares output from adopted to work separately and current Mathematics Parser
 */
public class BackwardCompatibilityTest {
    /**
     * Instance of current algorithm reader
     */
    private final AlgorithmReader reader = new AlgorithmReader();
    /**
     * Iteration counter
     */
    private final int cnt = 30;
    /**
     * Format output parameters toggle
     */
    private final boolean format = true;
    /**
     * Compare output parameters toggle
     */
    private final boolean compare = true;
    /**
     * Stop and throw exception if errorneous descrepance occur
     */
    private final boolean stopOnError = true;

    /**
     * Probe algorithm agains reference algorithm
     *
     * @param sAlg algorithm body
     * @throws Exception
     */
    void probeAlgorithm(String sAlg) throws Exception {
        probeAlgorithm(sAlg, true);
    }

    void probeAlgorithm(String sAlg, boolean calculate) throws Exception {
        System.out.println("*************** ALG ******************");
        System.out.println(sAlg);
        System.out.println("*************** ALG ******************");
        Algorithm algorithm = reader.createAlgorithm(sAlg);

        for (int i = 0; i < cnt; i++) {
            algorithm.calculateParameters();
            /** output parameters */
            algorithm.printParameters(System.out);
            if (calculate) {
                /** save algorithm and initial conditions */
                String alg = AlgorithmFactory.toXML(algorithm);
                String save = AlgorithmFactory.saveAlgorithm(algorithm);
                /** create reference algorithms */
                ReferenceAlgorithmLoader loader = new ReferenceAlgorithmLoader();
                org.clematis.math.v1.algorithm.Algorithm reference = loader.getAlgorithmReference(alg, save);
                /** go through tested algorithm parameters */
                ArrayList<Parameter> params = algorithm.getParameters();
                for (int k = 0; k < params.size(); k++) {
                    Parameter p = params.get(k);
                    if (p != null && reference.getParameter(p.getName()) != null) {
                        /** new and old abstract constants */
                        AbstractConstant ac_NEW = p.getCurrentResult();
                        if (reference.getParameter(p.getName()).getCurrentResult() != null) {
                            String NEW_STRING;
                            String OLD_STRING;

                            if (format) {
                                NEW_STRING = ac_NEW.getValue(new OutputFormatSettings());
                                OLD_STRING = loader.getAlgorithmReference(alg, save).getParameter(p.getName()).
                                    getCurrentResult().getValue(loader.getOutputFormatSettings());
                            } else {
                                NEW_STRING = ac_NEW.getValue();
                                OLD_STRING = loader.getAlgorithmReference(alg, save).getParameter(p.getName())
                                    .getCurrentResult().
                                    getValue(null);
                            }

                            System.out.println("CURRENT: " + p.getName() + " / " + p.getCode() + " / " + NEW_STRING);
                            System.out.println("REFERENCE: " + p.getName() + " / " + p.getCode() + " / " + OLD_STRING);

                            if (compare && !NEW_STRING.equals(OLD_STRING)) {
                                if (stopOnError) {
                                    throw new AlgorithmException(
                                        "Descrepance detected - new:" + NEW_STRING + " old:" + OLD_STRING, k + 1);
                                } else {
                                    System.out.println(
                                        "Descrepance detected - new:" + NEW_STRING + " old:" + OLD_STRING + " in " + k +
                                            1);
                                }
                            }
                        }
                    } else {
                        System.out.println("Parameter number " + i + " is null");
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
        String err = "$t1=rand(21,22,5);" +
            "$t2=sig(5,($t1+rand(2,7,5)));" +
            "$mass=rand(0.1,0.9,4);" +
            "$ans=sig(4,-1981.0000);" +
            "$tol=lsu(4,$ans);" +
            "$hc=sig(4,(-$ans*(1E+3)*($mass/480.9)/($t2-$t1)));";
        probeAlgorithm(err);
    }
}