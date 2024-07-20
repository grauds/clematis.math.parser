// Created: 12.05.2004 T 17:36:51
package org.clematis.math.v1.functions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.ExpressionParser;
import org.clematis.math.v1.FunctionFactory;
import org.clematis.math.v1.algorithm.DefaultParameterProvider;
import org.clematis.math.v1.algorithm.Key;
import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.algorithm.iParameterProvider;
import org.clematis.math.v1.iExpressionItem;
import org.jdom2.CDATA;
import org.jdom2.Element;

/**
 * Generic function can be created on-the-fly then parsing
 * algorithm code.
 */
public class GenericFunction extends aFunction {
    /**
     * Special name of generic function
     */
    public final static String GENERIC_FUNCTION_NAME = "function";
    /**
     * Code of this function
     */
    private String code = "";
    /**
     * Arguments provider
     */
    private final ArgumentsProvider argumentsProvider = new ArgumentsProvider();

    /**
     * Provides generic function with actual
     * arguments instead of formal ones.
     */
    class ArgumentsProvider extends DefaultParameterProvider {
        public ArrayList<Key> getLines() {
            return this.lines;
        }

        public void setLines(ArrayList<Key> lines) {
            this.lines = new ArrayList<Key>();
            this.lines.addAll(lines);
        }

        /**
         * Return parameter constant
         *
         * @param in_varName parameter name
         * @return parameter value, string or double
         */
        public AbstractConstant getParameterConstant(String in_varName) throws AlgorithmException {
            /** in_varName ignored */
            AbstractConstant result = null;
            if ((result = super.getParameterConstant(in_varName)) != null) {
                return result;
            } else {
                /** get index of name in formal parameters and get argument by this index */
                int index = lines.indexOf(new Key(in_varName));
                if ((index >= 0) && (index < arguments.size())) {
                    iExpressionItem argument = arguments.get(index);
                    argument = argument.calculate();
                    if (argument instanceof AbstractConstant) {
                        /** join constant and its name */
                        addParameter(new Parameter(in_varName, (AbstractConstant) argument));
                        result = (AbstractConstant) argument;
                    }
                }
            }
            return result;
        }
    }

    /**
     * Copy constructor
     *
     * @param genericFunction
     */
    public GenericFunction(GenericFunction genericFunction) {
        super();
        this.setSignature(genericFunction.getSignature());
        this.setCode(genericFunction.getCode());
        this.setFormalArguments(genericFunction.getFormalArguments());
        this.setFunctionFactory(genericFunction.getFunctionFactory());
    }

    /**
     * Constructor
     */
    private GenericFunction(String name, String code) {
        this.setSignature(name);
        this.code = code;
    }

    /**
     * Add formal argument to this generic function
     *
     * @param name of formal argument
     */
    public void addFormalArgument(String name) {
        //this.getFormalArguments().add( new Key( name ) );
        argumentsProvider.addKey(new Key(name));
    }

    public ArrayList<Key> getFormalArguments() {
        return argumentsProvider.getLines();
    }

    public void setFormalArguments(ArrayList<Key> lines) {
        this.argumentsProvider.setLines(lines);
    }

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException {
        return calculate(null);
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter and functions provider
     * @return expression item instance
     */
    public iExpressionItem calculate(iParameterProvider parameterProvider)
        throws AlgorithmException {
        /** parse code of this generic function */
        ExpressionParser parser = new ExpressionParser(code, argumentsProvider, null, argumentsProvider);
        iExpressionItem root = parser.parse();
        /** calculate generic function */
        if (root != null) {
            root = root.calculate(parameterProvider);
        }
        return root;
    }

    /**
     * Sets a link to external function factory
     *
     * @param ff external function factory
     */
    public void setFunctionFactory(FunctionFactory ff) {
        this.argumentsProvider.setFunctionFactory(ff);
    }

    /**
     * Returns a link to its function factory
     */
    public FunctionFactory getFunctionFactory() {
        return this.argumentsProvider.getFunctionFactory();
    }

    /**
     * Get generic function code
     *
     * @return generic function code
     */
    public String getCode() {
        return code;
    }

    /**
     * Set generic function code
     *
     * @param code generic function code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Create function from declaration
     *
     * @param declaration of generic function
     * @return instance of generic function
     * @throws AlgorithmException
     */
    public static GenericFunction create(String declaration) throws AlgorithmException {
        String _statement = declaration.substring(GENERIC_FUNCTION_NAME.length() + 1);
        /** position of = sign */
        int pos = _statement.indexOf('=');
        if (pos == -1) {
            throw new AlgorithmException("Operand '=' is missed: " + declaration);
        }
        /** signature of the function */
        String signature = _statement.substring(0, pos).trim();
        if (signature.length() < 1) {
            throw new AlgorithmException("Function signature is empty");
        }
        int beginBracesIndex = signature.indexOf('(');
        int endBracesIndex = signature.indexOf(')');
        /** name of the function */
        String name = signature.substring(0, beginBracesIndex);
        /** code of function */
        String code = _statement.substring(pos + 1).trim();
        /** create generic function */
        GenericFunction function = new GenericFunction(name, code);
        /** formal parameters */
        String parameters = signature.substring(beginBracesIndex + 1, endBracesIndex);
        StringTokenizer st = new StringTokenizer(parameters, ",");
        while (st.hasMoreTokens()) {
            function.addFormalArgument(st.nextToken().trim());
        }
        return function;
    }

    /**
     * Create parameter from XML element
     *
     * @param xml representation of this parameter
     * @return parameter
     */
    public static GenericFunction create(Element xml) {
        /**
         * Create generic function
         */
        if (xml.getAttribute("signature") != null) {
            String signature = xml.getAttributeValue("signature");
            String code = xml.getText();
            GenericFunction function = new GenericFunction(signature, code);
            Iterator arguments = xml.getChildren("argument").iterator();
            while (arguments.hasNext()) {
                Element argument = (Element) arguments.next();
                function.addFormalArgument(argument.getText());
            }
            return function;
        }
        return null;
    }

    /**
     * Serialize parameter to XML element
     *
     * @return xml representation of this parameter
     */
    public Element toXML() {
        Element paramElement = new Element("function");
        if (getSignature() != null) {
            paramElement.setAttribute("signature", getSignature());
        }
        for (int i = 0; i < getFormalArguments().size(); i++) {
            Element arg = new Element("argument");
            arg.setText(getFormalArguments().get(i).getName());
            paramElement.addContent(arg);
        }
        if (getCode() != null) {
            paramElement.addContent(new CDATA(getCode()));
        }
        return paramElement;
    }
}
