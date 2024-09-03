// Created: Jan 20, 2003 T 9:26:15 AM
package org.clematis.math.v2;

import org.clematis.math.v2.algorithm.Key;
import org.clematis.math.v2.algorithm.IFunctionProvider;
import org.clematis.math.v2.functions.aFunction;
import org.clematis.math.v2.functions.generic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * Factory for functions, either standard or declared in algorithm.
 */
public class FunctionFactory implements Serializable, IFunctionProvider {
    /**
     * Standard function classes
     */
    private static final HashMap<String, String> s_classes = new HashMap<String, String>(47);

    static {
        registerFunctions();
    }

    private static void registerFunctions() {
        s_classes.put("abs", "org.clematis.math.v2.functions.abs");
        s_classes.put("and", "org.clematis.math.v2.functions.and");
        s_classes.put("arccos", "org.clematis.math.v2.functions.arccos");
        s_classes.put("arccosh", "org.clematis.math.v2.functions.arccosh");
        s_classes.put("arccoth", "org.clematis.math.v2.functions.arccoth");
        s_classes.put("arccsch", "org.clematis.math.v2.functions.arccsch");
        s_classes.put("arcsech", "org.clematis.math.v2.functions.arcsech");
        s_classes.put("arcsin", "org.clematis.math.v2.functions.arcsin");
        s_classes.put("arcsinh", "org.clematis.math.v2.functions.arcsinh");
        s_classes.put("arctan", "org.clematis.math.v2.functions.arctan");
        s_classes.put("arctanh", "org.clematis.math.v2.functions.arctanh");
        s_classes.put("cntsig", "org.clematis.math.v2.functions.cntSig");
        s_classes.put("cos", "org.clematis.math.v2.functions.cos");
        s_classes.put("cosh", "org.clematis.math.v2.functions.cosh");
        s_classes.put("cot", "org.clematis.math.v2.functions.cot");
        s_classes.put("coth", "org.clematis.math.v2.functions.coth");
        s_classes.put("csc", "org.clematis.math.v2.functions.csc");
        s_classes.put("csch", "org.clematis.math.v2.functions.csch");
        s_classes.put("decimal", "org.clematis.math.v2.functions.Decimal");
        s_classes.put("eq", "org.clematis.math.v2.functions.eq");
        s_classes.put("erf", "org.clematis.math.v2.functions.erf");
        s_classes.put("exp", "org.clematis.math.v2.functions.exp");
        s_classes.put("gt", "org.clematis.math.v2.functions.gt");
        s_classes.put("gti", "org.clematis.math.v2.functions.gti");
        s_classes.put("if", "org.clematis.math.v2.functions.If");
        s_classes.put("int", "org.clematis.math.v2.functions.Int");
        s_classes.put("ln", "org.clematis.math.v2.functions.ln");
        s_classes.put("log", "org.clematis.math.v2.functions.log");
        s_classes.put("lsu", "org.clematis.math.v2.functions.Lsu");
        s_classes.put("lt", "org.clematis.math.v2.functions.lt");
        s_classes.put("lti", "org.clematis.math.v2.functions.lti");
        s_classes.put("ne", "org.clematis.math.v2.functions.ne");
        s_classes.put("not", "org.clematis.math.v2.functions.not");
        s_classes.put("or", "org.clematis.math.v2.functions.or");
        s_classes.put("rList", "org.clematis.math.v2.functions.rList");
        s_classes.put("rand", "org.clematis.math.v2.functions.Rand");
        s_classes.put("rint", "org.clematis.math.v2.functions.rInt");
        s_classes.put("sec", "org.clematis.math.v2.functions.sec");
        s_classes.put("sech", "org.clematis.math.v2.functions.sech");
        s_classes.put("sig", "org.clematis.math.v2.functions.Sig");
        s_classes.put("sin", "org.clematis.math.v2.functions.sin");
        s_classes.put("sinh", "org.clematis.math.v2.functions.sinh");
        s_classes.put("sqrt", "org.clematis.math.v2.functions.sqrt");
        s_classes.put("sum", "org.clematis.math.v2.functions.sum");
        s_classes.put("switch", "org.clematis.math.v2.functions.Switch");
        s_classes.put("tan", "org.clematis.math.v2.functions.tan");
        s_classes.put("tanh", "org.clematis.math.v2.functions.tanh");
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
    private final HashMap<Key, generic> ex_functions = new HashMap<Key, generic>();
    /**
     * Extended functions codes currently in use
     * <p/>
     * token -> code
     */
    private HashMap<String, generic> ex_functions_in_use = new HashMap<String, generic>();

    /**
     * Create library static function
     *
     * @param in_functionName
     * @return function
     * @throws AlgorithmException
     */
    public static aFunction createFunction(String in_functionName) throws AlgorithmException {
        aFunction f = null;
        try {
            String className = s_classes.get(in_functionName);
            if (className != null) {
                Object obj = Class.forName(className).newInstance();
                if (obj instanceof aFunction) {
                    f = (aFunction) obj;
                    if (f != null) {
                        f.setSignature(in_functionName);
                    }
                }
            }
        } catch (Exception e) {
            throw new AlgorithmException("Function factory cannot create function " + in_functionName);
        }
        return f;
    }

    /**
     * Create function, either standard or extended
     *
     * @param in_functionName function token
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
                    generic function = ex_functions_in_use.get(in_functionName);
                    f = new generic(function);
                } else {
                    f = getLatestFunction(in_functionName);
                }
            }
            if (f != null) {
                f.setSignature(in_functionName);
            }
        } catch (Exception ex) {
            f = null;
            throw new AlgorithmException(
                "Function factory cannot create function " + in_functionName + ": " + ex.getMessage());
        }
        return f;
    }

    /**
     * Get the latest function added to collection with the same token
     *
     * @param in_functionName token of function
     * @return the latest function added to collection with the same token
     */
    public aFunction getLatestFunction(String in_functionName) {
        Key key = new Key(in_functionName);
        key.setFunction(true);

        generic function = null;
        while (ex_functions.containsKey(key)) {
            function = ex_functions.get(key);
            key.setNo(key.getNo() + 1);
        }
        if (function != null) {
            return new generic(function);
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
    public void addFunction(Key key, generic function) {
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
     * This method returns true if function provider has function with given signature
     *
     * @param name of the function
     * @return true if function provider has function with given signature
     */
    public boolean hasFunction(String name) {
        String className = s_classes.get(name);
        if (className != null) {
            return true;
        } else {
            if (ex_functions_in_use.containsKey(name)) {
                return true;
            } else {
                return getLatestFunction(name) != null;
            }
        }
    }

    /**
     * Return function for this key
     *
     * @param key of function
     * @return function for this key
     */
    public generic getGenericFunction(Key key) {
        return new generic(ex_functions.get(key));
    }

    /**
     * Activate function with given key to be used
     * then function token is called
     *
     * @param key under which function is stored
     */
    public void loadForUse(Key key) {
        generic function = ex_functions.get(key);
        if (function != null) {
            ex_functions_in_use.put(key.getName(), function);
        }
    }

    /**
     * Clear functions loaded for usage
     */
    public void clear() {
        ex_functions_in_use = new HashMap<String, generic>();
    }
}
