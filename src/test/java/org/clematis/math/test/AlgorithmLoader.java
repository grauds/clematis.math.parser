// Created: 05.12.2006 T 16:02:35
package org.clematis.math.test;

import org.clematis.math.algorithm.Algorithm;
import org.clematis.math.algorithm.AlgorithmFactory;
import org.clematis.math.io.OutputFormatSettings;

/**
 * Load reference algorithms
 */
public class AlgorithmLoader {
    /**
     * Reference
     */
    private Algorithm algorithm = null;

    public Algorithm getAlgorithmReference(String sAlg, String initialConditions)
        throws Exception {
        if (algorithm == null) {
            algorithm = (Algorithm) AlgorithmFactory.loadAlgorithm(sAlg, initialConditions);
        }
        return algorithm;
    }

    public OutputFormatSettings getOutputFormatSettings() {
        return new OutputFormatSettings();
    }
}
