// Created: 10.01.2007 T 17:26:32
package org.clematis.math.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.IFunction;
import org.clematis.math.algorithm.IFunctionProvider;

/**
 * Class to wrap and create functions on expression parsing
 */
public class FunctionReference extends AbstractFunction {

    /**
     * Abstract function
     */
    private IFunction function = null;

    /**
     * Constructor with ident
     */
    public FunctionReference(IFunctionProvider functionProvider) {
        this.functionFactory = functionProvider;
    }

    /**
     * Create function or return the instance reference if function has already been created
     *
     * @return the instance reference if function has already been created
     */
    protected IFunction getFunction() throws AlgorithmException {
        if (function == null && functionFactory != null) {
            function = functionFactory.getFunction(signature);
            function.addArguments(this.arguments);
        }
        return function;
    }

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    @Override
    public IExpressionItem calculate() throws AlgorithmException {
        if (this.getFunction() != null) {
            return this.getFunction().calculate();
        }
        return null;
    }
}
