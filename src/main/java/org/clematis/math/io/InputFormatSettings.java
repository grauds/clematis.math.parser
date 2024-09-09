// Created: 28.06.2004 T 12:44:01
package org.clematis.math.io;

import static org.clematis.math.MathUtils.DECIMAL_SEPARATOR;
import org.clematis.math.MathUtils;
import org.clematis.math.StringUtils;
import org.clematis.math.XMLConstants;

import lombok.Getter;

/**
 * Format settings for users input
 */
@Getter
public class InputFormatSettings {
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
    public InputFormatSettings() {}

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
    @SuppressWarnings("checkstyle:CyclomaticComplexity")
    public String prepareToParse(String str) {
        /*
         * Number in braces is negative
         */
        boolean negative = false;
        /*
         * String should not have whitespaces
         */
        String result = StringUtils.removeWhitespaces(str);
        /*
         * Remove dollar sign
         */
        if (isHasDollar() && result.startsWith("$")) {
            result = result.substring(1);
        }
        /*
         * Negative and positive signs
         */
        if (str.startsWith(XMLConstants.MINUS_SIGN)) {
            result = result.substring(1);
            negative = true;
        } else if (result.startsWith(XMLConstants.PLUS_SIGN)) {
            result = result.substring(1);
        }
        /*
         * Negative value may be inside brackets
         */
        if (isInBrackets()) {
            if (result.startsWith(XMLConstants.OPENING_BRACKET) && result.endsWith(XMLConstants.CLOSING_BRACKET)) {
                result = StringUtils.removeChars(result, XMLConstants.OPENING_BRACKET);
                result = StringUtils.removeChars(result, XMLConstants.CLOSING_BRACKET);
                negative = !negative;
            } else if (result.startsWith(XMLConstants.OPENING_SQUARE_BRACKET) && result.endsWith(
                XMLConstants.CLOSING_SQUARE_BRACKET)) {
                result = StringUtils.removeChars(result, XMLConstants.OPENING_SQUARE_BRACKET);
                result = StringUtils.removeChars(result, XMLConstants.CLOSING_SQUARE_BRACKET);
                negative = !negative;
            } else if (result.startsWith(XMLConstants.OPENING_CURLY_BRACKET) && result.endsWith(
                XMLConstants.CLOSING_CURLY_BRACKET)) {
                result = StringUtils.removeChars(result, XMLConstants.OPENING_CURLY_BRACKET);
                result = StringUtils.removeChars(result, XMLConstants.CLOSING_CURLY_BRACKET);
                negative = !negative;
            }
        }
        /*
         * Number may be delimited with commas
         */
        if (isGrouping()) {
            result = MathUtils.ungroupThousands(result);
        }
        /*
         * In case number has leading zeros and doesnt have
         * dot - it is treated as decimal fraction
         * 00345 -> 0.00345
         */
        if (isLeadingZerosAsFraction() && !result.isEmpty()) {
            if (!result.contains(DECIMAL_SEPARATOR) && result.charAt(0) == '0') {
                result = DECIMAL_SEPARATOR + result;
            }
        }
        return (negative ? XMLConstants.MINUS_SIGN : "") + result.toUpperCase();
    }
}
