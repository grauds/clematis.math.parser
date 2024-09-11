// Created: Jan 20, 2003 T 9:26:15 AM
package org.clematis.math.v1;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.algorithm.IFunctionProvider;
import org.clematis.math.v1.algorithm.Key;
import org.clematis.math.v1.functions.AbstractFunction;
import org.clematis.math.v1.functions.GenericFunction;

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
        CLASSES.put("rand", "org.clematis.math.v1.functions.Rand");
        CLASSES.put("sig", "org.clematis.math.v1.functions.Sig");
        CLASSES.put("cntsig", "org.clematis.math.v1.functions.cntSig");
        CLASSES.put("lsu", "org.clematis.math.v1.functions.Lsu");
        CLASSES.put("abs", "org.clematis.math.v1.functions.abs");
        CLASSES.put("and", "org.clematis.math.v1.functions.and");
        CLASSES.put("arccos", "org.clematis.math.v1.functions.arccos");
        CLASSES.put("arccosh", "org.clematis.math.v1.functions.arccosh");
        CLASSES.put("arcsin", "org.clematis.math.v1.functions.arcsin");
        CLASSES.put("arcsinh", "org.clematis.math.v1.functions.arcsinh");
        CLASSES.put("arctan", "org.clematis.math.v1.functions.arctan");
        CLASSES.put("arctanh", "org.clematis.math.v1.functions.arctanh");
        CLASSES.put("cos", "org.clematis.math.v1.functions.cos");
        CLASSES.put("cosh", "org.clematis.math.v1.functions.cosh");
        CLASSES.put("cot", "org.clematis.math.v1.functions.cot");
        CLASSES.put("csc", "org.clematis.math.v1.functions.csc");
        CLASSES.put("decimal", "org.clematis.math.v1.functions.Decimal");
        CLASSES.put("eq", "org.clematis.math.v1.functions.eq");
        CLASSES.put("erf", "org.clematis.math.v1.functions.erf");
        CLASSES.put("exp", "org.clematis.math.v1.functions.exp");
        CLASSES.put("gt", "org.clematis.math.v1.functions.gt");
        CLASSES.put("gti", "org.clematis.math.v1.functions.gti");
        CLASSES.put("if", "org.clematis.math.v1.functions.If");
        CLASSES.put("int", "org.clematis.math.v1.functions.Int");
        CLASSES.put("rint", "org.clematis.math.v1.functions.rInt");
        CLASSES.put("ln", "org.clematis.math.v1.functions.ln");
        CLASSES.put("log", "org.clematis.math.v1.functions.log");
        CLASSES.put("lt", "org.clematis.math.v1.functions.lt");
        CLASSES.put("lti", "org.clematis.math.v1.functions.lti");
        CLASSES.put("ne", "org.clematis.math.v1.functions.ne");
        CLASSES.put("not", "org.clematis.math.v1.functions.not");
        CLASSES.put("or", "org.clematis.math.v1.functions.or");
        CLASSES.put("sec", "org.clematis.math.v1.functions.sec");
        CLASSES.put("sin", "org.clematis.math.v1.functions.sin");
        CLASSES.put("sinh", "org.clematis.math.v1.functions.sinh");
        CLASSES.put("sqrt", "org.clematis.math.v1.functions.sqrt");
        CLASSES.put("sum", "org.clematis.math.v1.functions.sum");
        CLASSES.put("switch", "org.clematis.math.v1.functions.Switch");
        CLASSES.put("tan", "org.clematis.math.v1.functions.tan");
        CLASSES.put("tanh", "org.clematis.math.v1.functions.tanh");
        CLASSES.put("rList", "org.clematis.math.v1.functions.rList");
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
