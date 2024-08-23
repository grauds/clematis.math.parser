// Created: Jan 21, 2003 T 11:34:43 AM
package org.clematis.math.v2.algorithm;

import org.clematis.math.v2.iValue;
import org.clematis.math.v2.io.iParameterFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Provider of parameters for functions.
 */
public interface iParameterProvider extends iSimpleParameterProvider, iParameterFormatter, Iterable<String> {
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
     * Sets parameter provider ident
     *
     * @param ident parameter provider ident
     */
    void setIdent(String ident);

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
     * Return timestamp of parameter provider
     *
     * @return timestamp of parameter provider
     */
    long getTimestamp();

    /**
     * Sets timestamp to parameter provider
     *
     * @param timestamp to parameter provider
     */
    void setTimestamp(Long timestamp);

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
     *
     * @param ident under which some parameters may store
     * @return parameter, found by custom ident
     */
    Parameter getParameterByCustomIdent(String ident);

    /**
     * Checks whether this provider has specified parameter.
     *
     * @param paramName the name of parameter.
     * @return <code>true</code> if this provider has parameter or <code>false</code> in otherwise.
     */
    boolean hasParameter(String paramName);

    /**
     * Returns parent parameter provider
     *
     * @return parent parameter provider
     */
    iParameterProvider getParent();

    /**
     * Set parent parameter provider
     *
     * @param parent parameter provider
     */
    void setParent(iParameterProvider parent);

    /**
     * Add child parameter provider
     *
     * @param key       under which child parameter provider is to be stored
     * @param algorithm child parameter provider
     */
    void addAlgorithm(String key, iParameterProvider algorithm);

    /**
     * Returns a list of children
     *
     * @return a list of children
     */
    ArrayList<iParameterProvider> getChildren();

    /**
     * Return child parameter provider by key
     *
     * @param key to search child parameter provider
     * @return child parameter provider
     */
    iParameterProvider getAlgorithm(String key);

    /**
     * Return child parameter provider by key
     *
     * @param key to search child parameter provider
     * @return child parameter provider
     */
    iParameterProvider findAlgorithm(String key);

    /**
     * Remove child parameter provider
     *
     * @param key to search child parameter provider
     */
    void removeAlgorithm(String key);
}
