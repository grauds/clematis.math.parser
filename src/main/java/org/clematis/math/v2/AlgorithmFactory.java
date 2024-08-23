// Created: 14.04.2005 T 11:24:27
package org.clematis.math.v2;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.clematis.math.v2.algorithm.Algorithm;
import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.BshParameterProvider;
import org.clematis.math.v2.algorithm.Key;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Factory to create algorithms froms xml
 */
public class AlgorithmFactory {
    /**
     * The JDOM builder.
     */
    private static final SAXBuilder builder = new SAXBuilder();

    /** config builder */
    static {
        builder.setExpandEntities(true);
    }

    /**
     * Loads XML from a reader.
     *
     * @param in the reader.
     * @return loaded JDOM document.
     * @throws IOException             on I/O error.
     * @throws org.jdom2.JDOMException on JDOM error.
     */
    static Document load(Reader in) throws IOException, JDOMException {
        Reader reader = in;
        Document doc = builder.build(reader);
        reader.close();
        return doc;
    }

    /**
     * Create either bsh algorithm or qu algorithm from xml element
     * in question.
     *
     * @param algorithmXML
     */
    public static iParameterProvider createFromQuestionAlgorithm(String algorithmXML)
        throws AlgorithmException, IOException, JDOMException {
        Element root = null;

        if (algorithmXML != null && !algorithmXML.trim().equals("")) {
            Document doc = load(new StringReader(algorithmXML));
            root = doc.getRootElement();
            return createFromQuestionAlgorithm(root);
        }
        return null;
    }

    /**
     * Create either bsh algorithm or qu algorithm from xml element
     * in question.
     *
     * @param algorithmXML
     */
    public static iParameterProvider createFromQuestionAlgorithm(Element algorithmXML)
        throws AlgorithmException {
        if (algorithmXML != null) {
            if ("bsh".equals(algorithmXML.getAttributeValue("type"))) {
                return BshParameterProvider.createFromQuestionXML(algorithmXML);
            } else {
                return Algorithm.createFromQuestionXML(algorithmXML);
            }
        }
        return null;
    }

    /**
     * Create either bsh algorithm or qu algorithm from xml element
     * in question.
     *
     * @param algorithmXML
     */
    private static iParameterProvider createAlgorithm(Element algorithmXML) throws AlgorithmException {
        if (algorithmXML != null) {
            if ("bsh".equals(algorithmXML.getAttributeValue("type"))) {
                return BshParameterProvider.createFromXML(algorithmXML);
            } else {
                return Algorithm.createFromXML(algorithmXML);
            }
        }
        return null;
    }

    /**
     * Returns a copy of given algorithm
     *
     * @param algorithm
     * @return a copy of given algorithm
     */
    public static iParameterProvider copyAlgorithm(iParameterProvider algorithm, HashMap<Key, iValue> params)
        throws AlgorithmException {
        if (algorithm instanceof BshParameterProvider) {
            return BshParameterProvider.createFromBshParameterProvider(algorithm, params);
        } else if (algorithm instanceof Algorithm) {
            return Algorithm.createFromAlgorithm(algorithm, params);
        }
        return algorithm;
    }

    /**
     * Returns a copy of given algorithm
     *
     * @param algorithm
     * @return a copy of given algorithm
     */
    public static iParameterProvider copyAlgorithm(iParameterProvider algorithm) throws AlgorithmException {
        if (algorithm instanceof BshParameterProvider) {
            return BshParameterProvider.createFromBshParameterProvider(algorithm);
        } else if (algorithm instanceof Algorithm) {
            return Algorithm.createFromAlgorithm(algorithm);
        }
        return algorithm;
    }

    /**
     * Store algorithm in xml string
     *
     * @param algorithm to store
     * @return xml string
     */
    public static String toXML(iParameterProvider algorithm) throws IOException {
        if (algorithm instanceof BshParameterProvider) {
            return printToString(((BshParameterProvider) algorithm).toXML());
        } else if (algorithm instanceof Algorithm) {
            return printToString(((Algorithm) algorithm).toXML());
        }
        return "";
    }

    /**
     * Loads algorithm with results of calculation either from algorithm xml from question bank
     * and xml from taken question or only from taken question
     *
     * @param algorithmXML        algorithm xml from question bank
     * @param algorithmResultsXML algorithm xml from taken question
     * @return algorithm with results of calculation
     */
    public static iParameterProvider loadAlgorithm(Element algorithmXML, Element algorithmResultsXML)
        throws AlgorithmException {
        iParameterProvider algorithm = null;
        // get version
        String version = null;
        if (algorithmResultsXML != null) {
            version = algorithmResultsXML.getAttributeValue("version");
        }
        /** second version of algorithm storage */
        boolean secondVersion = (version != null && version.trim().equals("2")) || (algorithmResultsXML == null);
        /** load algorithm */
        if (algorithmXML != null && secondVersion) {
            /**
             * Create algorithm from question xml
             */
            algorithm = AlgorithmFactory.createAlgorithm(algorithmXML);
            /**
             * Load algorithm results from taken algorithm xml
             */
            loadAlgorithm(algorithm, algorithmResultsXML);
            /**
             * Set version 2 to loaded algorithm
             */
            algorithm.setVersion("2");
        }
        /** first version of algorithm storage */
        else {
            algorithm = AlgorithmFactory.createAlgorithm(algorithmResultsXML);
        }
        return algorithm;
    }

    /**
     * Loads algorithm from results XML
     *
     * @param algorithm           to calculate
     * @param algorithmResultsXML results XML
     */
    public static void loadAlgorithm(iParameterProvider algorithm, String algorithmResultsXML)
        throws IOException, JDOMException, AlgorithmException {
        Element root2 = null;

        if (algorithmResultsXML != null && !algorithmResultsXML.trim().equals("")) {
            Document doc = load(new StringReader(algorithmResultsXML));
            root2 = doc.getRootElement();
            loadAlgorithm(algorithm, root2);
        }
    }

    /**
     * Loads algorithm from results XML
     *
     * @param algorithm           to calculate
     * @param algorithmResultsXML results XML
     */
    public static void loadAlgorithm(iParameterProvider algorithm, Element algorithmResultsXML)
        throws AlgorithmException {
        if (algorithm != null && algorithmResultsXML != null) {
            if (algorithm instanceof BshParameterProvider) {
                ((BshParameterProvider) algorithm).load(algorithmResultsXML);
            } else if (algorithm instanceof Algorithm) {
                ((Algorithm) algorithm).load(algorithmResultsXML);
            }
        }
    }

    /**
     * Loads algorithm with results of calculation either from algorithm xml from question bank
     * and xml from taken question or only from taken question
     *
     * @param algorithmXML        algorithm xml from question bank
     * @param algorithmResultsXML algorithm xml from taken question
     * @return algorithm with results of calculation
     */
    public static iParameterProvider loadAlgorithm(String algorithmXML, String algorithmResultsXML)
        throws IOException, JDOMException, AlgorithmException {
        Element root = null;

        if (algorithmXML != null && !algorithmXML.trim().equals("")) {
            Document doc = load(new StringReader(algorithmXML));
            root = doc.getRootElement();

            Element root2 = null;

            if (algorithmResultsXML != null && !algorithmResultsXML.trim().isEmpty()) {
                doc = load(new StringReader(algorithmResultsXML));
                root2 = doc.getRootElement();
            }

            return loadAlgorithm(root, root2);
        }
        return null;
    }

    /**
     * Save algorithm according to its version. Version 2 only
     * saves calculated results and version 1 updates question bank.
     *
     * @param algorithm
     */
    public static String saveAlgorithm(iParameterProvider algorithm) throws IOException {
        if (algorithm instanceof BshParameterProvider) {
            return printToString(((BshParameterProvider) algorithm).save());
        } else if (algorithm instanceof Algorithm) {
            return printToString(((Algorithm) algorithm).save());
        }

        return "";
    }

    /**
     * Replaces variable within parameter provider.
     *
     * @param algorithm
     * @param name
     * @param newName
     */
    public static void renameParameter(iParameterProvider algorithm, String name, String newName) {
        if (algorithm instanceof BshParameterProvider) {
            ((BshParameterProvider) algorithm).renameParameter(name, newName);
        } else if (algorithm instanceof Algorithm) {
            ((Algorithm) algorithm).renameParameter(name, newName);
        }
    }

    /**
     * Prints the document into a string.
     *
     * @param doc the document.
     * @return the string representation of the document.
     * @throws IOException on I/O error.
     */
    public static String printToString(Document doc) throws IOException {
        StringWriter out = new StringWriter();
        new XMLOutputter(Format.getCompactFormat()).output(doc, out);
        return out.toString();
    }

    /**
     * Prints the document into a string.
     *
     * @param doc the document.
     * @return the string representation of the document.
     * @throws java.io.IOException on I/O error.
     */
    public static String printToString(Element doc) throws IOException {
        StringWriter out = new StringWriter();
        new XMLOutputter(Format.getCompactFormat()).output(doc, out);
        return out.toString();
    }
}

