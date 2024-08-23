// Created: 10.01.2007 T 17:26:32
package org.clematis.math.v2;

import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.iFunctionProvider;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.clematis.math.v2.functions.aFunction;
import org.clematis.math.v2.parsers.Node;

import java.util.ArrayList;

/**
 * Class to wrap and create functions on expression parsing
 */
public class FunctionReference extends aFunction {
    /**
     * Reference to function provider
     */
    private iFunctionProvider functionProvider = null;
    /**
     * Abstract function
     */
    private aFunction function = null;

    /**
     * Constructor with ident
     *
     * @param i - ident
     */
    public FunctionReference(int i, iFunctionProvider functionProvider) {
        super(i);
        this.functionProvider = functionProvider;
    }

    /**
     * Create function or return the instance reference if function has already been created
     *
     * @return the instance reference if function has already been created
     */
    protected aFunction getFunction() throws AlgorithmException {
        if (function == null && functionProvider != null) {
            function = functionProvider.getFunction(token);
        }
        return function;
    }

    public void jjtAddChild(Node n, int i) {
        try {
            getFunction().jjtAddChild(n, i);
        } catch (AlgorithmException e) {
            super.jjtAddChild(n, i);
        }
    }

    public String getTokenName() {
        try {
            return getFunction().getTokenName();
        } catch (AlgorithmException e) {
            return super.getTokenName();
        }
    }

    public int getTokenKind() {
        try {
            return getFunction().getTokenKind();
        } catch (AlgorithmException e) {
            return super.getTokenKind();
        }
    }

    public void setTokenKind(int kind) {
        try {
            getFunction().setTokenKind(kind);
        } catch (AlgorithmException e) {
            super.setTokenKind(kind);
        }
    }

    public int getId() {
        try {
            return getFunction().getId();
        } catch (AlgorithmException e) {
            return super.getId();
        }
    }

    public void jjtSetParent(Node n) {
        try {
            getFunction().jjtSetParent(n);
        } catch (AlgorithmException e) {
            super.jjtSetParent(n);
        }
    }

    public Node jjtGetParent() {
        try {
            return getFunction().jjtGetParent();
        } catch (AlgorithmException e) {
            return super.jjtGetParent();
        }
    }

    public Node[] getChildren() {
        try {
            return getFunction().getChildren();
        } catch (AlgorithmException e) {
            return super.getChildren();
        }
    }

    public Node jjtGetChild(int i) {
        try {
            return getFunction().jjtGetChild(i);
        } catch (AlgorithmException e) {
            return super.jjtGetChild(i);
        }
    }

    public int jjtGetNumChildren() {
        try {
            return getFunction().jjtGetNumChildren();
        } catch (AlgorithmException e) {
            return super.jjtGetNumChildren();
        }
    }

    public String toString(String prefix) {
        try {
            return getFunction().toString(prefix);
        } catch (AlgorithmException e) {
            return super.toString(prefix);
        }
    }

    /**
     * Calculates the values of children of this node. It calls calculate() on every child.
     *
     * @return the calculated first argument or result of operation
     * @throws AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculateArguments(iParameterProvider parameterProvider, ArrayList<Node> calculated_nodes)
        throws AlgorithmException {
        return getFunction().calculateArguments(parameterProvider, calculated_nodes);
    }

    /**
     * Calculate the value of this node. It calls calculate() on every child and then makes
     * operation which this node was created for. If no operation is defined, method returns
     * the calculated first argument. Takes the values for parameters from parameter provider, if
     * they are exist.
     *
     * @param parameterProvider with precalculated values for some parameters
     * @return the calculated first argument or result of operation
     * @throws AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        return getFunction().calculate(parameterProvider);
    }

    /**
     * Dump this node along with its children to text tree to standard output stream
     */
    public void dump(String prefix) {
        try {
            getFunction().dump(prefix);
        } catch (AlgorithmException e) {
            super.dump(prefix);
        }
    }
}
