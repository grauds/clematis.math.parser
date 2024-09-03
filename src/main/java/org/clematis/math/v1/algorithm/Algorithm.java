// Created: 17.09.2004 T 16:27:26
package org.clematis.math.v1.algorithm;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.v1.IFunction;
import org.clematis.math.v1.io.OutputFormatSettings;
import org.clematis.math.v1.SimpleValue;
import org.clematis.math.v1.StringConstant;
import org.clematis.math.v1.functions.GenericFunction;
import org.clematis.math.v1.IValue;
import org.jdom2.CDATA;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;
/**
 * A representation of algorithm in question. This is
 * a formula for obtaining random variables values
 * for a question.
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
    /*
     * Parent algorithm
     */
    @Setter
    protected Algorithm parent = null;
    /*
     * Collection of algorithmic parts.
     */
    protected ArrayList<Algorithm> children = new ArrayList<Algorithm>();
    /*
     * Sets format settings for this algorithm
     */
    public void setFormatSettings(OutputFormatSettings fs) {
        super.setFormatSettings(fs);
        for (Algorithm algorithm : children) {
            algorithm.setFormatSettings(fs);
        }
    }
    /*
     * Adds parameter and sets itself as parameter container.
     *
     * @param p the parameter.
     */
    public void addParameter(Parameter p) {
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

    /*
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

    /*
     * Prints parameters names and values.
     *
     * @param ps output stream to print parameters names and values.
     */
    public void printParameters(PrintStream ps) {
        ps.println("Algorithm ( ident='" + getIdent() + "' ) parameters:");

        /* Get instance of parameter */
        for (Key key : lines) {
            /*
             * Get instance of parameter
             */
            if (!key.isFunction()) {
                /* directly get parameter */
                Parameter param = getParameter(key);
                ps.println(param.getName() + " = " + param.getOutputValue(true));
            } else {
                this.functionFactory.loadForUse(key);
            }
        }

        if (getParent() != null && !getParent().parameters.isEmpty()) {
            ps.println("Parent parameters:");
            getParent().printParameters(ps);
        }

        finishAndClear();
    }
    /*
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
    public Algorithm getAlgorithm(String key) {

        Algorithm ret = null;

        if (getIdent() != null && getIdent().equals(key)) {
            return this;
        }

        for (Algorithm algorithm : children) {
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
     * Remove algorithm
     *
     * @param key ident of algorithm
     */
    public void removeAlgorithm(String key) {
        Algorithm algorithm = getAlgorithm(key);
        if (algorithm != null) {
            algorithm.setParent(null);
            this.children.remove(algorithm);
        }
    }

    /*
     * Calculates values of all parameters participating in algorithm.
     *
     * @throws AlgorithmException on error.
     */
    public void calculateParameters() throws AlgorithmException {
        calculateParameters(null);
    }

    /*
     * Calculates values of all parameters participating in algorithm.
     *
     * @throws AlgorithmException on error.
     */
    public void calculateParameters(HashMap<Key, IValue> params) throws AlgorithmException {
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
        /*
         * Exception if no success in avoiding conditions
         */
        if (!success) {
            throw new AlgorithmException(message);
        }
        /*
         * Process children
         */
        for (Algorithm algorithm : children) {
            /*
             * Inherit calculated parameters
             */
            algorithm.calculateParameters(params);
        }
    }

    /*
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
    private boolean calculateParameters(HashMap<Key, IValue> params, boolean calculateAllParameters)
        throws ConditionException, AlgorithmException {
        boolean zero_failed = false;
        HashMap<String, Integer> randomized_params_counter = new HashMap<String, Integer>();
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
                    if (randomized_params_counter.containsKey(param.getName())) {
                        c = randomized_params_counter.get(param.getName());
                    }
                    IValue value = findKey(param.getName(), c, params);
                    randomized_params_counter.put(param.getName(), c + 1);
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
                    if (ac != null) {
                        System.out.println("Parameter " + param.getName() + " is loaded from initial values set with " +
                            ac.getValue(null));
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
                    //log.debug("Condition failed: " + param.getName() + " = " + param.getCode());
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
                        if (resV > 0 && resV < 10e-9) {
                            //log.debug("Zero failed: " + param.getName() + " = " + param.getCode() );
                            zero_failed = true;
                        }
                    }
                }
                /*
                 * Load parameter for use
                 */
                loadForUse(param);
            }
            /* function */
            else {
                /* load function for use */
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
//************************ RESTORE PARAMETERS ************************

    /*
     * Revalidate calculated parameters to restore missing parameter
     * code, but not to change results.
     * <p>
     * before: code=? after: code=sig(3, 2*$k*....
     *
     * @param alg_element question algorithm xml
     */
    public void revalidateParameters(Element alg_element) {
        if (alg_element != null) {
            /*
             * Create algorithm instance
             */
            Algorithm algorithm = Algorithm.createFromQuestionXML(alg_element);
            /*
             * Get algorithm with id of this taken algorithm
             */
            Algorithm p = algorithm.getAlgorithm(this.getIdent());
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
        for (Algorithm alg : children) {
            alg.revalidateParameters(alg_element);
        }
    }

    /*
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
//************************ CREATE FROM AND SAVE TO XML *********************

    /*
     * Creates algorithm for taken question
     *
     * @param qalg question algorithm
     * @return calculated algorithm instance
     * @throws AlgorithmException throws exception if algorithm cannot be created
     */
    static Algorithm createFromAlgorithm(IParameterProvider qalg) throws AlgorithmException {
        return createFromAlgorithm(qalg, null);
    }

    /*
     * Creates algorithm for taken question
     *
     * @param qalg   question algorithm
     * @param params some parameters values
     * @return calculated algorithm instance
     * @throws AlgorithmException throws exception if algorithm cannot be created
     */
    static Algorithm createFromAlgorithm(IParameterProvider qalg, HashMap<Key, IValue> params)
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

    /*
     * Creates algorithm from xml jdom element
     *
     * @param algorithmXML containing algorithm
     * @return algorithm object
     */
    static Algorithm createFromXML(Element algorithmXML) {

        if (algorithmXML != null && !algorithmXML.getChildren().isEmpty()) {
            Algorithm algorithm = new Algorithm();
            if (algorithmXML.getAttribute("ident") != null) {
                algorithm.setIdent(algorithmXML.getAttributeValue("ident"));
            }
            /* go through children - algorithm lines */
            List<Element> lines = algorithmXML.getChildren();
            if (lines != null) {
                for (Element line : lines) {
                    if (line.getName().equals("param")) {
                        algorithm.addParameter(Parameter.create(line));
                    } else if (line.getName().equals("function")) {
                        algorithm.addFunction(GenericFunction.create(line));
                    } else if (line.getName().equals("algorithm")) {
                        Algorithm child = Algorithm.createFromXML(line);
                        String key = line.getAttributeValue("ident");
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
        HashMap<Key, IValue> params = loadParameters(algElement);
        try {
            this.calculateParameters(params);
        } catch (AlgorithmException e) {
            //log.error( e );
        }
    }

    /*
     * Load initial values from algorithm xml
     *
     * @param algElement algorithm xml
     * @return initial values map, with algorithm idents in keys
     */
    private HashMap<Key, IValue> loadParameters(Element algElement) {
        /*  load parameters for this algorithm */
        HashMap<Key, IValue> params = new HashMap<>();
        List<Element> elements = algElement.getChildren();
        for (Object element : elements) {
            if (element instanceof Element e) {
                String name = e.getName();
                if (name.equals("param")) {
                    Parameter p = Parameter.loadResult(e);
                    /*
                     * Construct key
                     */
                    Key key = new Key(p.getName());
                    String ident = algElement.getAttributeValue("ident");
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
                    params.put(key, p.getCurrentResult().copy());
                } else if (name.equals("algorithm")) {
                    params.putAll(loadParameters(e));
                }
            }
        }
        return params;
    }

    /*
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
        for (Algorithm algorithm : children) {
            Element calc_xml = algorithm.save();
            algElement.addContent(calc_xml);
        }

        return algElement;
    }

    /*
     * Converts algorithm to JDOM.
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    public Element toXML() {
        Element algElement = new Element("algorithm");
        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
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
        for (Algorithm algorithm : children) {
            algElement.addContent(algorithm.toXML());
        }

        return algElement;
    }
//**************************** QUESTION XML OPERATIONS *****************************

    /*
     * Creates <code>Algorithm</code> object from QUESTION XML.
     *
     * @param algorithmXML XML representing the algorithm.
     */
    static Algorithm createFromQuestionXML(Element algorithmXML) {
        return createFromXML(algorithmXML);
    }

    /*
     * Saves algorithm to QUESTION XML.
     *
     * @return <code>Element</code> representing root of algorithm's JDOM.
     */
    public Element toQuestionXML() {
        Element algElement = new Element("algorithm");
        if (getIdent() != null) {
            algElement.setAttribute("ident", getIdent());
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
                Element paramElement = new Element("param");
                paramElement.setAttribute("name", param.getName());
                if (param.isContainsMathML()) {
                    paramElement.setAttribute("contains_mathml", "true");
                }

                paramElement.addContent(new CDATA(param.getCode()));

                if (param.isCorrectAnswer()) {
                    paramElement.setAttribute("answer", "true");
                }
                algElement.addContent(paramElement);
            } else {
                GenericFunction function = functionFactory.getGenericFunction(key);
                algElement.addContent(function.toXML());
            }
        }
        /*
         * Add child algorithms
         */
        for (Algorithm algorithm : children) {
            algElement.addContent(algorithm.toQuestionXML());
        }

        return algElement;
    }

    /*
     * Finds all parameters dependenced from the parameter.
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
}
