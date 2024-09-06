// Created: 17.09.2004 T 16:27:26
package org.clematis.math.v2.algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.clematis.math.v1.io.XMLConstants;
import org.clematis.math.v2.AbstractConstant;
import org.clematis.math.v2.AlgorithmException;
import org.clematis.math.v2.Constant;
import org.clematis.math.v2.IValue;
import org.clematis.math.v2.SimpleValue;
import org.clematis.math.v2.StringConstant;
import org.clematis.math.v2.functions.generic;
import org.clematis.math.v2.io.OutputFormatSettings;
import org.jdom2.CDATA;
import org.jdom2.Element;

import lombok.Getter;

/**
 * This class adds calculation to the basic default parameters provider with some
 * tolerance and confition checks policies
 */
@Getter
public class Algorithm extends DefaultParameterProvider {

    /*
     * Maximum number of algorithm iterations
     */
    protected static final int MAXIMUM_ZERO_CHECKS = 10;
    /*
     * Maximum number of algorithm iterations
     */
    protected static final int MAXIMUM_CONDITION_CHECKS = 100;
    /**
     * Numeric interval for condition checks
     */
    protected static final double TOLERANCE = 10e-9;
    /*
     * Adds parameter and sets itself as parameter container.
     *
     * @param p the parameter.
     */
    public void addParameter(Parameter p) throws AlgorithmException {
        super.addParameter(p);
        p.setContainer(this);
    }
    /*
     * Returns parameter, found by its name
     *
     * @return parameter, found by its name
     */
    public Parameter getParameter(String name) {
        Parameter param = super.getParameter(name);
        /*
         * Look for parameter in parent algorithm
         */
        if (param == null && getParent() != null) {
            param = getParent().getParameter(name);
        }
        /*
         * Return parameter may be null
         */
        return param;
    }

    /**
     * Renames parameter
     *
     * @param paramName    of existing parameter
     * @param newParamName of new parameter
     */
    public void renameParameter(String paramName, String newParamName) {
        /* find suffix */
        int no = getLastSimilarParameterNo(newParamName, 0);
        if (no != 0) {
            paramName = paramName + no;
        }
        /* rename parameter */
        super.renameParameter(paramName, newParamName);
        /* rename parameter among children */
        for (IParameterProvider aChildren : children) {
            if (aChildren instanceof Algorithm) {
                ((Algorithm) aChildren).renameParameter(paramName, newParamName);
            }
        }
    }

    /**
     * Finds parameter by its answer ident
     *
     * @param answerIdent answer ident
     * @return found parameter or null
     */
    public Parameter getParameterByCustomIdent(String answerIdent) {
        Parameter param = super.getParameterByCustomIdent(answerIdent);
        /*
         * Look for parameter in parent algorithm
         */
        if (param == null && getParent() != null) {
            param = getParent().getParameterByCustomIdent(answerIdent);
        }
        /*
         * Return parameter may be null
         */
        return param;
    }

    /**
     * Calculates values of all parameters participating in algorithm.
     *
     * @throws AlgorithmException on error.
     */
    public void calculateParameters() throws AlgorithmException {
        calculateParameters(null);
    }

    /**
     * Calculates values of all parameters participating in algorithm.
     *
     * @throws AlgorithmException on error.
     */
    public void calculateParameters(Map<Key, IValue> params) throws AlgorithmException {
        boolean success = false;
        String message = "";
        int conditionFailures = 0;
        // attempts to pass conditions
        while (conditionFailures <= Algorithm.MAXIMUM_CONDITION_CHECKS) {
            try {
                //attempts to get rid of zeros
                int zeroFailures = 0;
                while (zeroFailures <= Algorithm.MAXIMUM_ZERO_CHECKS) {
                    // do not interrupt calculation if zero neighbourhood is reached
                    // in the last attempt
                    boolean zeroFailed = calculateParameters(params,
                        zeroFailures == Algorithm.MAXIMUM_ZERO_CHECKS
                    );
                    if (zeroFailed) {
                        zeroFailures++;
                    } else {
                        break;
                    }
                }
                // success
                success = true;
                break;
            } catch (ConditionException ex) {
                // condition failed
                conditionFailures++;
                message = ex.getMessage();
            }
        }
        /*
         * Exception if no success in avoiding conditions
         */
        if (!success) {
            throw new AlgorithmException(message);
        }
        /*
         * Process children
         */
        for (IParameterProvider algorithm : children) {
            /*
             * Inherit calculated parameters
             */
            algorithm.calculateParameters(params);
        }
    }

    /**
     * Recursively calculates parameters of this algorithm and all its children.
     * Creates a calculated algorithm branch.
     *
     * @param params                 optional hashtable, containing parameters
     * @param calculateAllParameters flag preventing from breaking of calculation upon
     *                               getting zero neighbourhood in any of parameters
     * @return true if zero in calculation result occured
     * @throws ConditionException is thrown if condition is not satisfied
     * @throws AlgorithmException is thrown if error occurs in evaluating any parameter
     */
    @SuppressWarnings({"checkstyle:NestedIfDepth", "checkstyle:CyclomaticComplexity"})
    private boolean calculateParameters(Map<Key, IValue> params, boolean calculateAllParameters)
        throws AlgorithmException {

        boolean zeroFailed = false;
        Map<String, Integer> randomizedParamsCounter = new HashMap<>();
        for (int i = 0; i < lines.size(); i++) {
            /* key here is always without braces */
            Key key = lines.get(i);
            /* parameter */
            if (!key.isFunction()) {
                /*
                 * Directly get parameter from algorithm
                 */
                Parameter param = getParameter(key);
                /*
                 * Parameter result
                 */
                AbstractConstant ac = null;
                /*
                 * Try to find constant in extra list
                 */
                if (param.isRandomized()) {
                    int c = 0;
                    if (randomizedParamsCounter.containsKey(param.getName())) {
                        c = randomizedParamsCounter.get(param.getName());
                    }
                    IValue value = findKey(param.getName(), c, params);
                    randomizedParamsCounter.put(param.getName(), c + 1);
                    /*
                     * Only abstract constants and simple values are valid input values
                     */
                    if (value != null) {
                        if (value instanceof AbstractConstant) {
                            ac = (AbstractConstant) value;
                        } else if (value instanceof SimpleValue) {
                            final String v = ((SimpleValue) value).getValue();
                            try {
                                ac = new Constant(v);
                            } catch (NumberFormatException ex) {
                                ac = new StringConstant(v);
                            }
                        }
                    }
                }
                if (ac != null) {
                    param.setCurrentResult(ac);
                } else {
                    try {
                        param.calculate(this, i);
                    } catch (AlgorithmException ex) {
                        throw new AlgorithmException(ex.getMessage(), i + 1);
                    } catch (Exception ex) {
                        throw new AlgorithmException(ex.toString(), i + 1);
                    }
                }
                /*
                 * If parameter is condition, check if condition passed
                 */
                if (param.isCondition() && !param.isConditionPassed()) {
                    throw new ConditionException(param.getName(), param.getCode(), i + 1);
                }
                /*
                 * Check the value of correct answer,
                 * if it too close to zero, take another attempt
                 * to calculate parameters.
                 */
                if (param.getCurrentResult() != null) {
                    AbstractConstant result = param.getCurrentResult();
                    if (result instanceof Constant) {
                        double resV = Math.abs(((Constant) result).getNumber());
                        if (resV > 0 && resV < TOLERANCE) {
                            zeroFailed = true;
                        }
                    }
                }
                /*
                 * Load parameter for use
                 */
                loadForUse(param);
            } else {
                /* load function for use */
                functionFactory.loadForUse(key);
            }
            // save laps, break calculation and get another attempt
            // to get rid of zeros only if attempts remain
            if (zeroFailed && !calculateAllParameters) {
                break;
            }
        }
        finishAndClear();
        return zeroFailed;
    }

    /**
     * Copies algorithm from the initial algorithm and results
     *
     * @param qalg question algorithm
     * @return calculated algorithm instance
     * @throws AlgorithmException throws exception if algorithm cannot be created
     */
    public static Algorithm createFromAlgorithm(IParameterProvider qalg) throws AlgorithmException {
        return createFromAlgorithm(qalg, null);
    }

    /**
     * Copy algorithm from initial one. Restores algorithm from initial values or calculates it one again
     *
     * @param qalg   question algorithm
     * @param params             some parameters values
     * @return calculated algorithm instance
     * @throws AlgorithmException throws exception if algorithm cannot be created
     */
    public static Algorithm createFromAlgorithm(IParameterProvider qalg, Map<Key, IValue> params)
        throws AlgorithmException {

        if (qalg instanceof Algorithm) {

            Element ser = qalg.toXML();
            Algorithm algorithm = Algorithm.createFromXML(ser);
            if (algorithm != null) {
                algorithm.setFormatSettings(qalg.getFormatSettings());
                algorithm.calculateParameters(params);
            }
            return algorithm;
        }
        return null;
    }

    /**
     * Creates algorithm from xml jdom element
     *
     * @param algorithmXML containing algorithm
     * @return algorithm object
     * @throws AlgorithmException if algorithm cannot be created
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    static Algorithm createFromXML(Element algorithmXML) throws AlgorithmException {

        if (algorithmXML != null && !algorithmXML.getChildren().isEmpty()) {
            Algorithm algorithm = new Algorithm();
            if (algorithmXML.getAttribute(XMLConstants.IDENT_ATTRIBUTE_NAME) != null) {
                algorithm.setIdent(algorithmXML.getAttributeValue(XMLConstants.IDENT_ATTRIBUTE_NAME));
            }
            /* go through children - algorithm lines */
            List<Element> lines = algorithmXML.getChildren();
            if (lines != null) {
                for (Element line : lines) {
                    if (line.getName().equals(XMLConstants.PARAM_ELEMENT_NAME)) {
                        algorithm.addParameter(Parameter.create(line));
                    } else if (line.getName().equals("function")) {
                        algorithm.addFunction(generic.create(line));
                    } else if (line.getName().equals(XMLConstants.ALGORITHM_ATTRIBUTE_NAME)) {
                        Algorithm child = Algorithm.createFromXML(line);
                        String key = line.getAttributeValue(XMLConstants.IDENT_ATTRIBUTE_NAME);
                        if (child != null) {
                            algorithm.addAlgorithm(key, child);
                        }
                    }
                }
            }
            return algorithm;
        }
        return null;
    }

    /**
     * Loads algorithm calculation results
     *
     * @param algorithmResultsXml algorithm calculation results
     * @throws AlgorithmException if algorithm cannot be created
     */
    public void load(Element algorithmResultsXml) throws AlgorithmException {
        Map<Key, IValue> params = loadParameters(algorithmResultsXml);
        this.calculateParameters(params);
    }

    /**
     * Load initial values from algorithm xml
     *
     * @param algElement algorithm xml
     * @return initial values map, with algorithm idents in keys
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    private Map<Key, IValue> loadParameters(Element algElement) {
        /*  load parameters for this algorithm */
        Map<Key, IValue> params = new HashMap<>();
        if (algElement != null) {
            List<Element> elements = algElement.getChildren();
            for (Object element : elements) {
                if (element instanceof Element e) {
                    String name = e.getName();
                    if (name.equals(XMLConstants.PARAM_ELEMENT_NAME)) {
                        /*
                         * Load parameter from xml
                         */
                        Parameter p = Parameter.loadResult(e);
                        /*
                         * Construct key
                         */
                        Key key = new Key(p.getName());
                        String ident = algElement.getAttributeValue(XMLConstants.IDENT_ATTRIBUTE_NAME);
                        if (ident != null && !ident.trim().isEmpty()) {
                            key.setPartId(ident);
                        }
                        /*
                         * Find similar parameter
                         */
                        while (params.get(key) != null) {
                            key.setNo(key.getNo() + 1);
                        }
                    /*
                     * Put key with name and number
                         */
                        if (p.getCurrentResult() != null) {
                            params.put(key, p.getCurrentResult().copy());
                        }
                    } else if (name.equals(XMLConstants.ALGORITHM_ATTRIBUTE_NAME)) {
                        params.putAll(loadParameters(e));
                    }
                }
            }
        }
        return params;
    }

    /**
     * Sets format settings for this algorithm
     */
    public void setFormatSettings(OutputFormatSettings fs) {
        super.setFormatSettings(fs);
        for (IParameterProvider algorithm : children) {
            algorithm.setFormatSettings(fs);
        }
    }

    /**
     * Saves algorithm calculation results
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    public Element save() {
        Element algElement = new Element(XMLConstants.ALGORITHM_ATTRIBUTE_NAME);
        algElement.setAttribute("version", "2");

        if (getIdent() != null) {
            algElement.setAttribute(XMLConstants.IDENT_ATTRIBUTE_NAME, getIdent());
        }
        /*
         * Add parameters
         */
        for (Key key : lines) {
            /*
             * Get instance of parameter
             */
            if (!key.isFunction()) {
                Parameter param = parameters.get(key);
                /* save only initial conditions */
                if (param.isRandomized()) {
                    algElement.addContent(param.saveResult());
                }
            }
        }
        /*
         * Add child algorithms
         */
        for (IParameterProvider algorithm : children) {
            Element calcXml = algorithm.save();
            algElement.addContent(calcXml);
        }

        return algElement;
    }

    /**
     * Converts algorithm to JDOM.
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    public Element toXML() {
        Element algElement = new Element(XMLConstants.ALGORITHM_ATTRIBUTE_NAME);
        if (getIdent() != null) {
            algElement.setAttribute(XMLConstants.IDENT_ATTRIBUTE_NAME, getIdent());
        }
        /*
         * Add parameters
         */
        for (Key key : lines) {
            /*
             * Get instance of parameter
             */
            if (!key.isFunction()) {
                Parameter param = parameters.get(key);
                algElement.addContent(param.toXML());
            } else {
                generic function = functionFactory.getGenericFunction(key);
                algElement.addContent(function.toXML());
            }
        }
        /*
         * Add child algorithms
         */
        for (IParameterProvider algorithm : children) {
            algElement.addContent(algorithm.toXML());
        }

        return algElement;
    }

    /**
     * Creates <code>Algorithm</code> object from QUESTION XML.
     *
     * @param algorithmXML XML representing the algorithm.
     * @return instance of Algorithm
     * @throws AlgorithmException if algorithm cannot be created
     */
    public static Algorithm createFromQuestionXML(Element algorithmXML) throws AlgorithmException {
        return createFromXML(algorithmXML);
    }

    /**
     * Saves algorithm to QUESTION XML.
     *
     * @return <code>Element</code> representing root of algorithm's JDOM.
     */
    public Element toQuestionXML() {
        Element algElement = new Element("algorithm");
        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
        }
        /**
         * Add parameters
         */
        for (Key key : lines) {
            /**
             * Get instance of parameter
             */
            if (!key.isFunction()) {
                Parameter param = parameters.get(key);
                Element paramElement = new Element("param");
                paramElement.setAttribute("token", param.getName());
                if (param.isContainsXML()) {
                    paramElement.setAttribute("contains_mathml", "true");
                }

                paramElement.addContent(new CDATA(param.getCode()));

                if (param.isCorrectAnswer()) {
                    paramElement.setAttribute("answer", "true");
                }
                algElement.addContent(paramElement);
            } else {
                generic function = functionFactory.getGenericFunction(key);
                algElement.addContent(function.toXML());
            }
        }
        /**
         * Add child algorithms
         */
        for (IParameterProvider algorithm : children) {
            if (algorithm instanceof Algorithm) {
                algElement.addContent(((Algorithm) algorithm).toQuestionXML());
            }
        }

        return algElement;
    }

    /**
     * Modify parameter token to safely insert into algorithm. Leaves the initial token as it is or
     * adds suffixes to make initial token unique in parameter provider.
     *
     * @param name of desired parameter to insert
     * @param no   number of last similar parameter
     * @return possibly modified token's suffix
     */
    int getLastSimilarParameterNo(String name, int no) {
        /**
         * Find the token within candidates and modify parameter token
         */
        Iterator<String> it = iterator();
        while (it.hasNext()) {
            // modify if we have met
            if (name.equals(it.next())) {
                no++;
            }
        }

        for (IParameterProvider algorithm : children) {
            if (algorithm instanceof Algorithm) {
                no = ((Algorithm) algorithm).getLastSimilarParameterNo(name, no);
            }
        }

        return no;
    }

    @Override
    public void clear() {

    }
}
