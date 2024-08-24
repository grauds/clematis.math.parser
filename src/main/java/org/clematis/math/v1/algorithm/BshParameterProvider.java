// Created: 11.04.2005 T 16:32:05
package org.clematis.math.v1.algorithm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.SimpleValue;
import org.clematis.math.v1.StringConstant;
import org.clematis.math.v1.iValue;
import org.jdom2.Element;

/**
 * Algorithm, based on Bean Shell evaluation
 */
public class BshParameterProvider extends AbstractParameterFormatter implements Serializable {
    /**
     * Java code to eval
     */
    private String code = null;
    /**
     * Ident
     */
    protected String ident = null;
    /**
     * Version of originating persistent storage
     */
    protected String version = "1";
    /**
     * List of parameters
     * <p>
     * parameternam -> value
     */
    private final HashMap<String, String> parameters = new HashMap<String, String>();

    /**
     * Calculates values of all parameters participating in algorithm.
     */
    public void calculateParameters() throws AlgorithmException {
    /*    parameters.clear();
        if ( code != null )
        {
            RestrictionManager.getRestrictedPrefixList().clear();
            Map<String , String > result = bsh.eval(code);
            parameters.putAll(result);
        }    */
    }

    /**
     * Calculates values of all parameters participating in algorithm.
     */
    public void calculateParameters(HashMap<Key, iValue> params) throws AlgorithmException {
    /*    parameters.clear();
        if ( code != null )
        {
            RestrictionManager.getRestrictedPrefixList().clear();
            Map<String , String > result = bsh.eval(code, params);
            parameters.putAll(result);
        }    */
    }

    /**
     * Returns parameter, found by custom ident.
     *
     * @param ident under which some parameters may store
     * @return parameter, found by custom ident
     */
    public Parameter getParameterByCustomIdent(String ident) {
        return null;
    }

    /**
     * Return parameter constant
     *
     * @param name parameter name
     * @return parameter value, string or double
     */
    public AbstractConstant getParameterConstant(String name) {
        if (parameters.containsKey(name)) {
            return new StringConstant(parameters.get(name));
        }
        return null;
    }

    /**
     * Provides the parameter.
     *
     * @param name the parameter name.
     * @return the parameter or null.
     */
    public Parameter getParameter(String name) {
        return new Parameter(name, getParameterConstant(name));
    }

    /**
     * Returns algorithm ident
     *
     * @return algorithm ident
     */
    public String getIdent() {
        return ident;
    }

    /**
     * Returns version of parameter provider
     * (for persistent storage versioning purposes)
     *
     * @return version of parameter provider
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets version of parameter provider
     * (for persistent storage versioning purposes)
     *
     * @param version of parameter provider
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Sets algorithm ident
     *
     * @param ident algorithm ident
     */
    public void setIdent(String ident) {
        this.ident = ident;
    }

    /**
     * Creates <code>BshParameterProvider</code> object from QUESTION XML.
     *
     * @param algorithmXML XML representing the algorithm.
     */
    static BshParameterProvider createFromQuestionXML(Element algorithmXML) {
        return createFromXML(algorithmXML);
    }

    /**
     * Create <code>BshParameterProvider</code> from xml
     *
     * @param algorithmXML
     */
    static BshParameterProvider createFromXML(Element algorithmXML) {
        /*
         * Take not trivial algorithm xml element
         */
        if (algorithmXML != null && !algorithmXML.getChildren().isEmpty()) {
            /*
             * Create new algorithm
             */
            Element code = algorithmXML.getChild("code");
            if (code != null) {
                BshParameterProvider algorithm = new BshParameterProvider();
                algorithm.code = code.getTextTrim();
                if (algorithmXML.getAttribute("ident") != null) {
                    algorithm.setIdent(algorithmXML.getAttributeValue("ident"));
                }
                /**
                 * Add parameters
                 */
                List calculatedParams = algorithmXML.getChildren("param");
                for (Object param : calculatedParams) {
                    if (param instanceof Element element) {
                        algorithm.parameters.put(element.getAttributeValue("name"), element.getText());
                    }
                }
                return algorithm;
            }
        }
        return null;
    }

    /**
     * Loads algorithm calculation results
     */
    void load(Element algElement) throws AlgorithmException {
        /**  load parameters */
        HashMap<Key, iValue> params = new HashMap<Key, iValue>();
        List calculatedParams = algElement.getChildren("param");
        for (Object param : calculatedParams) {
            if (param instanceof Element element) {
                Key key = new Key(element.getAttributeValue("name"));
                params.put(key, new SimpleValue(element.getText()));
            }
        }
        this.calculateParameters(params);
    }

    /**
     * Saves algorithm calculation results
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    @Override
    public Element save() {
        Element algElement = new Element("algorithm");
        algElement.setAttribute("type", "bsh");
        algElement.setAttribute("version", "2");

        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
        }

        /**  save parameters */
        for (String name : parameters.keySet()) {
            Element param = new Element("param");
            param.setAttribute("name", name);
            param.setText(parameters.get(name));
            algElement.addContent(param);
        }

        return algElement;
    }

    /**
     * Converts algorithm to JDOM.
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    @Override
    public Element toXML() {
        Element algElement = new Element("algorithm");
        algElement.setAttribute("type", "bsh");

        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
        }

        Element codeElement = new Element("code");
        codeElement.setText(this.code);
        algElement.addContent(codeElement);

        /**  save parameters */
   /*     for ( String name : parameters.keySet() )
        {
            Element param = new Element( "param" );
            param.setAttribute( "name", name );
            param.setText( parameters.get(name) );
            algElement.addContent( param );
        }
            */
        return algElement;
    }

    /**
     * Creates algorithm for taken question
     *
     * @param qalg question algorithm
     * @return calculated algorithm instance
     */
    static BshParameterProvider createFromBshParameterProvider(iParameterProvider qalg) throws AlgorithmException {
        return createFromBshParameterProvider(qalg, null);
    }

    /**
     * Creates algorithm for taken question
     *
     * @param qalg   question algorithm
     * @param params some parameters values
     * @return calculated algorithm instance
     */
    static BshParameterProvider createFromBshParameterProvider(iParameterProvider qalg, HashMap<Key, iValue> params)
        throws AlgorithmException {

        if (qalg instanceof BshParameterProvider qalgorithm) {

            BshParameterProvider algorithm = new BshParameterProvider();
            algorithm.code = qalgorithm.code;

            algorithm.setFormatSettings(qalg.getFormatSettings());
            algorithm.calculateParameters(params);

            return algorithm;
        }
        return null;
    }

    /**
     * Returns parameter names
     *
     * @return parameter names
     */
    public Iterator<String> iterator() {
        return parameters.keySet().iterator();
    }
}
