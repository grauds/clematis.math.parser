// Created: 05.12.2006 T 15:59:31
package org.clematis.math.v2;

import java.util.HashMap;
import java.util.Iterator;

import org.clematis.math.v2.algorithm.Algorithm;
import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.Key;
import org.clematis.math.v2.algorithm.Parameter;

/**
 * Prints the distribution of randomly generated parameters in particular algorithm
 */
public class RandomDistributionTester {
    /**
     * Iteration counter
     */
    private final int cnt = 30;
    /**
     * Instance of algorithm reader
     */
    private final AlgorithmReader reader = null;

    /**
     * Prints the distribution of randomly generated parameters in particular algorithm
     *
     * @param alg       body
     * @param paramName name of desired parameter
     * @param params    initial parameters
     * @throws Exception in case of error
     */
    public void graphAlgorithm(String alg, String paramName, HashMap<Key, iValue> params) throws Exception {
        Algorithm algorithm = reader.createAlgorithm(alg);
        HashMap<String, Integer> counters = new HashMap<String, Integer>();
        for (int i = 0; i < cnt; i++) {
            try {
                algorithm.calculateParameters(params);
                Parameter p = algorithm.getParameter(paramName);
                /** new and old abstract constants */
                if (p != null) {
                    AbstractConstant c = p.getCurrentResult();
                    String v = c.getValue();
                    if (counters.containsKey(v)) {
                        int counter = counters.get(v);
                        counter++;
                        counters.put(v, counter);
                    } else {
                        counters.put(v, 1);
                    }
                }
            } catch (AlgorithmException ex) {
                ex.printStackTrace();
            }
        }

        Iterator<String> it = counters.keySet().iterator();
        System.out.println("-------------------------------------");
        while (it.hasNext()) {
            String key = it.next();
            System.out.print(key + ": ");
            for (int k = 0; k < counters.get(key); k++) {
                System.out.print("x");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("--------------------------------------");
    }
}
