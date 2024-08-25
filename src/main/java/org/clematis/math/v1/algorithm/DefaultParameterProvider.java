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

import lombok.Getter;
import lombok.Setter;

/**
 * Default parameter provider is used to provide generic
 * parameters to expression tree.
 */
public class DefaultParameterProvider extends AbstractParameterFormatter
    implements IFunctionProvider, Serializable {
    /**
     * List of ordered parameters and functions ids
     */
    protected ArrayList<Key> lines = new ArrayList<>();
    /**
     * List of parameters.
     * <p>
     * parameterId -> parameter
     */
    protected HashMap<Key, Parameter> parameters = new HashMap<>();
    /**
     * List of answer idents - ids of parameters
     * <p>
     * answerIdent -> parameterId
     */
    protected HashMap<String, Key> idents = new HashMap<>();
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
     * List of parameters currently in use
     * <p>
     * parameternam -> parameter
     */
    private HashMap<String, Parameter> cache = new HashMap<>();

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
            Key key = createKey(name);
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
     * Create key to find in parameter provider. This method
     * alternates names with braces like ${a} to $a
     *
     * @param name of desired parameter
     * @return key to find
     */
    protected Key createKey(String name) {
        /* normalize parameter name */
        if (SimpleParameter.isNameWithBraces(name)) {
            return new Key(SimpleParameter.alternateParameterName(name));
        } else {
            return new Key(name);
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
     * Get parameter
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
    public AbstractConstant getParameterConstant(String name) throws AlgorithmException {
        Parameter param = getParameter(name);
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