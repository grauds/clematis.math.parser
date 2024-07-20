// Created: Jan 21, 2003 T 4:22:51 PM
package org.clematis.math.v1;

import java.util.ArrayList;

/**
 * Function is an expression item with parameter
 */
public interface iFunction extends iExpressionItem {
    /**
     * Add parameter to a function
     *
     * @param in_parameter expression item as a parameter
     */
    void addArgument(iExpressionItem in_parameter);

    /**
     * Add arguments to a function
     *
     * @param arguments expression items as a parameter
     */
    void addArguments(ArrayList<iExpressionItem> arguments);

    /**
     * Removes all arguments from the function
     */
    void removeArguments();

    /**
     * Returns this function signature
     *
     * @return string function signature
     */
    String getSignature();

    /**
     * Sets this function signature
     *
     * @param signature - string function signature
     */
    void setSignature(String signature);

    /**
     * Return a set of arguments of this function
     *
     * @return an iExpressionItem set of arguments of this function
     */
    iExpressionItem[] getArguments();
}
