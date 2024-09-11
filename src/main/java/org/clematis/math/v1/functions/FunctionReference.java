// Created: 10.01.2007 T 17:26:32
package org.clematis.math.v1.functions;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.IFunction;
import org.clematis.math.v1.algorithm.IFunctionProvider;

/**
 * Class to wrap and create functions on expression parsing
 */
public class FunctionReference extends AbstractFunction {
    /**
     * Reference to function provider
     */
    private IFunctionProvider functionProvider = null;
    /**
     * Abstract function
     */
    private IFunction function = null;

    /**
     * Constructor with ident
     */
    public FunctionReference(IFunctionProvider functionProvider) {
        this.functionProvider = functionProvider;
    }

    /**
     * Create function or return the instance reference if function has already been created
     *
     * @return the instance reference if function has already been created
     */
    protected IFunction getFunction() throws AlgorithmException {
        if (function == null && functionProvider != null) {
            function = functionProvider.getFunction("token");
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
        if (this.function != null) {
            return this.function.calculate();
        }
        return null;
    }
}
