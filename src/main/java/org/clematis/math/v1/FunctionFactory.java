// Created: Jan 20, 2003 T 9:26:15 AM
package org.clematis.math.v1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

import org.clematis.math.v1.algorithm.IFunctionProvider;
import org.clematis.math.v1.algorithm.Key;
import org.clematis.math.v1.functions.GenericFunction;
import org.clematis.math.v1.functions.aFunction;

/**
 * Factory for functions.
 */
public class FunctionFactory implements Serializable, IFunctionProvider {

    /**
     * Standard function classes
     */
    private static final HashMap<String, String> CLASSES = new HashMap<>();
    /**
     * Extended functions codes
     * <p/>
     * key -> code
     */
    private final HashMap<Key, GenericFunction> functions = new HashMap<>();
    /**
     * Extended functions codes currently in use
     * <p/>
     * name -> code
     */
    private HashMap<String, GenericFunction> functionsInUse = new HashMap<>();

    /**
     * Return available library functions
     *
     * @return available library functions
     */
    public static Set<String> getAvailableFunctionsNames() {
        return CLASSES.keySet();
    }

    private static void registerFunctions() {
        CLASSES.put("rand", "org.clematis.math.v1.functions.reference.Rand");
        CLASSES.put("sig", "org.clematis.math.v1.functions.reference.Sig");
        CLASSES.put("cntsig", "org.clematis.math.v1.functions.reference.cntSig");
        CLASSES.put("lsu", "org.clematis.math.v1.functions.reference.Lsu");
        CLASSES.put("abs", "org.clematis.math.v1.functions.reference.abs");
        CLASSES.put("and", "org.clematis.math.v1.functions.reference.and");
        CLASSES.put("arccos", "org.clematis.math.v1.functions.reference.arccos");
        CLASSES.put("arccosh", "org.clematis.math.v1.functions.reference.arccosh");
        CLASSES.put("arcsin", "org.clematis.math.v1.functions.reference.arcsin");
        CLASSES.put("arcsinh", "org.clematis.math.v1.functions.reference.arcsinh");
        CLASSES.put("arctan", "org.clematis.math.v1.functions.reference.arctan");
        CLASSES.put("arctanh", "org.clematis.math.v1.functions.reference.arctanh");
        CLASSES.put("cos", "org.clematis.math.v1.functions.reference.cos");
        CLASSES.put("cosh", "org.clematis.math.v1.functions.reference.cosh");
        CLASSES.put("cot", "org.clematis.math.v1.functions.reference.cot");
        CLASSES.put("csc", "org.clematis.math.v1.functions.reference.csc");
        CLASSES.put("decimal", "org.clematis.math.v1.functions.reference.Decimal");
        CLASSES.put("eq", "org.clematis.math.v1.functions.reference.eq");
        CLASSES.put("erf", "org.clematis.math.v1.functions.reference.erf");
        CLASSES.put("exp", "org.clematis.math.v1.functions.reference.exp");
        CLASSES.put("gt", "org.clematis.math.v1.functions.reference.gt");
        CLASSES.put("gti", "org.clematis.math.v1.functions.reference.gti");
        CLASSES.put("if", "org.clematis.math.v1.functions.reference.If");
        CLASSES.put("int", "org.clematis.math.v1.functions.reference.Int");
        CLASSES.put("rint", "org.clematis.math.v1.functions.reference.rInt");
        CLASSES.put("ln", "org.clematis.math.v1.functions.reference.ln");
        CLASSES.put("log", "org.clematis.math.v1.functions.reference.log");
        CLASSES.put("lt", "org.clematis.math.v1.functions.reference.lt");
        CLASSES.put("lti", "org.clematis.math.v1.functions.reference.lti");
        CLASSES.put("ne", "org.clematis.math.v1.functions.reference.ne");
        CLASSES.put("not", "org.clematis.math.v1.functions.reference.not");
        CLASSES.put("or", "org.clematis.math.v1.functions.reference.or");
        CLASSES.put("sec", "org.clematis.math.v1.functions.reference.sec");
        CLASSES.put("sin", "org.clematis.math.v1.functions.reference.sin");
        CLASSES.put("sinh", "org.clematis.math.v1.functions.reference.sinh");
        CLASSES.put("sqrt", "org.clematis.math.v1.functions.reference.sqrt");
        CLASSES.put("sum", "org.clematis.math.v1.functions.reference.sum");
        CLASSES.put("switch", "org.clematis.math.v1.functions.reference.Switch");
        CLASSES.put("tan", "org.clematis.math.v1.functions.reference.tan");
        CLASSES.put("tanh", "org.clematis.math.v1.functions.reference.tanh");
        CLASSES.put("rList", "org.clematis.math.v1.functions.reference.rList");
    }

    static {
        registerFunctions();
    }
    /**
     * Create function, either standard or extended
     *
     * @param functionName function name
     * @return function, either standard or extended
     * @throws AlgorithmException is thrown is function is unknown
     */
    public aFunction getFunction(String functionName) throws AlgorithmException {
        aFunction f = null;
        try {
            String className = CLASSES.get(functionName);
            if (className != null) {
                Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
                if (obj instanceof aFunction) {
                    f = (aFunction) obj;
                    f.setFunctionFactory(this);
                }
            } else {
                if (functionsInUse.containsKey(functionName)) {
                    GenericFunction function = functionsInUse.get(functionName);
                    f = new GenericFunction(function);
                } else {
                    f = getLatestFunction(functionName);
                }
            }
            if (f != null) {
                f.setSignature(functionName);
            }
        } catch (Exception ex) {
            f = null;
            throw new AlgorithmException("Function factory cannot create function " + functionName);
        }
        return f;
    }

    /**
     * Get the latest function added to collection with the same name
     *
     * @param functionName name of function
     * @return the latest function added to collection with the same name
     */
    public aFunction getLatestFunction(String functionName) {
        Key key = new Key(functionName);
        GenericFunction function = null;
        while (functions.containsKey(key)) {
            function = functions.get(key);
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
            functions.put(key, function);
        }
    }

    /**
     * Returns true if generic function is in collection
     *
     * @param key under which function is stored
     * @return true if generic function is in collection
     */
    public boolean hasFunction(Key key) {
        return functions.containsKey(key);
    }

    /**
     * Return function for this key
     *
     * @param key of function
     * @return function for this key
     */
    public GenericFunction getGenericFunction(Key key) {
        return new GenericFunction(functions.get(key));
    }

    /**
     * Activate function with given key to be used
     * then function name is called
     *
     * @param key under which function is stored
     */
    public void loadForUse(Key key) {
        GenericFunction function = functions.get(key);
        if (function != null) {
            functionsInUse.put(key.getName(), function);
        }
    }

    /**
     * Clear functions loaded for usage
     */
    public void clear() {
        functionsInUse = new HashMap<String, GenericFunction>();
    }
}
