// Created: 25.03.2004 T 16:30:26

package org.clematis.math.reference.functions;

import org.clematis.math.reference.AbstractConstant;
import org.clematis.math.reference.AlgorithmException;
import org.clematis.math.reference.Constant;
import org.clematis.math.reference.ExpressionParser;
import org.clematis.math.reference.FunctionFactory;
import org.clematis.math.reference.StringConstant;
import org.clematis.math.reference.Variable;
import org.clematis.math.reference.algorithm.AlgorithmUtils;
import org.clematis.math.reference.algorithm.Parameter;
import org.clematis.math.reference.algorithm.iFunctionProvider;
import org.clematis.math.reference.algorithm.iSimpleParameterProvider;
import org.clematis.math.reference.algorithm.iVariableProvider;
import org.clematis.math.reference.iExpressionItem;
/**
 * Returns the result of summing the expression expr as the variable
 * var runs through the values start to stop (inclusive).
 *
 * For example,
 *
 * $s = sum(i, 1, $n, "i^2");
 * calculates the sum of the squares of the integers 1..n.
 * NOTE: The dummy variable in the sum MUST NOT be decorated with a $ character.
 */
public class sum extends aFunction2
{
    class Provider implements iSimpleParameterProvider,
                                       iVariableProvider,
                                       iFunctionProvider
    {
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
         * @param in_varName parameter name
         * @return parameter value, string or double
         */
        public AbstractConstant getParameterConstant(String in_varName)
        {
            if ( in_varName != null && !"".equals(in_varName.trim()) && in_varName.equals(name) )
            {
                return new Constant( variable );
            }
            else
            {
                return new Constant( 0.0 );
            }
        }
        /**
         * Provides the parameter.
         * @param name the parameter name.
         * @return the parameter or null.
         */
        public Parameter getParameter(String name)
        {
            /**
             * Synthetic parameter.
             */
            AbstractConstant ac = getParameterConstant( name );
            if ( ac != null )
            {
                return new Parameter( name, ac );
            }
            return null;
        }
        /**
         * Provides function.
         *
         * @param name the function name
         * @return function or null
         */
        public aFunction getFunction(String name) throws AlgorithmException
        {
            return functionFactory.getFunction( name );
        }

        public AbstractConstant getVariableConstant(String in_varName)
        {
            return new Constant( variable );
        }
    }
    /**
     * Calculate a subtree of expression items
     * @return expression item instance
     */
    public iExpressionItem calculate() throws AlgorithmException
    {
        try
        {
           if( arguments.size() <= 3 || arguments.size() > 4 )
                throw new AlgorithmException("Invalid number of arguments in function 'sum': " + arguments.size() );

            iExpressionItem a1 = arguments.get(0).calculate();
            iExpressionItem a2 = arguments.get(1).calculate();
            iExpressionItem a3 = arguments.get(2).calculate();
            iExpressionItem a4 = arguments.get(3).calculate();

            if( a1 instanceof Variable == false ||
                !AlgorithmUtils.isGoodNumericArgument(a2) ||
                !AlgorithmUtils.isGoodNumericArgument(a3) ||
                a4 instanceof StringConstant == false )
            {
                sum retvalue = new sum();
                retvalue.setSignature("sum");
                retvalue.addArgument( a1 );
                retvalue.addArgument( a2 );
                retvalue.addArgument( a3 );
                retvalue.addArgument( a4 );
                retvalue.setMultiplier( retvalue.getMultiplier() * getMultiplier() );
                return retvalue;
            }
            /**
             * Get boundaries of looping
             */
            Variable v1 = (Variable)a1;
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            Constant c3 = AlgorithmUtils.getNumericArgument(a3);
            StringConstant s4 = (StringConstant)a4;
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

            for ( int i = (int)c2.getNumber(); i <= c3.getNumber(); i++ )
            {
                provider.variable = i;

                ExpressionParser parser =
                     new ExpressionParser(s4.getValue(null), provider, provider, provider );

                iExpressionItem item = parser.parse();
                item = item.calculate();
                if ( item instanceof Constant )
                {
                    sum += ( (Constant) item ).getNumber();
                }
            }

            return new Constant( sum * getMultiplier() );
        }
        catch(AlgorithmException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex.toString() );
        }
    }
}
