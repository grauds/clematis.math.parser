// Created: 23.12.2003 T 10:39:01
package org.clematis.math.functions;

import org.clematis.math.parsers.Node;
import org.jdom2.Element;

/**
 * This function does not have analog in mathml notation
 */
public abstract class aFunction2 extends aFunction {
    protected aFunction2() {
    }

    public aFunction2(int i) {
        super(i);
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element apply = new Element("apply", Node.NS_MATH);
        Element fn = new Element("fn", Node.NS_MATH);
        Element ci = new Element("ci", Node.NS_MATH);
        ci.setText(getSignature());
        fn.addContent(ci);
        apply.addContent(fn);

        for (Node argument : getChildren()) {
            apply.addContent(argument.toMathML());
        }

        return apply;
    }
}
