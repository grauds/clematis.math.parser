// Created: 11.04.2005 T 15:51:27
package org.clematis.math.v1.algorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.clematis.math.utils.StringUtils;
import org.clematis.math.XMLConstants;
import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.Constant;

import lombok.Getter;
/**
 * Simple parameter - the holder for value and name of {@link Parameter}
 */
@Getter
public class SimpleParameter implements Serializable {
    /**
     * Regular expression to find parameter in a text
     */
    public static final String FIND_EXPRESSION = "(\\x24[a-zA-Z0-9_]+)|(\\x24\\x7B[a-zA-Z0-9_]+\\x7D)";
   /**
     * Parameter name.
     */
    private String name = null;
    /**
     * Last calculation result of the parameter expression.
     */
    private AbstractConstant currentResult = null;
    /**
     * Sets parameter name
     *
     * @param name of parameter
     */
    public void setName(String name) {
        this.name = name.trim();
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
     * Finds parameters in string. Quoted parameters will be skipped, if flag "skipInsideStrings" is set
     *
     * @param string            the explored string
     * @param skipInsideStrings quoted parameters will be skipped,
     *                          if this flag is set
     * @return array list with parameter names, may be empty, never null
     */
    public static ArrayList<String> findParameters(String string,
                                                   IParameterProvider provider,
                                                   boolean skipInsideStrings) {
        ArrayList<String> result = new ArrayList<>();
        if (provider != null && string != null && !string.trim().isEmpty()) {
            List<String> tokens = StringUtils.tokenizeReg(string, FIND_EXPRESSION, false);
            int apos = 0;
            for (String token : tokens) {
                /* filter only parameters */
                if (provider.getParameter(token) != null
                    && (!skipInsideStrings || apos % 2 == 0)
                ) {
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
}
