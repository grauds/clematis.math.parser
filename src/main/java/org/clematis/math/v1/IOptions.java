// Created: 05.04.2004 T 14:23:16
package org.clematis.math.v1;

import java.util.ArrayList;

import org.clematis.math.IExpressionItem;

/**
 * Implement this interface to denote function is a logic function with several choices
 */
public interface IOptions {
    /**
     * Return string or other variants for choosers
     *
     * @return string or other variants for choosers
     */
    ArrayList<IExpressionItem> getOptions();
}
