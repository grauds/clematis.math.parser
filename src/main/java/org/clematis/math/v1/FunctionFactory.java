// Created: Jan 20, 2003 T 9:26:15 AM
package org.clematis.math.v1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.clematis.math.v1.algorithm.Key;
import org.clematis.math.v1.algorithm.iFunctionProvider;
import org.clematis.math.v1.functions.GenericFunction;
import org.clematis.math.v1.functions.aFunction;

/**
 * Factory for functions.
 */
public class FunctionFactory implements Serializable, iFunctionProvider {
    /**
     * Standard function classes
     */
    private static final HashMap<String, String> s_classes = new HashMap<String, String>();

    static {
        registerFunctions();
    }

    private static void registerFunctions() {
        s_classes.put("rand", "org.clematis.math.v1.functions.reference.Rand");
        s_classes.put("sig", "org.clematis.math.v1.functions.reference.Sig");
        s_classes.put("cntsig", "org.clematis.math.v1.functions.reference.cntSig");
        s_classes.put("lsu", "org.clematis.math.v1.functions.reference.Lsu");
        s_classes.put("abs", "org.clematis.math.v1.functions.reference.abs");
        s_classes.put("and", "org.clematis.math.v1.functions.reference.and");
        s_classes.put("arccos", "org.clematis.math.v1.functions.reference.arccos");
        s_classes.put("arccosh", "org.clematis.math.v1.functions.reference.arccosh");
        s_classes.put("arcsin", "org.clematis.math.v1.functions.reference.arcsin");
        s_classes.put("arcsinh", "org.clematis.math.v1.functions.reference.arcsinh");
        s_classes.put("arctan", "org.clematis.math.v1.functions.reference.arctan");
        s_classes.put("arctanh", "org.clematis.math.v1.functions.reference.arctanh");
        s_classes.put("cos", "org.clematis.math.v1.functions.reference.cos");
        s_classes.put("cosh", "org.clematis.math.v1.functions.reference.cosh");
        s_classes.put("cot", "org.clematis.math.v1.functions.reference.cot");
        s_classes.put("csc", "org.clematis.math.v1.functions.reference.csc");
        s_classes.put("decimal", "org.clematis.math.v1.functions.reference.Decimal");
        s_classes.put("eq", "org.clematis.math.v1.functions.reference.eq");
        s_classes.put("erf", "org.clematis.math.v1.functions.reference.erf");
        s_classes.put("exp", "org.clematis.math.v1.functions.reference.exp");
        s_classes.put("gt", "org.clematis.math.v1.functions.reference.gt");
        s_classes.put("gti", "org.clematis.math.v1.functions.reference.gti");
        s_classes.put("if", "org.clematis.math.v1.functions.reference.If");
        s_classes.put("int", "org.clematis.math.v1.functions.reference.Int");
        s_classes.put("rint", "org.clematis.math.v1.functions.reference.rInt");
        s_classes.put("ln", "org.clematis.math.v1.functions.reference.ln");
        s_classes.put("log", "org.clematis.math.v1.functions.reference.log");
        s_classes.put("lt", "org.clematis.math.v1.functions.reference.lt");
        s_classes.put("lti", "org.clematis.math.v1.functions.reference.lti");
        s_classes.put("ne", "org.clematis.math.v1.functions.reference.ne");
        s_classes.put("not", "org.clematis.math.v1.functions.reference.not");
        s_classes.put("or", "org.clematis.math.v1.functions.reference.or");
        s_classes.put("sec", "org.clematis.math.v1.functions.reference.sec");
        s_classes.put("sin", "org.clematis.math.v1.functions.reference.sin");
        s_classes.put("sinh", "org.clematis.math.v1.functions.reference.sinh");
        s_classes.put("sqrt", "org.clematis.math.v1.functions.reference.sqrt");
        s_classes.put("sum", "org.clematis.math.v1.functions.reference.sum");
        s_classes.put("switch", "org.clematis.math.v1.functions.reference.Switch");
        s_classes.put("tan", "org.clematis.math.v1.functions.reference.tan");
        s_classes.put("tanh", "org.clematis.math.v1.functions.reference.tanh");
        s_classes.put("rList", "org.clematis.math.v1.functions.reference.rList");
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
    private final HashMap<Key, GenericFunction> ex_functions = new HashMap<Key, GenericFunction>();
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
    public aFunction getLatestFunction(String in_functionName) {
        Key key = new Key(in_functionName);
        GenericFunction function = null;
        while (ex_functions.containsKey(key)) {
            function = ex_functions.get(key);
            key.setNo(key.getNo() + 1);
        }
        if (function != null) {
            return new GenericFunction(function);
        } else {
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
