// Created: 12.04.2005 T 16:46:49
package org.clematis.math.v1.algorithm;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.functions.aFunction;

/**
 * Provides generic functions for qu algorithms, declared earlier in
 * algorithm being calculated.
 */
public interface IFunctionProvider {
    /**
     * Provides functions for qu algorithms.
     *
     * @param name the function name
     * @return function or null
     */
    aFunction getFunction(String name) throws AlgorithmException;

    /**
     * This method returns true if function provider has function with given signature
     *
     * @param name of the function
     * @return true if function provider has function with given signature
     */
    boolean hasFunction(String name);
}
