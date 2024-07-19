// Created: 17.09.2004 T 16:27:26
package org.clematis.math.algorithm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.clematis.math.AbstractConstant;
import org.clematis.math.Constant;
import org.clematis.math.SimpleValue;
import org.clematis.math.StringConstant;
import org.clematis.math.functions.generic;
import org.clematis.math.iValue;
import org.clematis.math.io.OutputFormatSettings;
import org.jdom2.CDATA;
import org.jdom2.Element;

/**
 * A representation of algorithm in question. This is
 * a formula for obtaining random variables values for a question.
 */
public class Algorithm extends DefaultParameterProvider {
    /**
     * Maximum number of algorithm iterations
     */
    protected static final int MAXIMUM_ZERO_CHECKS = 10;
    /**
     * Maximum number of algorithm iterations
     */
    protected static final int MAXIMUM_CONDITION_CHECKS = 100;
//************************ FORMAT SETTINGS ********************************

    /**
     * Sets format settings for this algorithm
     */
    public void setFormatSettings(OutputFormatSettings fs) {
        super.setFormatSettings(fs);
        //Set format settings for children algorithms
        for (iParameterProvider aChildren : children) {
            aChildren.setFormatSettings(fs);
        }
    }
//************************ ADD, FIND, RENAME OR REMOVE PARAMETERS ************************

    /**
     * Adds parameter and sets itself as parameter container.
     *
     * @param p the parameter.
     */
    public void addParameter(Parameter p) throws AlgorithmException {
        super.addParameter(p);
        p.setContainer(this);
    }

    /**
     * Returns parameter, found by its token
     *
     * @return parameter, found by its token
     */
    public Parameter getParameter(String name) {
        Parameter param = super.getParameter(name);
        /**
         * Look for parameter in parent algorithm
         */
        if (param == null && getParent() != null) {
            param = getParent().getParameter(name);
        }
        /**
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
        /** find suffix */
        int no = getLastSimilarParameterNo(newParamName, 0);
        if (no != 0) {
            paramName = paramName + no;
        }
        /** rename parameter */
        super.renameParameter(paramName, newParamName);
        /** rename parameter among children */
        for (iParameterProvider aChildren : children) {
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
        /**
         * Look for parameter in parent algorithm
         */
        if (param == null && getParent() != null) {
            param = getParent().getParameterByCustomIdent(answerIdent);
        }
        /**
         * Return parameter may be null
         */
        return param;
    }
//************************ CALCULATE PARAMETERS ************************

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
    public void calculateParameters(HashMap<Key, iValue> params) throws AlgorithmException {
        boolean success = false;
        String message = "";
        int conditionFailures = 0;
        // attempts to pass conditions
        while (conditionFailures <= Algorithm.MAXIMUM_CONDITION_CHECKS) {
            try {
                //attempts to get rid of zeros
                int zeroFailures = 0;
                boolean lastAttempt;
                while (lastAttempt = zeroFailures <= Algorithm.MAXIMUM_ZERO_CHECKS) {
                    // do not interrupt calculation if zero neighbourhood is reached
                    // in the last attempt
                    boolean zero_failed = calculateParameters(params, lastAttempt);
                    if (zero_failed) {
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
        /**
         * Exception if no success in avoiding conditions
         */
        if (!success) {
            throw new AlgorithmException(message);
        }
        /**
         * Process children
         */
        for (iParameterProvider aChildren : children) {
            /**
             * Inherit calculated parameters
             */
            aChildren.calculateParameters(params);
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
    private boolean calculateParameters(HashMap<Key, iValue> params, boolean calculateAllParameters)
        throws AlgorithmException {
        boolean zero_failed = false;
        HashMap<String, Integer> randomized_params_counter = new HashMap<String, Integer>();
        for (int i = 0; i < lines.size(); i++) {
            /** key here is always without braces */
            Key key = lines.get(i);
            /** parameter */
            if (!key.isFunction()) {
                /**
                 * Directly get parameter from algorithm
                 */
                Parameter param = getParameter(key);
                /**
                 * Parameter result
                 */
                AbstractConstant ac = null;
                /**
                 * Try to find constant in extra list
                 */
                if (param.isRandomized()) {
                    int c = 0;
                    if (randomized_params_counter.containsKey(param.getName())) {
                        c = randomized_params_counter.get(param.getName());
                    }
                    iValue value = findKey(param.getName(), c, params);
                    randomized_params_counter.put(param.getName(), c + 1);
                    /**
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
                    if (ac != null) {
                        //log.debug("Parameter " + param.getName() + " is loaded from initial values set with " + ac.getValue(null));
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
                /**
                 * If parameter is condition, check if condition passed
                 */
                if (param.isCondition() && !param.isConditionPassed()) {
                    //log.debug("Condition failed: " + param.getName() + " = " + param.getCode());
                    throw new ConditionException(param.getName(), param.getCode(), i + 1);
                }
                /**
                 * Check the value of correct answer,
                 * if it too close to zero, take another attempt
                 * to calculate parameters.
                 */
                if (param.getCurrentResult() != null) {
                    AbstractConstant result = param.getCurrentResult();
                    if (result instanceof Constant) {
                        double resV = Math.abs(((Constant) result).getNumber());
                        if (resV > 0 && resV < 10e-9) {
                            //log.debug("Zero failed: " + param.getName() + " = " + param.getCode() );
                            zero_failed = true;
                        }
                    }
                }
                /**
                 * Load parameter for use
                 */
                loadForUse(param);
            }
            /** function */
            else {
                /** load function for use */
                functionFactory.loadForUse(key);
            }
            // save laps, break calculation and get another attempt
            // to get rid of zeros only if attempts remain
            if (zero_failed && !calculateAllParameters) {
                break;
            }
        }
        finishAndClear();
        return zero_failed;
    }
//************************ CREATE FROM AND SAVE TO XML *********************

    /**
     * Copy original_algorithm from initial one. Restores algorithm from initial values
     * of original one or calculates it one again
     *
     * @param original_algorithm question original_algorithm
     * @return calculated original_algorithm instance
     * @throws AlgorithmException throws exception if original_algorithm cannot be created
     */
    public static Algorithm createFromAlgorithm(iParameterProvider original_algorithm) throws AlgorithmException {
        if (original_algorithm != null && original_algorithm instanceof Algorithm) {
            Element xml = ((Algorithm) original_algorithm).toXML();
            Algorithm algorithm = Algorithm.createFromXML(xml);
            Element resultsXml = ((Algorithm) original_algorithm).save();
            if (algorithm != null) {
                algorithm.setFormatSettings(original_algorithm.getFormatSettings());
                /** restore algorithm from initial values or calculate it one again */
                algorithm.load(resultsXml);
            }
            return algorithm;
        }
        return null;
    }

    /**
     * Copy algorithm from initial one. Restores algorithm from initial values or calculates it one again
     *
     * @param original_algorithm question algorithm
     * @param params             some parameters values
     * @return calculated algorithm instance
     * @throws AlgorithmException throws exception if algorithm cannot be created
     */
    public static Algorithm createFromAlgorithm(iParameterProvider original_algorithm, HashMap<Key, iValue> params)
        throws AlgorithmException {
        if (original_algorithm != null && original_algorithm instanceof Algorithm) {
            Element ser = ((Algorithm) original_algorithm).toXML();
            Algorithm algorithm = Algorithm.createFromXML(ser);
            if (algorithm != null) {
                algorithm.setFormatSettings(original_algorithm.getFormatSettings());
                /** restore algorithm from initial values or calculate it one again */
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
    public static Algorithm createFromXML(Element algorithmXML) throws AlgorithmException {
        if (algorithmXML != null && algorithmXML.getChildren().size() > 0) {
            Algorithm algorithm = new Algorithm();
            if (algorithmXML.getAttribute("ident") != null) {
                algorithm.setIdent(algorithmXML.getAttributeValue("ident"));
            }
            /** go through children - algorithm lines */
            List lines = algorithmXML.getChildren();
            if (lines != null) {
                for (Object line : lines) {
                    Element element = (Element) line;

                    if (element.getName().equals("param")) {
                        algorithm.addParameter(Parameter.create(element));
                    } else if (element.getName().equals("function")) {
                        algorithm.addFunction(generic.create(element));
                    } else if (element.getName().equals("algorithm")) {
                        Algorithm child = Algorithm.createFromXML(element);
                        String key = element.getAttributeValue("ident");
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
        HashMap<Key, iValue> params = loadParameters(algorithmResultsXml);
        this.calculateParameters(params);
    }

    /**
     * Load initial values from algorithm xml
     *
     * @param algElement algorithm xml
     * @return initial values map, with algorithm idents in keys
     */
    private HashMap<Key, iValue> loadParameters(Element algElement) {
        /**  load parameters for this algorithm */
        HashMap<Key, iValue> params = new HashMap<Key, iValue>();
        if (algElement != null) {
            List elements = algElement.getChildren();
            for (Object element : elements) {
                if (element instanceof Element e) {
                    String name = e.getName();
                    if (name.equals("param")) {
                        /**
                         * Load parameter from xml
                         */
                        Parameter p = Parameter.loadResult(e);
                        /**
                         * Construct key
                         */
                        Key key = new Key(p.getName());
                        String ident = algElement.getAttributeValue("ident");
                        if (ident != null && !ident.trim().equals("")) {
                            key.setPartId(ident);
                        }
                        /**
                         * Find similar parameter
                         */
                        while (params.get(key) != null) {
                            key.setNo(key.getNo() + 1);
                        }
                        /**
                         * Put key with token and number
                         */
                        if (p.getCurrentResult() != null) {
                            params.put(key, p.getCurrentResult().copy());
                        }
                    } else if (name.equals("algorithm")) {
                        params.putAll(loadParameters(e));
                    }
                }
            }
        }
        return params;
    }

    /**
     * Saves algorithm calculation results
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    public Element save() {
        Element algElement = new Element("algorithm");
        algElement.setAttribute("version", "2");

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
                /** save only initial conditions */
                if (param.isRandomized() && param.getCurrentResult() != null) {
                    algElement.addContent(param.saveResult());
                }
            }
        }
        /**
         * Add child algorithms
         */
        for (iParameterProvider aChildren : children) {
            if (aChildren instanceof Algorithm) {
                Element calc_xml = ((Algorithm) aChildren).save();
                algElement.addContent(calc_xml);
            }
        }

        return algElement;
    }

    /**
     * Converts algorithm to JDOM.
     *
     * @return <code>Element</code> representing root of algorithm's JDOM.
     */
    public Element toXML() {
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
                algElement.addContent(param.toXML());
            } else {
                generic function = functionFactory.getGenericFunction(key);
                algElement.addContent(function.toXML());
            }
        }
        /**
         * Add child algorithms
         */
        for (iParameterProvider algorithm : children) {
            if (algorithm instanceof Algorithm) {
                algElement.addContent(((Algorithm) algorithm).toXML());
            }
        }

        return algElement;
    }
//**************************** QUESTION XML OPERATIONS *****************************

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
        for (iParameterProvider algorithm : children) {
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

        for (iParameterProvider algorithm : children) {
            if (algorithm instanceof Algorithm) {
                no = ((Algorithm) algorithm).getLastSimilarParameterNo(name, no);
            }
        }

        return no;
    }
}
