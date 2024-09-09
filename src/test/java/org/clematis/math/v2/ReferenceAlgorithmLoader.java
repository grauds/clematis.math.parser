// Created: 05.12.2006 T 16:02:35
package org.clematis.math.v2;

import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.v1.algorithm.Algorithm;
import org.clematis.math.v1.algorithm.AlgorithmFactory;

/**
 * Load reference algorithms
 */
public class ReferenceAlgorithmLoader {
    /**
     * Reference
     */
    private Algorithm algorithm = null;

    private AlgorithmFactory loadReference() {
        try {
            Class<AlgorithmFactory> clazz =
                (Class<AlgorithmFactory>)
                    Class.forName("org.clematis.math.v2.algorithm.AlgorithmFactory");
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Algorithm getAlgorithmReference(String sAlg, String initialConditions)
        throws Exception {
        if (algorithm == null) {
            algorithm = (Algorithm)
                AlgorithmFactory.loadAlgorithm(sAlg, initialConditions);
        }
        return algorithm;
    }

    public OutputFormatSettings getOutputFormatSettings() {
        return new OutputFormatSettings();
    }
}
