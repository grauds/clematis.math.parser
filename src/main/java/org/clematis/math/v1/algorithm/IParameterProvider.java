// $Id: IParameterProvider.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Jan 21, 2003 T 11:34:43 AM
package org.clematis.math.v1.algorithm;

import java.util.HashMap;
import java.util.Iterator;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.IValue;
import org.clematis.math.v1.io.IParameterFormatter;

/**
 * Provider of parameters for functions.
 */
public interface IParameterProvider extends ISimpleParameterProvider, IParameterFormatter, Iterable<String> {

    /**
     * Returns parameter names
     *
     * @return parameter names
     */
    Iterator<String> iterator();

    /**
     * Returns ident
     *
     * @return string ident
     */
    String getIdent();

    /**
     * Returns version of parameter provider
     * (for persistent storage versioning purposes)
     *
     * @return version of parameter provider
     */
    String getVersion();

    /**
     * Sets version of parameter provider
     * (for persistent storage versioning purposes)
     *
     * @param version of parameter provider
     */
    void setVersion(String version);

    /**
     * Calculates values of all parameters participating in algorithm.
     */
    void calculateParameters() throws AlgorithmException;

    /**
     * Calculates values of all parameters participating in algorithm with a map
     * of parameters with values, so the parameters from the map are substituted to the
     * algorithm with values from the map.
     */
    void calculateParameters(HashMap<Key, IValue> params) throws AlgorithmException;

    /**
     * Returns parameter, found by custom identifier.
     *
     * @param ident under which some parameters may store
     * @return parameter, found by custom identifier
     */
    Parameter getParameterByCustomIdent(String ident);
}
