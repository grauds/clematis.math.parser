// Created: 12.05.2004 T 17:36:51
package org.clematis.math.v1.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.FunctionFactory;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.algorithm.DefaultParameterProvider;
import org.clematis.math.v1.algorithm.IParameterProvider;
import org.clematis.math.v1.algorithm.Key;
import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.parsers.ExpressionParser;
import org.jdom2.CDATA;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Generic function can be created on-the-fly then parsing
 * algorithm code.
 */
@Getter
@Setter
public class GenericFunction extends aFunction {
    /**
     * Special name of generic function
     */
    public static final String GENERIC_FUNCTION_NAME = "function";
    public static final String SIGNATURE_ATTRIBUTE_NAME = "signature";
    public static final String ARGUMENT_ELEMENT_NAME = "argument";
    /**
     * Arguments provider
     */
    private final ArgumentsProvider argumentsProvider = new ArgumentsProvider();
    /**
     * Code of this function
     */
    private String code = "";
    /**
     * Copy constructor
     *
     * @param genericFunction to copy values from
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

    public List<Key> getFormalArguments() {
        return argumentsProvider.getLines();
    }

    public void setFormalArguments(List<Key> lines) {
        this.argumentsProvider.setLines(lines);
    }

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        return calculate(null);
    }

    /**
     * Calculate a subtree of expression items with parameter
     * and functions provider
     *
     * @param parameterProvider parameter and functions provider
     * @return expression item instance
     */
    public IExpressionItem calculate(IParameterProvider parameterProvider)
        throws AlgorithmException {
        /* parse code of this generic function */
        ExpressionParser parser = new ExpressionParser(code, argumentsProvider, null, argumentsProvider);
        IExpressionItem root = parser.parse();
        /* calculate generic function */
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
     * Create function from declaration
     *
     * @param declaration of generic function
     * @return instance of generic function
     */
    public static GenericFunction create(String declaration) throws AlgorithmException {
        String statement = declaration.substring(GENERIC_FUNCTION_NAME.length() + 1);
        /* position of = sign */
        int pos = statement.indexOf('=');
        if (pos == -1) {
            throw new AlgorithmException("Operand '=' is missed: " + declaration);
        }
        /* signature of the function */
        String signature = statement.substring(0, pos).trim();
        if (signature.isEmpty()) {
            throw new AlgorithmException("Function signature is empty");
        }
        int beginBracesIndex = signature.indexOf('(');
        int endBracesIndex = signature.indexOf(')');
        /* name of the function */
        String name = signature.substring(0, beginBracesIndex);
        /* code of function */
        String code = statement.substring(pos + 1).trim();
        /* create generic function */
        GenericFunction function = new GenericFunction(name, code);
        /* formal parameters */
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
        /*
         * Create generic function
         */
        if (xml.getAttribute(SIGNATURE_ATTRIBUTE_NAME) != null) {
            String signature = xml.getAttributeValue(SIGNATURE_ATTRIBUTE_NAME);
            String code = xml.getText();
            GenericFunction function = new GenericFunction(signature, code);
            for (Element argument : xml.getChildren(ARGUMENT_ELEMENT_NAME)) {
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
        Element paramElement = new Element(GENERIC_FUNCTION_NAME);
        if (getSignature() != null) {
            paramElement.setAttribute(SIGNATURE_ATTRIBUTE_NAME, getSignature());
        }
        for (int i = 0; i < getFormalArguments().size(); i++) {
            Element arg = new Element(ARGUMENT_ELEMENT_NAME);
            arg.setText(getFormalArguments().get(i).getName());
            paramElement.addContent(arg);
        }
        if (getCode() != null) {
            paramElement.addContent(new CDATA(getCode()));
        }
        return paramElement;
    }


    /**
     * Provides generic function with actual
     * arguments instead of formal ones.
     */
    class ArgumentsProvider extends DefaultParameterProvider {

        public List<Key> getLines() {
            return this.lines;
        }

        public void setLines(List<Key> lines) {
            this.lines = new ArrayList<>();
            this.lines.addAll(lines);
        }

        /**
         * Return parameter constant
         *
         * @param name parameter name
         * @return parameter value, string or double
         */
        @SuppressWarnings("checkstyle:NestedIfDepth")
        public AbstractConstant getParameterConstant(String name) throws AlgorithmException {
            /* name ignored */
            AbstractConstant result = super.getParameterConstant(name);
            if (result != null) {
                return result;
            } else {
                /* get index of name in formal parameters and get argument by this index */
                int index = lines.indexOf(new Key(name));
                if ((index >= 0) && (index < arguments.size())) {
                    IExpressionItem argument = arguments.get(index);
                    argument = argument.calculate();
                    if (argument instanceof AbstractConstant) {
                        /* join constant and its name */
                        addParameter(new Parameter(name, (AbstractConstant) argument));
                        result = (AbstractConstant) argument;
                    }
                }
            }
            return result;
        }
    }
}
