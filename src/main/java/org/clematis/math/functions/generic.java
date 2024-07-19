// Created: 12.05.2004 T 17:36:51
package org.clematis.math.functions;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.clematis.math.AbstractConstant;
import org.clematis.math.Constant;
import org.clematis.math.FunctionFactory;
import org.clematis.math.StringConstant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.DefaultParameterProvider;
import org.clematis.math.algorithm.Parameter;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.parsers.Node;
import org.clematis.math.parsers.string.ParseException;
import org.clematis.math.parsers.string.StringMathParser;
import org.clematis.math.utils.AlgorithmUtils;
import org.jdom2.CDATA;
import org.jdom2.Element;

/**
 * Generic function can be created within the algorithm code.
 */
public class generic extends aFunction {
    /**
     * Special token of generic function
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
     * Formal arguments
     */
    private ArrayList<String> formalArguments = new ArrayList<String>();

    /**
     * Provides generic function with actual
     * arguments instead of formal ones.
     */
    class ArgumentsProvider extends DefaultParameterProvider {
        /**
         * Return parameter constant while calculating
         *
         * @param in_varName parameter token
         * @return parameter value, string or double
         */
        public AbstractConstant getParameterConstant(String in_varName) {
            /** in_varName ignored */
            AbstractConstant result = null;
            if ((result = super.getParameterConstant(in_varName)) != null) {
                return result;
            } else {
                /** get index of token in formal parameters and get argument by this index */
                int index = formalArguments.indexOf(in_varName);
                if ((index >= 0) && (index < jjtGetNumChildren())) {
                    Node argument = jjtGetChild(index);
                    try {
                        argument = argument.calculate();
                        if (argument instanceof StringConstant) {
                            /** join constant and its token */
                            addParameter(new Parameter(in_varName, (StringConstant) argument));
                            result = (StringConstant) argument;
                        } else {
                            argument = AlgorithmUtils.getNumericArgument(argument);
                            if (argument != null) {
                                /** join constant and its token */
                                addParameter(new Parameter(in_varName, (Constant) argument));
                                result = (Constant) argument;
                            }
                        }
                    } catch (AlgorithmException e) {
                        return null;
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
    public generic(generic genericFunction) {
        super(0);
        this.setSignature(genericFunction.getSignature());
        this.setCode(genericFunction.getCode());
        this.setFormalArguments(genericFunction.getFormalArguments());
        this.setFunctionFactory(genericFunction.getFunctionFactory());
    }

    /**
     * Constructor
     */
    private generic(String name, String code) {
        super(0);
        this.setSignature(name);
        this.code = code;
    }

    /**
     * Add formal argument to this generic function
     *
     * @param name of formal argument
     */
    public void addFormalArgument(String name) throws AlgorithmException {
        if (formalArguments.contains(name)) {
            throw new AlgorithmException("Another formal argument with token " + name + " already exists");
        }
        formalArguments.add(name);
    }

    public ArrayList<String> getFormalArguments() {
        return formalArguments;
    }

    public void setFormalArguments(ArrayList<String> formalArguments) {
        this.formalArguments = formalArguments;
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter and functions provider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider)
        throws AlgorithmException {
        /** parse code of this generic function, substituting actual parameters instead of formal ones */
        StringMathParser parser = new StringMathParser(new StringReader(code));
        /** inherit parameters declared earlier */
        parser.setParameterProvider(argumentsProvider);
        /** inherit functions stl and declared earlier */
        parser.setFunctionProvider(argumentsProvider);
        /** start parsing */
        Node root = null;
        try {
            root = parser.Start();
        } catch (ParseException e) {
            throw new AlgorithmException("Exception parsing line: " + code);
        }
        /** sanity check - formal arguments number must match the number of actual arguments */
        if (formalArguments.size() != jjtGetNumChildren()) {
            throw new AlgorithmException("Invalid call of generic function " + getSignature()
                + ": the number of arguments does not match the number of formal arguments");
        }
        /** calculate generic function */
        if (root != null) {
            root = root.calculate(parameterProvider);
        }
        /** remove all parameters, used on internal calculation */
        argumentsProvider.clear();
        /** finally return the result */
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
    public static generic create(String declaration) throws AlgorithmException {
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
        /** token of the function */
        String name = signature.substring(0, beginBracesIndex);
        /** code of function */
        String code = _statement.substring(pos + 1).trim();
        /** create generic function */
        generic function = new generic(name, code);
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
    public static generic create(Element xml) throws AlgorithmException {
        /**
         * Create generic function
         */
        if (xml.getAttribute("signature") != null) {
            String signature = xml.getAttributeValue("signature");
            String code = xml.getText();
            generic function = new generic(signature, code);
            for (Object o : xml.getChildren("argument")) {
                Element argument = (Element) o;
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
            arg.setText(getFormalArguments().get(i));
            paramElement.addContent(arg);
        }
        if (getCode() != null) {
            paramElement.addContent(new CDATA(getCode()));
        }
        return paramElement;
    }
}
