// Created: 02.06.2006 T 11:46:09
package org.clematis.math.io;

import java.io.IOException;
import java.io.Serializable;

import org.clematis.math.AlgorithmFactory;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.jdom2.JDOMException;

/**
 * This class hold and synchronizes XML representation of algorithm and its results with
 * the algorithm object itself.
 */
public class XParameterProvider implements Serializable {
    /**
     * Algorithm XML
     */
    private String xml = "";
    /**
     * Algorithm results
     */
    private String resultsXml = "";
    /**
     * Algorithm
     */
    private iParameterProvider parameterProvider = null;
    /**
     * Property parameterProvider xml is changed
     */
    public final static int XML_PROPERTY = 0;
    /**
     * Property parameterProvider results xml is changed
     */
    public final static int XML_RESULTS_PROPERTY = 1;

    /**
     * Gets calculated parameterProvider
     *
     * @return calculated parameterProvider
     */
    public iParameterProvider getParameterProvider() throws AlgorithmException, IOException, JDOMException {
        if (parameterProvider == null) {
            if (hasResults()) {
                parameterProvider = AlgorithmFactory.loadAlgorithm(xml, resultsXml);
            } else {
                parameterProvider = AlgorithmFactory.createFromQuestionAlgorithm(xml);
            }
        }
        return parameterProvider;
    }

    /**
     * Sets parameter provider object
     *
     * @param parameterProvider
     */
    public void setParameterProvider(iParameterProvider parameterProvider) throws AlgorithmException,
        IOException {
        this.parameterProvider = AlgorithmFactory.copyAlgorithm(parameterProvider);
        this.xml = AlgorithmFactory.toXML(this.parameterProvider);
        this.resultsXml = AlgorithmFactory.saveAlgorithm(this.parameterProvider);
    }

    /**
     * Gets algorihm XML.
     *
     * @return algorihm XML.
     */
    public String getXml() {
        return xml;
    }

    /**
     * Sets parameterProvider in XML format.
     *
     * @param algorithmXML parameterProvider in XML format.
     */
    public void setXml(String algorithmXML) throws IOException, JDOMException, AlgorithmException {
        this.xml = algorithmXML;
        fireInternalPropertyChange(XParameterProvider.XML_PROPERTY);
    }

    /**
     * Sets initial values for parameterProvider
     *
     * @param algorithmResultsXML - initial values for parameterProvider
     */
    public void setResultsXml(String algorithmResultsXML) throws IOException, JDOMException, AlgorithmException {
        this.resultsXml = algorithmResultsXML;
        fireInternalPropertyChange(XParameterProvider.XML_RESULTS_PROPERTY);
    }

    /**
     * Returns initial values for parameterProvider
     *
     * @return initial values for parameterProvider
     */
    public String getResultsXml() {
        return resultsXml;
    }

    /**
     * Calculates parameters and changes algorithm results xml property
     */
    public void calculateParameters() throws AlgorithmException, IOException, JDOMException {
        if (getParameterProvider() != null) {
            getParameterProvider().calculateParameters();
            this.resultsXml = AlgorithmFactory.saveAlgorithm(this.parameterProvider);
        }
    }

    /**
     * This method gets called when a bound property is changed.
     */
    private void fireInternalPropertyChange(int property)
        throws IOException, JDOMException, AlgorithmException {
        switch (property) {
            case XParameterProvider.XML_PROPERTY: {
                parameterProvider = null;
                break;
            }
            case XParameterProvider.XML_RESULTS_PROPERTY: {
                AlgorithmFactory.loadAlgorithm(parameterProvider, resultsXml);
                break;
            }
        }
    }

    /**
     * Returns true, if this parameter provider has results
     *
     * @return true, if this parameter provider has results
     */
    public boolean hasResults() {
        return getResultsXml() != null && !getResultsXml().trim().equals("");
    }
}
