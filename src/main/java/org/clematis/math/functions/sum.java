// Created: 25.03.2004 T 16:30:26
package org.clematis.math.functions;

import org.clematis.math.*;
import org.clematis.math.algorithm.*;
import org.clematis.math.parsers.Node;
import org.clematis.math.parsers.string.ParseException;
import org.clematis.math.parsers.string.StringMathParser;
import org.clematis.math.utils.AlgorithmUtils;

import java.io.StringReader;

/**
 * Returns the result of summing the expression expr as the variable
 * var runs through the values start to stop (inclusive).
 * <p>
 * For example,
 * <p>
 * $s = sum(i, 1, $n, "i^2");
 * calculates the sum of the squares of the integers 1..n.
 * NOTE: The dummy variable in the sum MUST NOT be decorated with a $ character.
 */
public class sum extends aFunction2 {
    class Provider implements iSimpleParameterProvider,
        iVariableProvider,
        iFunctionProvider {
        /**
         * Value of variable
         */
        public double variable = 0.0;
        /**
         * Name of variable
         */
        public String name = "";
        /**
         * Function factory
         */
        protected FunctionFactory functionFactory = new FunctionFactory();

        /**
         * Return parameter constant
         *
         * @param in_varName parameter token
         * @return parameter value, string or double
         */
        public AbstractConstant getParameterConstant(String in_varName) {
            if (in_varName != null && !"".equals(in_varName.trim()) && in_varName.equals(name)) {
                return new Constant(variable);
            } else {
                return new Constant(0.0);
            }
        }

        /**
         * Provides the parameter.
         *
         * @param name the parameter token.
         * @return the parameter or null.
         */
        public Parameter getParameter(String name) {
            /**
             * Synthetic parameter.
             */
            AbstractConstant ac = getParameterConstant(name);
            if (ac != null) {
                return new Parameter(name, ac);
            }
            return null;
        }

        /**
         * Provides function.
         *
         * @param name the function token
         * @return function or null
         */
        public aFunction getFunction(String name) throws AlgorithmException {
            return functionFactory.getFunction(name);
        }

        /**
         * This method returns true if function provider has function with given signature
         *
         * @param name of the function
         * @return true if function provider has function with given signature
         */
        public boolean hasFunction(String name) {
            return functionFactory.hasFunction(name);
        }

        public AbstractConstant getVariableConstant(String in_varName) {
            return new Constant(variable);
        }
    }

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider
     * @return expression item instance
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 3 || getArguments().size() > 4) {
                throw new AlgorithmException("Invalid number of arguments in function 'sum': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);
            Node a3 = getArguments().get(2).calculate(parameterProvider);
            Node a4 = getArguments().get(3).calculate(parameterProvider);

            if (!(a1 instanceof Variable) ||
                !AlgorithmUtils.isGoodNumericArgument(a2) ||
                !AlgorithmUtils.isGoodNumericArgument(a3) ||
                !(a4 instanceof StringConstant)) {
                sum retvalue = new sum();
                retvalue.setSignature("sum");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.addArgument(a3);
                retvalue.addArgument(a4);
                return retvalue;
            }
            /**
             * Get boundaries of looping
             */
            Variable v1 = (Variable) a1;
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            Constant c3 = AlgorithmUtils.getNumericArgument(a3);
            StringConstant s4 = (StringConstant) a4;
            /**
             * Total sum
             */
            double sum = 0.0;
            /**
             * Provider of variable that is the first argument
             */
            Provider provider = new Provider();
            provider.functionFactory = this.getFunctionFactory();
            provider.name = v1.getName();

            for (int i = (int) c2.getNumber(); i <= c3.getNumber(); i++) {
                provider.variable = i;
                /** parse code of this sum, substituting actual parameters instead of formal ones */
                StringMathParser parser = new StringMathParser(new StringReader(s4.getValue()));
                /** inherit parameters declared earlier */
                parser.setParameterProvider(provider);
                /** inherit functions stl and declared earlier */
                parser.setFunctionProvider(provider);
                /** inherit variables values */
                parser.setVariableProvider(provider);
                /** start parsing */
                Node root = null;
                try {
                    root = parser.Start();
                } catch (ParseException e) {
                    throw new AlgorithmException("Exception parsing line: " + s4.getValue());
                }
                root = root.calculate(parameterProvider);
                if (root instanceof Constant) {
                    sum += ((Constant) root).getNumber();
                }
            }

            return new Constant(sum);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }
}
