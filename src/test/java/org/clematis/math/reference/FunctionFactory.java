// Created: Jan 20, 2003 T 9:26:15 AM
package org.clematis.math.reference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.clematis.math.reference.algorithm.Key;
import org.clematis.math.reference.algorithm.iFunctionProvider;
import org.clematis.math.reference.functions.GenericFunction;
import org.clematis.math.reference.functions.aFunction;

/**
 * Factory for functions.
 */
public class FunctionFactory implements Serializable, iFunctionProvider
{
    /**
     * Standard function classes
     */
    private static HashMap<String, String> s_classes = new HashMap<String, String>();

    static {
        registerFunctions();
    }

    private static void registerFunctions() {
        s_classes.put("rand", "org.clematis.math.reference.functions.Rand");
        s_classes.put("sig", "org.clematis.math.reference.functions.Sig");
        s_classes.put("cntsig", "org.clematis.math.reference.functions.cntSig");
        s_classes.put("lsu", "org.clematis.math.reference.functions.Lsu");
        s_classes.put("abs", "org.clematis.math.reference.functions.abs");
        s_classes.put("and", "org.clematis.math.reference.functions.and");
        s_classes.put("arccos", "org.clematis.math.reference.functions.arccos");
        s_classes.put("arccosh", "org.clematis.math.reference.functions.arccosh");
        s_classes.put("arcsin", "org.clematis.math.reference.functions.arcsin");
        s_classes.put("arcsinh", "org.clematis.math.reference.functions.arcsinh");
        s_classes.put("arctan", "org.clematis.math.reference.functions.arctan");
        s_classes.put("arctanh", "org.clematis.math.reference.functions.arctanh");
        s_classes.put("cos", "org.clematis.math.reference.functions.cos");
        s_classes.put("cosh", "org.clematis.math.reference.functions.cosh");
        s_classes.put("cot", "org.clematis.math.reference.functions.cot");
        s_classes.put("csc", "org.clematis.math.reference.functions.csc");
        s_classes.put("decimal", "org.clematis.math.reference.functions.Decimal");
        s_classes.put("eq", "org.clematis.math.reference.functions.eq");
        s_classes.put("erf", "org.clematis.math.reference.functions.erf");
        s_classes.put("exp", "org.clematis.math.reference.functions.exp");
        s_classes.put("gt", "org.clematis.math.reference.functions.gt");
        s_classes.put("gti", "org.clematis.math.reference.functions.gti");
        s_classes.put("if", "org.clematis.math.reference.functions.If");
        s_classes.put("int", "org.clematis.math.reference.functions.Int");
        s_classes.put("rint", "org.clematis.math.reference.functions.rInt");
        s_classes.put("ln", "org.clematis.math.reference.functions.ln");
        s_classes.put("log", "org.clematis.math.reference.functions.log");
        s_classes.put("lt", "org.clematis.math.reference.functions.lt");
        s_classes.put("lti", "org.clematis.math.reference.functions.lti");
        s_classes.put("ne", "org.clematis.math.reference.functions.ne");
        s_classes.put("not", "org.clematis.math.reference.functions.not");
        s_classes.put("or", "org.clematis.math.reference.functions.or");
        s_classes.put("sec", "org.clematis.math.reference.functions.sec");
        s_classes.put("sin", "org.clematis.math.reference.functions.sin");
        s_classes.put("sinh", "org.clematis.math.reference.functions.sinh");
        s_classes.put("sqrt", "org.clematis.math.reference.functions.sqrt");
        s_classes.put("sum", "org.clematis.math.reference.functions.sum");
        s_classes.put("switch", "org.clematis.math.reference.functions.Switch");
        s_classes.put("tan", "org.clematis.math.reference.functions.tan");
        s_classes.put("tanh", "org.clematis.math.reference.functions.tanh");
        s_classes.put("rList", "org.clematis.math.reference.functions.rList");
    }

    /**
     * Return available library functions
     *
     * @return available library functions
     */
    public static Set<String> getAvailableFunctionsNames() {
        return s_classes.keySet();
    }

    /**
     * Extended functions codes
     * <p/>
     * key -> code
     */
    private HashMap<Key, GenericFunction> ex_functions = new HashMap<Key, GenericFunction>();
    /**
     * Extended functions codes currently in use
     * <p/>
     * name -> code
     */
    private HashMap<String, GenericFunction> ex_functions_in_use = new HashMap<String, GenericFunction>();

    /**
     * Create function, either standard or extended
     *
     * @param in_functionName function name
     * @return function, either standard or extended
     * @throws AlgorithmException is thrown is function is unknown
     */
    public aFunction getFunction(String in_functionName) throws AlgorithmException {
        aFunction f = null;
        try {
            String className = s_classes.get(in_functionName);
            if (className != null) {
                Object obj = Class.forName(className).newInstance();
                if (obj instanceof aFunction) {
                    f = (aFunction) obj;
                    f.setFunctionFactory(this);
                }
            } else {
                if (ex_functions_in_use.containsKey(in_functionName)) {
                    GenericFunction function = ex_functions_in_use.get(in_functionName);
                    f = new GenericFunction(function);
                } else {
                    f = getLatestFunction(in_functionName);
                }
            }
            if (f != null) {
                f.setSignature(in_functionName);
            }
        } catch (Exception ex) {
            f = null;
            throw new AlgorithmException("Function factory cannot create function " + in_functionName);
        }
        return f;
    }

    /**
     * Get the latest function added to collection with the same name
     *
     * @param in_functionName name of function
     * @return the latest function added to collection with the same name
     */
    public aFunction getLatestFunction(String in_functionName)
    {
        Key key = new Key(in_functionName);
        GenericFunction function = null;
        while (ex_functions.containsKey(key))
        {
            function = ex_functions.get(key);
            key.setNo(key.getNo() + 1);
        }
        if (function != null)
        {
            return new GenericFunction(function);
        }
        else
        {
            return null;
        }
    }

    /**
     * Add extended function to collection
     *
     * @param key      under which function will be stored
     * @param function - extended function
     */
    public void addFunction(Key key, GenericFunction function) {
        if (function != null) {
            ex_functions.put(key, function);
        }
    }

    /**
     * Returns true if generic function is in collection
     *
     * @param key under which function is stored
     * @return true if generic function is in collection
     */
    public boolean hasFunction(Key key) {
        return ex_functions.containsKey(key);
    }

    /**
     * Return function for this key
     *
     * @param key of function
     * @return function for this key
     */
    public GenericFunction getGenericFunction(Key key) {
        return new GenericFunction(ex_functions.get(key));
    }

    /**
     * Activate function with given key to be used
     * then function name is called
     *
     * @param key under which function is stored
     */
    public void loadForUse(Key key) {
        GenericFunction function = ex_functions.get(key);
        if (function != null) {
            ex_functions_in_use.put(key.getName(), function);
        }
    }

    /**
     * Clear functions loaded for usage
     */
    public void clear() {
        ex_functions_in_use = new HashMap<String, GenericFunction>();
    }
}
