// Created: 12.04.2005 T 16:46:49
package org.clematis.math.reference.algorithm;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.functions.aFunction;

/**
 * Provides generic functions for qu algorithms, declared earlier in
 * algorithm being calculated.
 */
public interface iFunctionProvider
{
    /**
     * Provides functions for qu algorithms.
     *
     * @param name the function name
     * @return function or null
     */
    aFunction getFunction(String name) throws AlgorithmException;
}
