// Created: 14.04.2005 T 11:24:27
package org.clematis.math.v2.algorithm;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.IValue;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * Factory to create algorithms from xml
 */
public class AlgorithmFactory {

    public static final String VERSION_2 = "2";
    /**
     * The JDOM builder.
     */
    private static final SAXBuilder BUILDER = new SAXBuilder();

    /* config builder */
    static {
        BUILDER.setExpandEntities(true);
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
        try (in) {
            return BUILDER.build(in);
        }
    }


    /**
     * Create algorithm from xml notation
     *
     * @param algorithmXML - xml notation of an algorithm
     */
    private static IParameterProvider createAlgorithm(Element algorithmXML) throws AlgorithmException {
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
     * @param algorithm to get a copy of
     * @return a copy of given algorithm
     */
    public static IParameterProvider copyAlgorithm(
        IParameterProvider algorithm, HashMap<Key, IValue> params
    ) throws AlgorithmException {

        IParameterProvider result = algorithm;
        if (algorithm instanceof BshParameterProvider) {
            result = BshParameterProvider.createFromBshParameterProvider(algorithm, params);
        } else if (algorithm instanceof Algorithm) {
            result = Algorithm.createFromAlgorithm(algorithm, params);
        }
        return result;
    }

    /**
     * Returns a copy of given algorithm
     *
     * @param algorithm to get a copy of
     * @return a copy of given algorithm
     */
    public static IParameterProvider copyAlgorithm(IParameterProvider algorithm) throws AlgorithmException {
        return copyAlgorithm(algorithm, null);
    }

    /**
     * Loads algorithm with results of calculation either from algorithm xml from question bank
     * and xml from taken question or only from taken question
     *
     * @param algorithmXML        algorithm xml from question bank
     * @param algorithmResultsXML algorithm xml from taken question
     * @return algorithm with results of calculation
     */
    public static IParameterProvider loadAlgorithm(Element algorithmXML, Element algorithmResultsXML)
        throws AlgorithmException {

        IParameterProvider algorithm;

        // get version
        String version = null;
        if (algorithmResultsXML != null) {
            version = algorithmResultsXML.getAttributeValue("version");
        }

        /* second version of algorithm storage */
        if (algorithmXML != null && version != null && version.trim().equals(VERSION_2)) {
            /*
             * Create algorithm from question xml
             */
            algorithm = AlgorithmFactory.createAlgorithm(algorithmXML);
            /*
             * Load algorithm results from taken algorithm xml
             */
            if (algorithm instanceof BshParameterProvider) {
                ((BshParameterProvider) algorithm).load(algorithmResultsXML);
            } else if (algorithm instanceof Algorithm) {
                ((Algorithm) algorithm).load(algorithmResultsXML);
            }
            /*
             * Set version 2 to loaded algorithm
             */
            algorithm.setVersion(VERSION_2);
        } else {
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
    public static void loadAlgorithm(IParameterProvider algorithm, String algorithmResultsXML)
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
    public static void loadAlgorithm(IParameterProvider algorithm, Element algorithmResultsXML)
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
     * Loads algorithm with results of calculation either from algorithm and results xml
     *
     * @param algorithmXML        algorithm xml
     * @param algorithmResultsXML algorithm results xml
     * @return algorithm with results of calculation
     */
    public static IParameterProvider loadAlgorithm(String algorithmXML, String algorithmResultsXML)
        throws IOException, JDOMException, AlgorithmException {
        Element root;

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
     * Replaces variable within parameter provider.
     *
     * @param algorithm
     * @param name
     * @param newName
     */
    public static void renameParameter(IParameterProvider algorithm, String name, String newName) {
        if (algorithm instanceof BshParameterProvider) {
            ((BshParameterProvider) algorithm).renameParameter(name, newName);
        } else if (algorithm instanceof Algorithm) {
            ((Algorithm) algorithm).renameParameter(name, newName);
        }
    }

    /**
     * Save algorithm according to its version.
     *
     * @param algorithm instance to save
     */
    public static String saveAlgorithm(IParameterProvider algorithm) throws IOException {
        if (algorithm instanceof BshParameterProvider) {
            return printToString(((BshParameterProvider) algorithm).save());
        } else if (algorithm instanceof Algorithm) {
            return printToString(((Algorithm) algorithm).save());
        }

        return "";
    }

    /**
     * Store algorithm in xml string
     *
     * @param algorithm to store
     * @return xml string
     */
    public static String toXML(IParameterProvider algorithm) throws IOException {
        if (algorithm instanceof BshParameterProvider) {
            return printToString(((BshParameterProvider) algorithm).toXML());
        } else if (algorithm instanceof Algorithm) {
            return printToString(((Algorithm) algorithm).toXML());
        }
        return "";
    }

    /**
     * Prints the document into a string.
     *
     * @param doc the document.
     * @return the string representation of the document.
     * @throws IOException on I/O error.
     */
    public static String printToString(Document doc) throws IOException {
        try (StringWriter out = new StringWriter()) {
            new XMLOutputter(Format.getCompactFormat()).output(doc, out);
            return out.toString();
        }
    }

    /**
     * Prints the document into a string.
     *
     * @param doc the document.
     * @return the string representation of the document.
     * @throws java.io.IOException on I/O error.
     */
    public static String printToString(Element doc) throws IOException {
        try (StringWriter out = new StringWriter()) {
            new XMLOutputter(Format.getCompactFormat()).output(doc, out);
            return out.toString();
        }
    }

}
