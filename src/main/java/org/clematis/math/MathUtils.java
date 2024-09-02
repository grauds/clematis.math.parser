// $Id: MathUtils.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 12, 2003 T 10:48:45 AM

package org.clematis.math;

import java.util.Random;
import java.util.regex.Pattern;

import org.clematis.math.v2.io.InputFormatSettings;

/**
 * A collection of math utils.
 */
public class MathUtils {
    /**
     * Get number format locale
     */
    public static final String E = "e";
    public static final char THOUSANDS_SEPARATOR = ',';
    public static final int THOUSANDS_SEPARATOR_POSITION = 4; // each 4th element
    public static final int THOUSANDS_GROUP_LENGTH = 3;
    public static final String DECIMAL_SEPARATOR = ".";
    public static final String ZERO_STRING = "0";
    public static final String MINUS_SIGN = "-";
    /**
     * Random seed
     */
    public static Random rand = new Random(System.currentTimeMillis());

    public static boolean isDigit(char digit) {
        String test = "0123456789";
        return test.indexOf(digit) != -1;
    }

    /**
     * This method returns true, if string represents integer value
     *
     * @param str with number
     * @return true, if string represents integer value
     */
    public static boolean isInteger(String str) {
        try {
            double d = Double.parseDouble(str);
            return (int) d == d;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isPlainInteger(String str) {
        return MathUtils.isInteger(str) && !str.toLowerCase().contains(E);
    }

    public static boolean isZero(String str) {
        try {
            double digit = Double.parseDouble(str);
            return digit == 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    /**
     * Cut trailing zeros like ".00000" to ".0", ".9067000" to ".9067", thus making the number more presentable.
     *
     * @param str initial number string
     * @return resulting number string
     */
    public static String cutTrailingZeros(String str) {

        if (str == null) {
            return "";
        }

        String result = str;
        if (result.contains(DECIMAL_SEPARATOR) && !result.toLowerCase().contains(E)) {

            // remove decimal separator
            if (result.endsWith(DECIMAL_SEPARATOR)) {
                result = result.substring(0, result.length() - 1);
            }

            // remove trailing zeros
            while (result.endsWith(ZERO_STRING)) {
                if (result.endsWith(".0")) {
                    result = result.substring(0, result.length() - 2);
                    break;
                }
                result = result.substring(0, result.length() - 1);
            }
        }
        return result;
    }

    /**
     * Parse string a in smart way
     *
     * @param str incoming string
     * @return Number
     * @throws NumberFormatException if string is not a number
     */
    public static double parse(String str, InputFormatSettings formatSettings) throws NumberFormatException {
        String toParse = formatSettings.prepareToParse(str);
        if (checkJavaFormatString(toParse)) {
            return Double.parseDouble(toParse);
        } else {
            throw new NumberFormatException("Not a valid number: " + str);
        }
    }

    /**
     * A decimal floating-point string representing a finite positive
     * number without a leading sign has at most five basic pieces:
     *  digits . digits ExponentPart FloatTypeSuffix
     *
     * @param str to verify for correctness
     * @return true if string is a number in the correct format
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static boolean checkJavaFormatString(String str) {

        final String digits = "(\\p{Digit}+)";
        final String hexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String exp = "[eE][+-]?" + digits;

        final String fpRegex = "[\\x00-\\x20]*" // Optional leading "whitespace"
               + "[+-]?(" // Optional sign character
               +  "NaN|" // "NaN" string
               +  "Infinity|" // "Infinity" string


                //
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from the Java Language Specification, 2nd
                // edition, section 3.10.2.

                // digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                + "(" + digits + "(\\.)?(" + digits + "?)(" + exp + ")?"

                + "|"

                // . digits ExponentPart_opt FloatTypeSuffix_opt
                + "(\\." + digits + "(" + exp + ")?)"

                + "|"

                // Hexadecimal strings
                + "(("

                    // 0[xX] hexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                    + "(0[xX]" + hexDigits + "(\\.)?)|"

                    // 0[xX] HexDigits_opt . hexDigits BinaryExponent FloatTypeSuffix_opt
                    + "(0[xX]" + hexDigits + "?(\\.)" + hexDigits + ")"

                    // exclude any optional FloatTypeSuffixes in number string
                    //  ")[pP][+-]?" + digits + "))" + "[fFdD]?))" +
                    + ")[pP][+-]?" + digits + "))"

                // End of hexadecimal strings
                + ")"

                // Optional trailing "whitespace"
                + "[\\x00-\\x20]*";

        return Pattern.matches(fpRegex, str);
    }

    /**
     * Group thousands
     *
     * @param str initial string
     * @return string with grouping
     */
    public static String groupThousands(String str) {
        boolean negative = false;
        String result = str;

        if (result.startsWith(MINUS_SIGN)) {
            result = result.substring(1);
            negative = true;
        }
        StringBuilder sb = new StringBuilder();
        int dot = result.indexOf(DECIMAL_SEPARATOR);

        String trail = null;
        String ipart;
        // part to dot
        if (dot != -1) {
            ipart = result.substring(0, dot);
            trail = result.substring(dot + 1);
        } else {
            ipart = result;
        }

        // process string from the end
        for (int i = ipart.length() - 1, c = 1; i >= 0; i--, c++) {
            char ch = ipart.charAt(i);
            sb.append(ch);
            if (c % THOUSANDS_GROUP_LENGTH == 0 && i != 0) {
                sb.append(",");
            }
        }
        if (trail != null) {
            return (negative ? MINUS_SIGN : "") + sb.reverse() + DECIMAL_SEPARATOR + trail;
        } else {
            return (negative ? MINUS_SIGN : "") + sb.reverse();
        }
    }

    /**
     * Ungroup thousands
     *
     * @param str initial string
     * @return string without grouping
     */
    public static String ungroupThousands(String str) {
        boolean negative = false;
        String result = str;

        if (result.startsWith(MINUS_SIGN)) {
            result = result.substring(1);
            negative = true;
        }
        StringBuilder sb = new StringBuilder();
        int dot = result.indexOf(DECIMAL_SEPARATOR);

        // ipart.trail, for example 123.456
        String ipart;
        String trail = null;
        // part to dot
        if (dot != -1) {
            ipart = result.substring(0, dot);
            trail = result.substring(dot + 1);
        } else {
            ipart = result;
        }

        // process string from the end
        for (int i = ipart.length() - 1, c = 1; i >= 0; i--, c++) {
            char ch = ipart.charAt(i);
            if (i != 0 && c % THOUSANDS_SEPARATOR_POSITION == 0 && ch == THOUSANDS_SEPARATOR) {
                continue;
            }
            if (ch == THOUSANDS_SEPARATOR) {
                c--;
            } else {
                sb.append(ch);
            }
        }

        if (trail != null) {
            return (negative ? MINUS_SIGN : "") + sb.reverse() + DECIMAL_SEPARATOR + trail;
        } else {
            return (negative ? MINUS_SIGN : "") + sb.reverse();
        }
    }

    /**
     * This method validates the input and determines the sign of the number.
     * Whether the number is negative or not has no bearing on the number
     * of significant digits, however, it makes all the difference when
     * treating the number as a string, where it is convenient to be able
     * to assume it is positive (no leading minus sign).
     *
     * @param numberString input string
     * @return corrected and validated string, if validation failed
     * (string is not a number) returned null
     */
    @SuppressWarnings("checkstyle:ReturnCount")
    public static String correctAndValidateInput(String numberString) {

        if (numberString == null || numberString.trim().isEmpty()) {
            return null;
        } else {
            String result = numberString;
            try {
                /*
                 * Parse the string to know, if it is a digit.
                 */
                Double.parseDouble(result);
                result = result.trim();
                /*
                 * Eliminate leading minus or plus sign
                 */
                if (!result.isEmpty() && (result.charAt(0) == '-' || result.charAt(0) == '+')) {
                    result = result.substring(1);
                }
                /*
                 * Eliminate leading zeroes only if its length
                 * is more than 1 and number string has dot 00000.123 -> .123
                 */
                if (result.contains(DECIMAL_SEPARATOR)) {
                    while (result.length() > 1 &&  result.charAt(0) == '0') {
                        result = result.substring(1);
                    }
                }  else {
                    /*
                     * In case number has leading zeros and doesn't have
                     * dot - it is treated as decimal fraction 00345 -> 0.00345
                     */
                    if (!result.isEmpty() && result.charAt(0) == '0' && !result.equals(ZERO_STRING)) {
                        result = DECIMAL_SEPARATOR + result;
                    }
                }
                return cutTrailingZeros(result);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /**
     * Returns random sequence of numbers in 0 .. i limits
     *
     * @param i exclusive upper limit of sequence
     * @return random sequence of numbers in 0 .. i limits
     */
    public static int[] shuffle(int i) {
        /* zero filled array */
        int[] result = new int[i];
        /* array with ordered numbers */
        int[] source = new int[i];
        /* fill result array with random sequence */
        for (int k = 0; k < i; k++) {
            /* generate random value - position in source array */
            int v = MathUtils.rand.nextInt(i);
            /* if number in position is zero, take the next one */
            while (source[v] == -1) {
                v = (v < i - 1) ? (v + 1) : 0;
            }
            /* copy integer */
            result[k] = v;
            /* this cell is used */
            source[v] = -1;
        }
        return result;
    }
}
