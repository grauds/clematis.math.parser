// Created: Jan 20, 2003 T 9:26:15 AM
package org.clematis.math;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.clematis.math.algorithm.IFunctionProvider;
import org.clematis.math.algorithm.Key;
import org.clematis.math.functions.AbstractFunction;
import org.clematis.math.functions.GenericFunction;

/**
 * Factory for functions.
 */
public class FunctionFactory implements Serializable, IFunctionProvider {

    /**
     * Standard function classes
     */
    private static final Map<String, String> CLASSES = new HashMap<>();
    /**
     * Extended functions codes
     * <p/>
     * key -> code
     */
    private final Map<Key, IFunction> functions = new HashMap<>();
    /**
     * Extended functions codes currently in use
     * <p/>
     * name -> code
     */
    private final Map<String, IFunction> functionsInUse = new HashMap<>();

    /**
     * Return available library functions
     *
     * @return available library functions
     */
    public static Set<String> getAvailableFunctionsNames() {
        return CLASSES.keySet();
    }

    private static void registerFunctions() {
        CLASSES.put("rand", "org.clematis.math.functions.Rand");
        CLASSES.put("sig", "org.clematis.math.functions.Sig");
        CLASSES.put("cntsig", "org.clematis.math.functions.cntSig");
        CLASSES.put("lsu", "org.clematis.math.functions.Lsu");
        CLASSES.put("abs", "org.clematis.math.functions.abs");
        CLASSES.put("and", "org.clematis.math.functions.and");
        CLASSES.put("arccos", "org.clematis.math.functions.arccos");
        CLASSES.put("arccosh", "org.clematis.math.functions.arccosh");
        CLASSES.put("arcsin", "org.clematis.math.functions.arcsin");
        CLASSES.put("arcsinh", "org.clematis.math.functions.arcsinh");
        CLASSES.put("arctan", "org.clematis.math.functions.arctan");
        CLASSES.put("arctanh", "org.clematis.math.functions.arctanh");
        CLASSES.put("cos", "org.clematis.math.functions.cos");
        CLASSES.put("cosh", "org.clematis.math.functions.cosh");
        CLASSES.put("cot", "org.clematis.math.functions.cot");
        CLASSES.put("csc", "org.clematis.math.functions.csc");
        CLASSES.put("decimal", "org.clematis.math.functions.Decimal");
        CLASSES.put("eq", "org.clematis.math.functions.eq");
        CLASSES.put("erf", "org.clematis.math.functions.erf");
        CLASSES.put("exp", "org.clematis.math.functions.exp");
        CLASSES.put("gt", "org.clematis.math.functions.gt");
        CLASSES.put("gti", "org.clematis.math.functions.gti");
        CLASSES.put("if", "org.clematis.math.functions.If");
        CLASSES.put("int", "org.clematis.math.functions.Int");
        CLASSES.put("rint", "org.clematis.math.functions.rInt");
        CLASSES.put("ln", "org.clematis.math.functions.ln");
        CLASSES.put("log", "org.clematis.math.functions.log");
        CLASSES.put("lt", "org.clematis.math.functions.lt");
        CLASSES.put("lti", "org.clematis.math.functions.lti");
        CLASSES.put("ne", "org.clematis.math.functions.ne");
        CLASSES.put("not", "org.clematis.math.functions.not");
        CLASSES.put("or", "org.clematis.math.functions.or");
        CLASSES.put("sec", "org.clematis.math.functions.sec");
        CLASSES.put("sin", "org.clematis.math.functions.sin");
        CLASSES.put("sinh", "org.clematis.math.functions.sinh");
        CLASSES.put("sqrt", "org.clematis.math.functions.sqrt");
        CLASSES.put("sum", "org.clematis.math.functions.sum");
        CLASSES.put("switch", "org.clematis.math.functions.Switch");
        CLASSES.put("tan", "org.clematis.math.functions.tan");
        CLASSES.put("tanh", "org.clematis.math.functions.tanh");
        CLASSES.put("rList", "org.clematis.math.functions.rList");
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
    public IFunction getFunction(String functionName) throws AlgorithmException {
        IFunction f = null;
        try {
            String className = CLASSES.get(functionName);
            if (className != null) {
                Object obj = Class.forName(className).getDeclaredConstructor().newInstance();
                if (obj instanceof AbstractFunction) {
                    f = (AbstractFunction) obj;
                    ((AbstractFunction) f).setFunctionFactory(this);
                }
            } else {
                if (functionsInUse.containsKey(functionName)) {
                    IFunction function = functionsInUse.get(functionName);
                    if (function instanceof GenericFunction) {
                        f = new GenericFunction((GenericFunction) function);
                    }
                } else {
                    f = getLatestFunction(functionName);
                }
            }
            if (f != null) {
                f.setSignature(functionName);
            }
        } catch (Exception ex) {
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
    public IFunction getLatestFunction(String functionName) {
        Key key = new Key(functionName);
        IFunction function = null;
        while (functions.containsKey(key)) {
            function = functions.get(key);
            key.setNo(key.getNo() + 1);
        }
        if (function instanceof GenericFunction) {
            return new GenericFunction((GenericFunction) function);
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

    @SuppressWarnings("checkstyle:ReturnCount")
    @Override
    public boolean hasFunction(String name) {
        String className = CLASSES.get(name);
        if (className != null) {
            return true;
        } else {
            if (functionsInUse.containsKey(name)) {
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
    public GenericFunction getGenericFunction(Key key) {
        if (functions.get(key) instanceof GenericFunction) {
            return new GenericFunction((GenericFunction) functions.get(key));
        }
        return null;
    }

    /**
     * Activate function with given key to be used
     * then function name is called
     *
     * @param key under which function is stored
     */
    public void loadForUse(Key key) {
        IFunction function = functions.get(key);
        if (function != null) {
            functionsInUse.put(key.getName(), function);
        }
    }

    /**
     * Clear functions loaded for usage
     */
    public void clear() {
        functionsInUse.clear();
    }
}
