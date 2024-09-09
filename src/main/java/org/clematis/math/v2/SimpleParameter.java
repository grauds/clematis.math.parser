// Created: 11.04.2005 T 15:51:27
package org.clematis.math.v2;

import java.io.Serializable;

import org.clematis.math.AlgorithmException;
import org.clematis.math.XMLConstants;
import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.algorithm.Parameter;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;

import lombok.Getter;
/**
 * Simple parameter - the holder for value and name of {@link Parameter}
 */
@Getter
public class SimpleParameter extends SimpleNode implements Serializable {
    /**
     * Last calculation result of the parameter expression.
     */
    protected AbstractConstant currentResult = null;

    public SimpleParameter() {
        super(0);
    }

    /**
     * Simple parameter constructor to match one of simple node
     *
     * @param i simple node index
     */
    public SimpleParameter(int i) {
        super(i);
    }

    /**
     * Shallow copy constructor. This does not copy parent and children nodes.
     *
     * @param node to copy
     */
    public SimpleParameter(SimpleParameter node) {
        super(node);
        setCurrentResult(node.getCurrentResult());
    }

    /**
     * Gets parameter token.
     *
     * @return <code>String</code> containing parameter token.
     */
    public String getName() {
        return token;
    }

    /**
     * Sets parameter name
     *
     * @param name of parameter
     */
    public void setName(String name) {
        this.token = name.trim();
    }

    /**
     * Sets result manually, usually result is being calculated
     *
     * @param currentResult to set
     */
    public void setCurrentResult(AbstractConstant currentResult) {
        if (currentResult != null) {
            this.currentResult = currentResult.copy();
        } else {
            this.currentResult = null;
        }
    }

    /**
     * Tells the caller whether if this parameter value is less than zero
     *
     * @return returns true if this parameter value is less than zero
     */
    public boolean lessThanZero() {
        if (getCurrentResult() != null && getCurrentResult() instanceof Constant) {
            return ((Constant) getCurrentResult()).getNumber() < 0;
        }
        return false;
    }

    /**
     * Alternate parameter name, i.e. make following transitions:
     *
     * <p>
     * $a -> ${a}
     * ${a} -> $a
     *
     * @param name of parameter to alternate
     * @return new parameter name
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public static String alternateParameterName(String name) {
        if (XMLConstants.CONDITION_NAME.equals(name)) {
            return name;
        }
        if (isNameWithBraces(name)) {
            return "$" + name.substring(2, name.length() - 1);
        } else {
            return "${" + name.substring(1) + "}";
        }
    }

    /**
     * Returns true if name is with braces, like ${k1}
     *
     * @param name parameter name
     * @return true if name is with braces, like ${k1}
     */
    public static boolean isNameWithBraces(String name) {
        if (name != null && name.length() > 1) {
            return name.charAt(1) == '{';
        } else {
            return false;
        }
    }

    /**
     * Calculate the value of this node. If current result is set as a result from
     * one of the previous steps calculations, it returns a copy of it.
     *
     * @param parameterProvider
     * @return the calculated first argument or result of operation
     * @throws AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculate(IParameterProvider parameterProvider) throws AlgorithmException {
        if (currentResult != null) {
            return currentResult.copy();
        } else {
            return new StringConstant(this.getName());
        }
    }
}
