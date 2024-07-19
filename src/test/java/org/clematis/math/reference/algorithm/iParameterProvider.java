// $Id: iParameterProvider.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Jan 21, 2003 T 11:34:43 AM
package org.clematis.math.reference.algorithm;

import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.iValue;

import java.util.HashMap;
import java.util.Iterator;
/**
 * Provider of parameters for functions.
 */
public interface iParameterProvider extends iSimpleParameterProvider,
                                            iParameterFormatter,
                                            Iterable<String>
{
    /**
     * Returns parameter names
     * @return parameter names
     */
    Iterator<String> iterator();

    /**
     * Returns ident
     * @return string ident
     */
    String getIdent();

    /**
     * Returns version of parameter provider
     * (for persistent storage versioning purposes)
     * @return version of parameter provider
     */
    String getVersion();

    /**
     * Sets version of parameter provider
     * (for persistent storage versioning purposes)
     * @param version of parameter provider
     */
    void setVersion(String version);

    /**
     * Calculates values of all parameters participating in algorithm.
     */
    void calculateParameters() throws AlgorithmException;

    /**
     * Calculates values of all parameters participating in algorithm.
     */
    void calculateParameters(HashMap<Key, iValue> params) throws AlgorithmException;

    /**
     * Returns parameter, found by custom ident.
     * @param ident under which some parameters may store
     * @return parameter, found by custom ident
     */
    Parameter getParameterByCustomIdent(String ident);
}
