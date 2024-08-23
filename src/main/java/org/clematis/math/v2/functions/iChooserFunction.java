// Created: 05.04.2004 T 14:23:16
package org.clematis.math.v2.functions;

import org.clematis.math.v2.parsers.Node;

import java.util.ArrayList;

/**
 * Implement this interface to denote function is a function with several choices
 */
public interface iChooserFunction {
    /**
     * Return string or other variants for choosers
     *
     * @return string or other variants for choosers
     */
    ArrayList<Node> getVariants();
}
