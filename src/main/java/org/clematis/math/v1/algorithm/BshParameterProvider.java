// Created: 11.04.2005 T 16:32:05
package org.clematis.math.v1.algorithm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.IValue;
import org.clematis.math.v1.SimpleValue;
import org.clematis.math.v1.StringConstant;
import org.clematis.math.v1.io.AbstractParameterFormatter;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Algorithm, based on Bean Shell evaluation
 */
public class BshParameterProvider extends AbstractParameterFormatter implements Serializable {

    public static final String ROOT_TAGE_NAME = "algorithm";
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String BSH_TYPE = "bsh";
    /**
     * Ident
     */
    @Getter
    @Setter
    protected String ident = null;
    /**
     * Version of originating persistent storage
     */
    @Getter
    @Setter
    protected String version = "1";
    /**
     * Java code to eval
     */
    private String code = null;
    /**
     * List of parameters
     * <p>
     * parameternam -> value
     */
    private final HashMap<String, String> parameters = new HashMap<>();

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
    public void calculateParameters(HashMap<Key, IValue> params) throws AlgorithmException {
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
     * @param algorithmXML XML representing the algorithm.
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
                /*
                 * Add parameters
                 */
                List<Element> calculatedParams = algorithmXML.getChildren("param");
                for (Element element : calculatedParams) {
                    algorithm.parameters.put(element.getAttributeValue("name"), element.getText());
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
        /*  load parameters */
        HashMap<Key, IValue> params = new HashMap<>();
        List<Element> calculatedParams = algElement.getChildren("param");
        for (Element element : calculatedParams) {
            Key key = new Key(element.getAttributeValue("name"));
            params.put(key, new SimpleValue(element.getText()));
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
        Element algElement = new Element(ROOT_TAGE_NAME);
        algElement.setAttribute(TYPE_ATTRIBUTE, BSH_TYPE);
        algElement.setAttribute("version", "2");

        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
        }

        /*  save parameters */
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
        Element algElement = new Element(ROOT_TAGE_NAME);
        algElement.setAttribute(TYPE_ATTRIBUTE, BSH_TYPE);

        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
        }

        Element codeElement = new Element("code");
        codeElement.setText(this.code);
        algElement.addContent(codeElement);

        /*  save parameters */
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
    static BshParameterProvider createFromBshParameterProvider(IParameterProvider qalg) throws AlgorithmException {
        return createFromBshParameterProvider(qalg, null);
    }

    /**
     * Creates algorithm for taken question
     *
     * @param qalg   question algorithm
     * @param params some parameters values
     * @return calculated algorithm instance
     */
    static BshParameterProvider createFromBshParameterProvider(IParameterProvider qalg, HashMap<Key, IValue> params)
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
