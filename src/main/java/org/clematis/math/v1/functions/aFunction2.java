// Created: 23.12.2003 T 10:39:01
package org.clematis.math.v1.functions;

import org.clematis.math.v1.IExpressionItem;
import org.jdom2.Element;

/**
 * This function does not have analog in mathml notation
 */
@SuppressWarnings("checkstyle:TypeName")
public abstract class aFunction2 extends aFunction {

    public static final String APPLY_ELEMENT_NAME = "apply";

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element apply = new Element(APPLY_ELEMENT_NAME, IExpressionItem.NS_MATH);

        Element times = new Element("times", IExpressionItem.NS_MATH);
        apply.addContent(times);

        Element cn = new Element("cn", IExpressionItem.NS_MATH);
        cn.setText(Double.toString(getMultiplier()));
        apply.addContent(cn);

        Element apply2 = new Element(APPLY_ELEMENT_NAME, IExpressionItem.NS_MATH);
        Element fn = new Element("fn", IExpressionItem.NS_MATH);
        Element ci = new Element("ci", IExpressionItem.NS_MATH);
        ci.setText(signature);
        fn.addContent(ci);
        apply2.addContent(fn);

        for (IExpressionItem argument : arguments) {
            apply2.addContent(argument.toMathML());
        }

        apply.addContent(apply2);
        return apply;
    }
}
