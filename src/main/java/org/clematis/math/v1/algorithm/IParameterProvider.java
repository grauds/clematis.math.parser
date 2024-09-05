// $Id: IParameterProvider.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Jan 21, 2003 T 11:34:43 AM
package org.clematis.math.v1.algorithm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.IValue;
import org.clematis.math.v1.io.IParameterFormatter;
import org.jdom2.Element;

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
     * Calculates values of all parameters participating in algorithm.
     */
    void calculateParameters() throws AlgorithmException;

    /**
     * Calculates values of all parameters participating in algorithm with a map
     * of parameters with values, so the parameters from the map are substituted to the
     * algorithm with values from the map.
     */
    void calculateParameters(Map<Key, IValue> params) throws AlgorithmException;

    /**
     * Returns parameter, found by custom identifier.
     *
     * @param ident under which some parameters may store
     * @return parameter, found by custom identifier
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
    IParameterProvider getParent();

    /**
     * Set parent parameter provider
     *
     * @param parent parameter provider
     */
    void setParent(IParameterProvider parent);

    /**
     * Add child parameter provider
     *
     * @param key       under which child parameter provider is to be stored
     * @param algorithm child parameter provider
     */
    void addAlgorithm(String key, IParameterProvider algorithm);

    /**
     * Returns a list of children
     *
     * @return a list of children
     */
    List<IParameterProvider> getChildren();

    /**
     * Return child parameter provider by key
     *
     * @param key to search child parameter provider
     * @return child parameter provider
     */
    IParameterProvider getAlgorithm(String key);

    /**
     * Return child parameter provider by key
     *
     * @param key to search child parameter provider
     * @return child parameter provider
     */
    IParameterProvider findAlgorithm(String key);

    /**
     * Remove child parameter provider
     *
     * @param key to search child parameter provider
     */
    void removeAlgorithm(String key);

    /**
     * Returns the array of parameters
     * @return the array of parameters
     */
    Parameter[] getParameters();

    /**
     * Revalidate parameters with XML element containing parameters
     * @param element containing parameters
     */
    void revalidateParameters(Element element) throws AlgorithmException;

    /**
     * Save parameters to XML
     *
     * @return JDOM element with parameters
     */
    Element save();
    /**
     * Converts algorithm to JDOM.
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    Element toXML();
}
