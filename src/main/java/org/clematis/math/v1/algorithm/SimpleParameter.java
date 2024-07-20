// Created: 11.04.2005 T 15:51:27
package org.clematis.math.v1.algorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.Constant;
import org.clematis.math.utils.StringUtils;

/**
 * Simple parameter - the holder for value and
 */
public class SimpleParameter implements Serializable {
    /**
     * Parameter name.
     */
    private String name = null;
    /**
     * Last calculation result of the parameter expression.
     */
    protected AbstractConstant currentResult = null;
    /**
     * Regular expression to find parameter in question text
     */
    public final static String FIND_EXPRESSION = "(\\x24[a-zA-Z0-9_]+)|(\\x24\\x7B[a-zA-Z0-9_]+\\x7D)";
    /**
     * Regular expression to find parameter in question text
     */
    public final static String FIND_EXPRESSION_ALL =
        "(\\x24[a-zA-Z_]+[0-9_]*)|(\\x24\\x7B[a-zA-Z_]+[0-9_]*\\x7D)|condition";

    /**
     * Gets parameter name.
     *
     * @return <code>String</code> containing parameter name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets parameter name
     *
     * @param name of parameter
     */
    void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Finds parameters in string. Quoted parameters will be skipped,
     * if flag "skipInsideStrings" is set
     *
     * @param string            the explored string
     * @param skipInsideStrings quoted parameters will be skipped,
     *                          if this flag is set
     * @return array list with parameter names, may be empty, never null
     */
    public static ArrayList findParameters(String string,
                                           iParameterProvider provider,
                                           boolean skipInsideStrings) {
        ArrayList result = new ArrayList();
        if (provider != null && string != null && !string.trim().equals("")) {
            List<String> tokens = StringUtils.tokenizeReg(string, FIND_EXPRESSION, false);
            int apos = 0;
            for (int i = 0; i < tokens.size(); i++) {
                /** filter only parameters */
                String token = tokens.get(i);

                if (provider.getParameter(token) != null &&
                    (!skipInsideStrings || apos % 2 == 0)) {
                    result.add(token);
                } else {
                    int cursor = -1;
                    while ((cursor = token.indexOf("\"", cursor + 1)) != -1) {
                        apos++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets value wrapper.
     *
     * @return <code>iConstant</code> representing value wrapper.
     */
    public AbstractConstant getCurrentResult() {
        return currentResult;
    }

    /**
     * Sets current result violently
     *
     * @param currentResult
     */
    void setCurrentResult(AbstractConstant currentResult) {
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
     * Alternate parameter name, i.e. make following
     * transitions:
     * <p>
     * $a -> ${a}
     * ${a} -> $a
     *
     * @param name of parameter to alternate
     * @return new parameter name
     */
    public static String alternateParameterName(String name) {
        if (Parameter.CONDITION_NAME.equals(name)) {
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
}
