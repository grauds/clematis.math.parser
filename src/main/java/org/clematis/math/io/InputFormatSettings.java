// Created: 28.06.2004 T 12:44:01
package org.clematis.math.io;

import org.clematis.math.utils.StringUtils;

import java.io.Serializable;

/**
 * Format settings for users input
 */
public class InputFormatSettings implements Serializable {
    /**
     * Remove brackets flag - the number in braskets
     * is treated as negative
     */
    private boolean inBrackets = false;
    /**
     * Remove dollar signs - the number with leading dollar
     * sign is still a number - amount of dollars
     */
    private boolean hasDollar = false;
    /**
     * Has thousands grouping or not
     */
    private boolean grouping = false;
    /**
     * Leading zeros as fraction number
     */
    private boolean leadingZerosAsFraction = false;

    /**
     * Default constructor
     */
    public InputFormatSettings() {
    }

    /**
     * Copy constructor
     */
    public InputFormatSettings(InputFormatSettings inputFormatSettings) {
        this.grouping = inputFormatSettings.isGrouping();
        this.inBrackets = inputFormatSettings.isInBrackets();
        this.leadingZerosAsFraction = inputFormatSettings.isLeadingZerosAsFraction();
        this.hasDollar = inputFormatSettings.isHasDollar();
    }

    /**
     * Small preparation of number string to parsing
     * Example:
     * <p>
     * $-(154,254)
     *
     * @param str number string
     * @return trimmed number string
     */
    public String prepareToParse(String str) {
        /**
         * Number in braces is negative
         */
        boolean negative = false;
        /**
         * String should not have whitespaces
         */
        String ret_str = StringUtils.removeWhitespaces(str);
        /**
         * Remove dollar sign
         */
        if (isHasDollar() && ret_str.startsWith("$")) {
            ret_str = ret_str.substring(1);
        }
        /**
         * Negative and positive signs
         */
        if (str.startsWith("-")) {
            ret_str = ret_str.substring(1);
            negative = true;
        } else if (ret_str.startsWith("+")) {
            ret_str = ret_str.substring(1);
            negative = false;
        }
        /**
         * Negative value may be inside brackets
         */
        if (isInBrackets()) {
            if (ret_str.startsWith("(") && ret_str.endsWith(")")) {
                ret_str = StringUtils.removeChars(ret_str, "(");
                ret_str = StringUtils.removeChars(ret_str, ")");
                negative = !negative;
            } else if (ret_str.startsWith("[") && ret_str.endsWith("]")) {
                ret_str = StringUtils.removeChars(ret_str, "[");
                ret_str = StringUtils.removeChars(ret_str, "]");
                negative = !negative;
            } else if (ret_str.startsWith("{") && ret_str.endsWith("}")) {
                ret_str = StringUtils.removeChars(ret_str, "{");
                ret_str = StringUtils.removeChars(ret_str, "}");
                negative = !negative;
            }
        }
        /**
         * Number may be delimited with commas
         */
        if (isGrouping()) {
            ret_str = NumberFormatter.ungroupThousands(ret_str);
        }
        /**
         * In case number has leading zeros and doesnt have
         * dot - it is treated as decimal fraction
         * 00345 -> 0.00345
         */
        if (isLeadingZerosAsFraction() && ret_str.length() > 0) {
            if (ret_str.indexOf(".") == -1 && ret_str.charAt(0) == '0') {
                ret_str = "." + ret_str;
            }
        }
        return (negative ? "-" : "") + ret_str.toUpperCase();
    }

    public boolean isGrouping() {
        return grouping;
    }

    public void setGrouping(boolean grouping) {
        this.grouping = grouping;
    }

    public boolean isHasDollar() {
        return hasDollar;
    }

    public void setHasDollar(boolean hasDollar) {
        this.hasDollar = hasDollar;
    }

    public boolean isInBrackets() {
        return inBrackets;
    }

    public void setInBrackets(boolean inBrackets) {
        this.inBrackets = inBrackets;
    }

    public boolean isLeadingZerosAsFraction() {
        return leadingZerosAsFraction;
    }

    public void setLeadingZerosAsFraction(boolean leadingZerosAsFraction) {
        this.leadingZerosAsFraction = leadingZerosAsFraction;
    }
}
