// Created: 23.12.2003 T 10:39:01
package org.clematis.math.v1.functions;

import org.clematis.math.v1.iExpressionItem;
import org.jdom2.Element;

/**
 * This function does not have analog in mathml notation
 */
public abstract class aFunction2 extends aFunction {
    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element apply = new Element("apply", iExpressionItem.NS_MATH);

        Element times = new Element("times", iExpressionItem.NS_MATH);
        apply.addContent(times);

        Element cn = new Element("cn", iExpressionItem.NS_MATH);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element("apply", iExpressionItem.NS_MATH);
        Element fn = new Element("fn", iExpressionItem.NS_MATH);
        Element ci = new Element("ci", iExpressionItem.NS_MATH);
        ci.setText(signature);
        fn.addContent(ci);
        apply2.addContent(fn);

        for (iExpressionItem argument : arguments) {
            apply2.addContent(argument.toMathML());
        }

        apply.addContent(apply2);

        return apply;
    }
}
