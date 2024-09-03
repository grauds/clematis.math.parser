// Created: Feb 13, 2003 T 6:20:41 PM
package org.clematis.math.v2;

import java.io.Serializable;

import org.clematis.math.v2.algorithm.IParameterProvider;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;
import org.jdom2.Element;

import lombok.Getter;

/**
 * Representation variables, for example x, y or z. See grammar description for more details.
 */
@Getter
public class Variable extends SimpleNode implements Serializable {

    /**
     * The assigned value to this variable.
     */
    protected AbstractConstant currentResult = null;

    /**
     * Constructor with node ident
     *
     * @param i node ident
     */
    public Variable(int i) {
        super(i);
    }

    /**
     * Constructor.
     *
     * @param name String containing the variable token.
     */
    private Variable(String name) {
        super();
        token = name;
    }

    /**
     * Copy constructor
     *
     * @param v variable to copy
     */
    public Variable(Variable v) {
        super(v);
        if (v.currentResult != null) {
            this.currentResult = v.currentResult.copy();
        }
    }

    /**
     * Create either variable or multiplication, it depends on
     * the token in arguments.
     *
     * @param name for future variable
     * @return variable or multiplication
     */
    public static Node create(String name) {
        return new Variable(name);
    }

    /**
     * Gets the variable token.
     *
     * @return String containing the variable token.
     */
    public String getName() {
        return token;
    }

    /**
     * Calculate a subtree of expression items
     *
     * @param parameterProvider instance
     * @return expression item instance
     */
    public Node calculate(IParameterProvider parameterProvider) {
        if (getCurrentResult() == null) {
            return new Variable(this);
        } else {
            return getCurrentResult().copy();
        }
    }

    /**
     * Sets current result violently
     *
     * @param currentResult
     */
    public void setCurrentResult(AbstractConstant currentResult) {
        if (currentResult != null) {
            this.currentResult = currentResult.copy();
        } else {
            this.currentResult = null;
        }
    }

    /**
     * Returns a string representation of the object.
     * <p>
     * 2x, 50y etc
     *
     * @return a string representation of the object.
     */
    public String toString() {
        return getName();
    }

    /**
     * Provides mathml formatted element, representing expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element ci = new Element("ci", Node.NS_MATH);
        ci.setAttribute("type", "real");
        ci.setText(this.getName());
        return ci;
    }
}
