// Created: 01.12.2003 T 11:57:27
package org.clematis.math.v2.algorithm;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IValue;
import org.clematis.math.utils.StringUtils;
import org.clematis.math.io.AbstractParameterFormatter;
import org.clematis.math.v2.AbstractConstant;
import org.clematis.math.v2.FunctionFactory;
import org.clematis.math.v2.SimpleParameter;
import org.clematis.math.v2.functions.aFunction;
import org.clematis.math.v2.functions.generic;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Default parameter provider is used to provide generic
 * parameters to expression tree.
 */
public class DefaultParameterProvider extends AbstractParameterFormatter
    implements IParameterProvider, IFunctionProvider, Serializable {
    /**
     * List of ordered parameters and functions ids
     */
    protected List<Key> lines = new ArrayList<>();
    /**
     * List of parameters.
     * <p>
     * parameterId -> parameter
     */
    protected Map<Key, Parameter> parameters = new HashMap<>();
    /**
     * List of answer idents - ids of parameters
     * <p>
     * answerIdent -> parameterId
     */
    protected Map<String, Key> idents = new HashMap<>();
    /**
     * Function factory
     */
    @Setter
    @Getter
    protected FunctionFactory functionFactory = new FunctionFactory();
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
     * Parent algorithm
     */
    protected DefaultParameterProvider parent = null;
    /**
     * Collection of algorithmic parts.
     */
    @Getter
    protected List<IParameterProvider> children = new ArrayList<>();
    /**
     * List of parameters currently in use
     * <p>
     * parameternam -> parameter
     */
    private Map<String, Parameter> cache = new HashMap<>();

    /**
     * Adds parameter.
     *
     * @param p the parameter.
     */
    public void addParameter(Parameter p) throws AlgorithmException {
        if (p != null) {
            /*
             * Create normalized token without braces
             */
            Key key = Key.create(p.getName());
            /*
             * Set new name to parameter
             */
            p.setName(key.getName());
            /*
             * Find similar parameter
             */
            Key prevKey = null;
            while (parameters.get(key) != null) {
                prevKey = new Key(key);
                key.setNo(key.getNo() + 1);
            }
            /*
             * Put parameter
             */
            parameters.put(key, p);
            if (p.getCorrectAnswerIdent() != null) {
                idents.put(p.getCorrectAnswerIdent(), key);
            }
            /*
             * Add key 
             */
            addKey(key);
            /*
             * Copy declared line number from the previously found key
             */
            if (prevKey != null) {
                key.setLine(prevKey.getLine());
            }
        }
    }

    /**
     * Adds function.
     *
     * @param f the function.
     */
    public void addFunction(generic f) throws AlgorithmException {
        if (f != null) {
            Key key = new Key(f.getSignature());
            key.setFunction(true);
            /*
             * Find similar function
             */
            while (this.functionFactory.hasFunction(key)) {
                key.setNo(key.getNo() + 1);
            }
            /*
             * Put function and write its key to
             * lines sequence.
             */
            addKey(key);
            /*
             * Set a link to a function factory
             */
            f.setFunctionFactory(this.getFunctionFactory());
            /*
             * Add function
             */
            this.functionFactory.addFunction(key, f);
        }
    }

    /**
     * Returns parameter, found by its name
     *
     * @return parameter, found by its name
     */
    public Parameter getParameter(String name) {
        if (name != null) {
            Key key = Key.create(name);
            /*
             * Seek for parameter in this algorithm
             */
            Parameter param = null;
            /*
             * Get currently loaded parameter for use
             */
            if (cache.containsKey(key.getName())) {
                param = cache.get(key.getName());
            } else {
                /* get latest parameter */
                while (parameters.containsKey(key)) {
                    param = parameters.get(key);
                    key.setNo(key.getNo() + 1);
                }
            }

            return param;
        } else {
            return null;
        }
    }

    /**
     * Find parameter key in list of extra parameters
     *
     * @param name of parameter key
     * @param no   number of parameter key
     * @return value from extra list
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    protected IValue findKey(String name, int no, Map<Key, IValue> params) {

        if (params != null && name != null) {
            /*
             * Create normal key to find
             */
            Key toFind = Key.create(name);
            /*
             * Initialize key number
             */
            toFind.setNo(no);
            if (params.containsKey(toFind)) {
                return params.get(toFind);
            }
            /*
             * Alternate name
             */
            toFind.setName(SimpleParameter.alternateParameterName(toFind.getName()));
            if (params.containsKey(toFind)) {
                return params.get(toFind);
            }
            /*
             * Find key with part ident
             */
            toFind.setPartId(this.ident);
            if (params.containsKey(toFind)) {
                return params.get(toFind);
            }
            /*
             * Alternate name
             */
            toFind.setName(SimpleParameter.alternateParameterName(toFind.getName()));
            return params.get(toFind);
        }
        return null;
    }

    /**
     * Provides function.
     *
     * @param name the function name
     * @return function or null
     */
    public aFunction getFunction(String name) throws AlgorithmException {
        return this.functionFactory.getFunction(name);
    }

    /**
     * This method returns true if function provider has function with given signature
     *
     * @param name of the function
     * @return true if function provider has function with given signature
     */
    public boolean hasFunction(String name) {
        return this.functionFactory.hasFunction(name);
    }

    /**
     * Returns parameter key, found by its name
     *
     * @return parameter key, found by its name
     */
    public Key getParameterKey(String name) {
        if (name != null) {
            /*
             * Find param with key with no=0
             */
            String n;
            if (SimpleParameter.isNameWithBraces(name)) {
                n = SimpleParameter.alternateParameterName(name);
            } else {
                n = name;
            }
            Key key = new Key(n);
            while (parameters.containsKey(key)) {
                key.setNo(key.getNo() + 1);
            }
            key.setNo(key.getNo() - 1);
            Set<Key> keys = parameters.keySet();
            for (Key k : keys) {
                if (k.equals(key)) {
                    key.setLine(k.getLine());
                }
            }
            return key;
        } else {
            return null;
        }
    }

    /**
     * Finds parameter by its answer ident
     *
     * @param answerIdent answer ident
     * @return found parameter or null
     */
    public Parameter getParameterByCustomIdent(String answerIdent) {
        if (answerIdent != null && idents.get(answerIdent) != null) {
            return parameters.get(idents.get(answerIdent));
        } else {
            return null;
        }
    }

    /**
     * Returns the array of all keys in this algorithm only
     *
     * @return the array of all keys in this algorithm only
     */
    public Key[] getKeys() {
        /*
         * Get instance of parameter
         */
        Key[] ret = new Key[lines.size()];
        System.arraycopy(lines.toArray(new Key[0]), 0, ret, 0, ret.length);
        return ret;
    }

    /**
     * Get the list of all parameters in this algorithm only
     *
     * @return array of all parameters in this algorithm only
     */
    public Parameter[] getParameters() {
        ArrayList<Parameter> parametersArray = new ArrayList<>();
        for (Key key : lines) {
            /*
             * Get instance of parameter
             */
            if (!key.isFunction()) {
                Parameter param = parameters.get(key);
                parametersArray.add(param);
            }
        }
        return parametersArray.toArray(Parameter[]::new);
    }

    /**
     * Get parameter directly and activate it
     *
     * @param key of parameter
     * @return parameter
     */
    public Parameter getParameter(Key key) {
        return parameters.get(key);
    }

    /**
     * Loads parameter for usage from the pile of parameters with the same name
     *
     * @param p parameter to use
     */
    protected void loadForUse(Parameter p) {
        String name = p.getName();
        if (SimpleParameter.isNameWithBraces(p.getName())) {
            name = SimpleParameter.alternateParameterName(name);
            p.setName(name);
        }
        this.cache.put(name, p);
    }

    /**
     * Finish cyclic iteration and clear currently in
     * use functions
     */
    protected void finishAndClear() {
        cache = new HashMap<>();
        functionFactory.clear();
    }

    /**
     * Return the calculated value of algorithmic parameter
     *
     * @param name the name of required parameter
     * @return the value of required parameter
     */
    public AbstractConstant getParameterConstant(String name) {
        Parameter param = getParameter(name);
        if (param != null) {
            return param.getCurrentResult();
        }
        return null;
    }

    /**
     * Checks whether this provider has specified parameter.
     *
     * @param paramName the name of parameter.
     * @return <code>true</code> if it is a parameter name or <code>false</code> in otherwise.
     */
    public boolean hasParameter(String paramName) {
        Parameter param = getParameter(paramName);
        return param != null;
    }

    /**
     * Renames parameter
     *
     * @param paramName    of existing parameter
     * @param newParamName of new parameter
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    protected void renameParameter(String paramName, String newParamName) {
        /*
         * Sanity check
         */
        if (!paramName.equals(newParamName)) {
            /*
             * Iterate through our parameters
             */
            for (Key key : lines) {
                /* parameter */
                if (!key.isFunction()) {
                    /* directly get parameter */
                    Parameter param = getParameter(key);
                    /* replace names */
                    if (param.getName().equals(paramName)) {
                        key.setName(newParamName);
                        param.setName(newParamName);
                    }
                    /* replace occurences in code of any parameter */
                    param.setCode(StringUtils.replaceString(param.getCode(), paramName, newParamName));
                }
            }
        }
    }

    /**
     * Sets correct anser ident to parameter.
     *
     * @param p           parameter
     * @param answerIdent of parameter
     */
    public void setParameterCustomIdent(Parameter p, String answerIdent) {
        p.setCorrectAnswerIdent(answerIdent);
        idents.put(answerIdent, getParameterKey(p.getName()));
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
    }

    /**
     * Returns parameter names
     *
     * @return parameter names
     */
    public Iterator<String> iterator() {
        Set<String> nameSet = new HashSet<>(parameters.size());

        for (Key key : parameters.keySet()) {
            nameSet.add(key.getName());
        }
        return nameSet.iterator();
    }

    /**
     * Add key to this parameter provider
     *
     * @param key to add
     */
    public void addKey(Key key) {
        key.setLine(lines.size());
        lines.add(key);
    }

    /**
     * Prints parameters names and values.
     *
     * @param ps output stream to print parameters names and values.
     */
    public void printParameters(PrintStream ps) {
        ps.println("Algorithm ( ident='" + getIdent() + "' ) parameters:");

        /*
         * Get instance of parameter
         */
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

        if (parent != null && !parent.parameters.isEmpty()) {
            ps.println("Parent parameters:");
            parent.printParameters(ps);
        }

        finishAndClear();
    }

    /**
     * Returns parent algorithm
     *
     * @return parent algorithm
     */
    public IParameterProvider getParent() {
        return parent;
    }

    /**
     * Sets parent algorithm
     *
     * @param parent algorithm
     */
    public void setParent(IParameterProvider parent) {
        if (parent == null || parent instanceof DefaultParameterProvider) {
            this.parent = (DefaultParameterProvider) parent;
        }
    }

    /**
     * Add child algorithm
     *
     * @param key       for storing algorithm
     * @param algorithm child algorithm
     */
    public void addAlgorithm(String key, IParameterProvider algorithm) {
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

    /**
     * Returns algorithm, stored under given ident
     *
     * @param key ident of algorithm
     * @return algorithm, stored under given ident
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IParameterProvider getAlgorithm(String key) {
        IParameterProvider ret = null;

        if (getIdent() != null && getIdent().equals(key)) {
            return this;
        }

        for (IParameterProvider child : children) {
            ret = child.getAlgorithm(key);
            if (ret != null) {
                return ret;
            }
        }

        if (getParent() != null && getParent().getIdent() == null) {
            return this;
        } else if (getParent() == null) {
            return this;
        }

        return ret;
    }

    /**
     * Finds algorithm, stored under given key among children
     *
     * @param key of algorithm to find
     * @return algorithm, stored under given key among children
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public IParameterProvider findAlgorithm(String key) {
        IParameterProvider ret = null;

        if (getIdent() != null && getIdent().equals(key)) {
            return this;
        }

        for (IParameterProvider child : children) {
            ret = child.getAlgorithm(key);
            if (ret != null) {
                return ret;
            }
        }

        return ret;
    }

    /**
     * Remove algorithm
     *
     * @param key ident of algorithm
     */
    public void removeAlgorithm(String key) {
        IParameterProvider algorithm = getAlgorithm(key);
        if (algorithm != null) {
            algorithm.setParent(null);
            this.children.remove(algorithm);
        }
    }

    /**
     * Save parameters to XML
     *
     * @return JDOM element with parameters
     */
    @Override
    public Element save() {
        return null;
    }

    /**
     * Converts algorithm to JDOM.
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    @Override
    public Element toXML() {
        return null;
    }

    /**
     * Clear all the parameters calculation results
     */
    @Override
    public void clear() {
        this.parameters.clear();
    }
}