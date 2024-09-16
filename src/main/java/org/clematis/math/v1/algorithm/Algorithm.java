// Created: 17.09.2004 T 16:27:26
package org.clematis.math.v1.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.clematis.math.AlgorithmException;
import org.clematis.math.ConditionException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.IValue;
import org.clematis.math.XMLConstants;
import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IFunction;
import org.clematis.math.v1.SimpleValue;
import org.clematis.math.v1.StringConstant;
import org.clematis.math.v1.functions.GenericFunction;
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
     * Add child algorithm
     *
     * @param key       for storing algorithm
     * @param algorithm child algorithm
     */
    public void addAlgorithm(String key, Algorithm algorithm) {
        /*
         * Inherit parameters from parent algorithm
         */
        algorithm.setParent(this);
        /*
         * Set ident to calculated algorithm
         */
        algorithm.setIdent(key);
        /*
         * Store algorithm
         */
        children.add(algorithm);
    }

    /*
     * Returns algorithm, stored under given ident
     *
     * @param key ident of algorithm
     * @return algorithm, stored under given ident
     */
    public IParameterProvider getAlgorithm(String key) {

        IParameterProvider ret = null;

        if (getIdent() != null && getIdent().equals(key)) {
            return this;
        }

        for (IParameterProvider algorithm : children) {
            if (algorithm != null) {
                ret = algorithm.getAlgorithm(key);
                if (ret != null) {
                    break;
                }
            }
        }

        return ret;
    }

    /*
     * Calculates values of all parameters in algorithm.
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
                            final String v = ((SimpleValue) value).getValue(null);
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
     * @param params some parameters values
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
                        algorithm.addFunction(GenericFunction.create(line));
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

    /*
     * Loads algorithm calculation results
     */
    void load(Element algElement) {
        Map<Key, IValue> params = loadParameters(algElement);
        try {
            this.calculateParameters(params);
        } catch (AlgorithmException e) {
            //log.error( e );
        }
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
                GenericFunction function = functionFactory.getGenericFunction(key);
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

    /*
     * Finds all parameters dependent on the parameter.
     *
     * @param param the parameter from which the dependencies are searched.
     * @return ArrayList with parameters if exist or null.
     */
    public ArrayList<Parameter> findDependencies(Parameter param) {
        ArrayList<Parameter> dependencies = null;
        if (param.getExpressionRoot() != null) {
            dependencies = new ArrayList<>();
            findDependencies(param.getExpressionRoot(), dependencies);
        }
        return dependencies;
    }

    /*
     * Recursively processes the expression tree and picks up all Parameters objects.
     *
     * @param exprItem     the expression root.
     * @param dependencies container for dependencies.
     */
    private void findDependencies(IExpressionItem exprItem, ArrayList<Parameter> dependencies) {
        if (exprItem instanceof IFunction function) {
            for (IExpressionItem arg : function.getArguments()) {
                findDependencies(arg, dependencies);
            }
        } else if (exprItem instanceof StringConstant constant) {
            ArrayList<String> paramNames = SimpleParameter.findParameters(constant.getValue(null), this, false);
            for (String name : paramNames) {
                Parameter param = getParameter(name);
                if (param != null) {
                    dependencies.add(param);
                }
            }
        } else if (exprItem instanceof ParameterReference paramRef) {
            dependencies.add(paramRef.getOrigin());
        }
    }

    /**
     * Revalidate calculated parameters to restore missing parameter
     * code, but not to change results.
     * <p>
     * before: code=? after: code=sig(3, 2*$k*....
     *
     * @param algElement question algorithm xml
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    public void revalidateParameters(Element algElement) throws AlgorithmException {
        if (algElement != null) {
            /*
             * Create algorithm instance
             */
            IParameterProvider algorithm = Algorithm.createFromQuestionXML(algElement);
            /*
             * Get algorithm with id of this taken algorithm
             */
            IParameterProvider p = algorithm.getAlgorithm(this.getIdent());
            if (p != null) {
                algorithm = p;
            }
            /*
             * Iterate through our parameters
             */
            Iterator<Key> it = lines.iterator();
            /*
             * Get algorithm parameters
             */
            Parameter[] algParams = algorithm.getParameters();
            int cursor = 0;
            while (it.hasNext()) {
                Key key = it.next();
                /* parameter */
                if (!key.isFunction()) {
                    /* directly get parameter */
                    Parameter param = getParameter(key);
                    Parameter pa = null;
                    if (cursor < algParams.length) {
                        pa = algParams[cursor];
                        cursor++;
                    }

                    if (param.getCode() == null && pa != null) {
                        param.setCode(pa.getCode());
                    }
                } else {
                    this.functionFactory.loadForUse(key);
                }
            }
        }
        finishAndClear();
        /*
         * Process children
         */
        for (IParameterProvider alg : children) {
            alg.revalidateParameters(algElement);
        }
    }

    /**
     * Replaces parameters references with expression roots
     */
    public void parseParametersWithFullTree() throws AlgorithmException {
        Iterator<Key> it = lines.iterator();
        int line = 1;
        while (it.hasNext()) {
            Key key = it.next();
            /* parameter */
            if (!key.isFunction()) {
                /* directly get parameter */
                Parameter param = getParameter(key);
                try {
                    param.parseWithFullTree(this);
                } catch (AlgorithmException ex) {
                    throw new AlgorithmException(ex.getMessage(), line);
                }
                /* load parameter for usage */
                loadForUse(param);
            } else {
                this.functionFactory.loadForUse(key);
            }
            line++;
        }
        finishAndClear();
    }

    /*
     * Replaces parameters references with ParameterReference objects.
     */
    public void parseParametersWithReferences() throws AlgorithmException {
        Iterator<Key> it = lines.iterator();
        int line = 1;
        while (it.hasNext()) {
            Key key = it.next();
            /* parameter */
            if (!key.isFunction()) {
                /* directly get parameter */
                Parameter param = getParameter(key);
                try {
                    param.parseWithReferences(this);
                } catch (AlgorithmException ex) {
                    throw new AlgorithmException(ex.getMessage(), line);
                }
                /* load parameter for usage */
                loadForUse(param);
            } else {
                this.functionFactory.loadForUse(key);
            }
            line++;
        }
        finishAndClear();
    }

}
