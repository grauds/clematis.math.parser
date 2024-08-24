// Created: 01.12.2003 T 11:57:27
package org.clematis.math.v1.algorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.FunctionFactory;
import org.clematis.math.v1.functions.GenericFunction;
import org.clematis.math.v1.functions.aFunction;
import org.clematis.math.v1.iValue;
import org.jdom2.Element;

/**
 * Default parameter provider is used to provide generic
 * parameters to expression tree.
 */
public class DefaultParameterProvider extends AbstractParameterFormatter
    implements iFunctionProvider, Serializable {
    /**
     * List of ordered parameters and functions ids
     * <p>
     * parameterId
     * functionIds
     */
    protected ArrayList<Key> lines = new ArrayList<>();
    /**
     * List of parameters.
     * <p>
     * parameterId -> parameter
     */
    protected HashMap<Key, Parameter> parameters = new HashMap<>();
    /**
     * List of parameters currently in use
     * <p>
     * parameternam -> parameter
     */
    private HashMap<String, Parameter> parameters_in_use = new HashMap<>();
    /**
     * List of answer idents - ids of parameters
     * <p>
     * answerIdent -> parameterId
     */
    protected HashMap<String, Key> idents = new HashMap<>();
    /**
     * Function factory
     */
    protected FunctionFactory functionFactory = new FunctionFactory();
    /**
     * Ident
     */
    protected String ident = null;
    /**
     * Version of originating persistent storage
     */
    protected String version = "1";

    /**
     * Adds parameter.
     *
     * @param p the parameter.
     */
    public void addParameter(Parameter p) {
        if (p != null) {
            /*
             * Create normal name $a without braces
             */
            Key key = createKey(p.getName());
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
    public void addFunction(GenericFunction f) {
        if (f != null) {
            Key key = new Key(f.getSignature());
            key.setFunction(true);
            /**
             * Find similar function
             */
            while (this.functionFactory.hasFunction(key)) {
                key.setNo(key.getNo() + 1);
            }
            /**
             * Put function and write its key to
             * lines sequence.
             */
            addKey(key);
            /**
             * Set a link to a function factory
             */
            f.setFunctionFactory(this.getFunctionFactory());
            /**
             * Add function
             */
            this.functionFactory.addFunction(key, f);
        }
    }

    public FunctionFactory getFunctionFactory() {
        return functionFactory;
    }

    public void setFunctionFactory(FunctionFactory functionFactory) {
        this.functionFactory = functionFactory;
    }

    /**
     * Returns parameter, found by its name
     *
     * @return parameter, found by its name
     */
    public Parameter getParameter(String name) {
        if (name != null) {
            Key key = createKey(name);
            /*
             * Seek for parameter in this algorithm
             */
            Parameter param = null;
            /*
             * Get currently loaded parameter for use
             */
            if (parameters_in_use.containsKey(key.getName())) {
                return parameters_in_use.get(key.getName());
            }
            /* get latest parameter */
            else {
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
     * Create key to find in parameter provider. This method
     * alternates names with braces like ${a} to $a
     *
     * @param name of desired parameter
     * @return key to find
     */
    protected Key createKey(String name) {
        /* normalize parameter name */
        if (SimpleParameter.isNameWithBraces(name)) {
            name = SimpleParameter.alternateParameterName(name);
        }
        return new Key(name);
    }

    /**
     * Find parameter key in list of extra parameters
     *
     * @param name of parameter key
     * @param no   number of parameter key
     * @return value from extra list
     */
    protected iValue findKey(String name, int no, HashMap<Key, iValue> params) {

        if (params != null && name != null) {
            /*
             * Create normal key to find
             */
            Key toFind = createKey(name);
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
     * Returns parameter key, found by its name
     *
     * @return parameter key, found by its name
     */
    public Key getParameterKey(String name) {
        if (name != null) {
            /*
             * Find param with key with no=0
             */
            if (SimpleParameter.isNameWithBraces(name)) {
                name = SimpleParameter.alternateParameterName(name);
            }
            Key key = new Key(name);
            while (parameters.containsKey(key)) {
                key.setNo(key.getNo() + 1);
            }
            key.setNo(key.getNo() - 1);
            Set<Key> keys = parameters.keySet();
            Iterator<Key> iter = keys.iterator();
            while (iter.hasNext()) {
                Key k = iter.next();
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
     * Returns the list of all keys in this algorithm only
     *
     * @return the list of all keys in this algorithm only
     */
    public Key[] getKeys() {
        ArrayList keysArray = new ArrayList();
        Iterator<Key> names = lines.iterator();
        while (names.hasNext()) {
            /**
             * Get instance of parameter
             */
            keysArray.add(names.next());
        }
        Key[] ret = new Key[keysArray.size()];
        System.arraycopy(keysArray.toArray(), 0, ret, 0, ret.length);
        return ret;
    }

    /**
     * Get the list of all parameters in this algorithm only
     *
     * @return list of all parameters in this algorithm only
     */
    public Parameter[] getParameters() {
        ArrayList parametersArray = new ArrayList();
        Iterator<Key> names = lines.iterator();
        while (names.hasNext()) {
            /**
             * Get instance of parameter
             */
            Key key = names.next();
            if (!key.isFunction()) {
                Parameter param = parameters.get(key);
                parametersArray.add(param);
            }
        }
        Parameter[] ret = new Parameter[parametersArray.size()];
        System.arraycopy(parametersArray.toArray(), 0, ret, 0, ret.length);
        return ret;
    }

    /**
     * Get parameter directly and activate it
     *
     * @param key of parameter
     * @return parameter
     */
    public Parameter getParameter(Key key) {
        /**
         * Get parameter for the current line
         */
        return parameters.get(key);
    }

    /**
     * Loads parameter for usage from the pile of
     * parameters with the same name
     *
     * @param p parameter to use
     */
    protected void loadForUse(Parameter p) {
        String name = p.getName();
        if (SimpleParameter.isNameWithBraces(p.getName())) {
            name = SimpleParameter.alternateParameterName(name);
            p.setName(name);
        }
        this.parameters_in_use.put(name, p);
    }

    /**
     * Finish cyclic iteration and clear currently in
     * use functions
     */
    protected void finishAndClear() {
        parameters_in_use = new HashMap<String, Parameter>();
        functionFactory.clear();
    }

    /**
     * Return the calculated value of algorithmic parameter
     *
     * @param paramName the name of required parameter
     * @return the value of required parameter
     */
    public AbstractConstant getParameterConstant(String paramName) throws AlgorithmException {
        Parameter param = getParameter(paramName);
        if (param != null) {
            return param.getCurrentResult();
        }
        return null;
    }

    /**
     * Checks whether the string is a real parameter name.
     *
     * @param paramName the name of parameter.
     * @return <code>true</code> if it is a parameter name or <code>false</code> in otherwise.
     */
    protected boolean hasParameter(String paramName) {
        Parameter param = getParameter(paramName);
        return param != null;
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
     * Returns algorithm ident
     *
     * @return algorithm ident
     */
    public String getIdent() {
        return ident;
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
     * Calculates values of all parameters participating in algorithm.
     *
     * @throws AlgorithmException on error.
     */
    public void calculateParameters() throws AlgorithmException {
    }

    /**
     * Calculates values of all parameters participating in algorithm.
     *
     * @throws AlgorithmException on error.
     */
    public void calculateParameters(HashMap<Key, iValue> params) throws AlgorithmException {
    }

    /**
     * Returns parameter names
     *
     * @return parameter names
     */
    public Iterator<String> iterator() {
        Set<String> nameSet = new HashSet<String>(parameters.size());

        for (Key key : parameters.keySet()) {
            nameSet.add(key.getName());
        }
        return nameSet.iterator();
    }

    public void addKey(Key key) {
        key.setLine(lines.size());
        lines.add(key);
    }

    @Override
    public Element save() {
        return null;
    }

    @Override
    public Element toXML() {
        return null;
    }
}