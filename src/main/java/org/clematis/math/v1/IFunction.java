// Created: Jan 21, 2003 T 4:22:51 PM
package org.clematis.math.v1;

import java.util.ArrayList;
import java.util.List;

import org.clematis.math.IExpressionItem;
import org.clematis.math.v1.algorithm.Key;

/**
 * Function is an expression item with parameter
 */
public interface IFunction extends IExpressionItem {
    /**
     * Add parameter to a function
     *
     * @param item expression item as a parameter
     */
    void addArgument(IExpressionItem item);

    /**
     * Add arguments to a function
     *
     * @param arguments expression items as a parameter
     */
    void addArguments(ArrayList<IExpressionItem> arguments);

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
     * Return a list of arguments of this function
     *
     * @return an iExpressionItem set of arguments of this function
     */
    List<IExpressionItem> getArguments();
}
