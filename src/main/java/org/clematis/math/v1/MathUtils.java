// $Id: MathUtils.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Feb 12, 2003 T 10:48:45 AM

package org.clematis.math.v1;

import org.clematis.math.v2.io.InputFormatSettings;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * A collection of math utils.
 */
public class MathUtils {
    /**
     * Get number format locale
     */
    public static final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
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
            /**
             * Parse as double, as it may be notation like 1542.000
             */
            Double d = new Double(str);
            return d.intValue() == d.doubleValue();
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isPlainInteger(String str) {
        return MathUtils.isInteger(str) && str.toLowerCase().indexOf("e") == -1;
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
     * Cut trailing zeros like ".00000" to ".0",
     * ".9067000" to ".9067", thus making number more
     * beautiful.
     *
     * @param str initial number string
     * @return resulting number string
     */
    public static String cutTrailingsZeros(String str) {
        if (str == null) {
            return "0";
        }

        if (str.indexOf(".") != -1 && str.indexOf("E") == -1 && str.indexOf("e") == -1) {
            if (str.endsWith(".")) {
                str = str.substring(0, str.length() - 1);
            }

            while (str.endsWith("0")) {
                if (str.endsWith(".0")) {
                    str = str.substring(0, str.length() - 2);
                    break;
                }
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }

    /**
     * Parse string in smart way
     *
     * @param str incoming string
     * @return Number
     * @throws NumberFormatException if string is not a number
     */
    public static double parse(String str, InputFormatSettings formatSettings)
        throws NumberFormatException {
        str = formatSettings.prepareToParse(str);
        if (checkJavaFormatString(str)) {
            return Double.parseDouble(str);
        } else {
            throw new NumberFormatException("Not a valid number: " + str);
        }
    }

    private static boolean checkJavaFormatString(String str) {
        final String Digits = "(\\p{Digit}+)";
        final String HexDigits = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp = "[eE][+-]?" + Digits;
        final String fpRegex =
            "[\\x00-\\x20]*" +  // Optional leading "whitespace"
                "[+-]?(" + // Optional sign character
                "NaN|" +           // "NaN" string
                "Infinity|" +      // "Infinity" string

                // A decimal floating-point string representing a finite positive
                // number without a leading sign has at most five basic pieces:
                // Digits . Digits ExponentPart FloatTypeSuffix
                //
                // Since this method allows integer-only strings as input
                // in addition to strings of floating-point literals, the
                // two sub-patterns below are simplifications of the grammar
                // productions from the Java Language Specification, 2nd
                // edition, section 3.10.2.

                // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                "(((" + Digits + "(\\.)?(" + Digits + "?)(" + Exp + ")?)|" +

                // . Digits ExponentPart_opt FloatTypeSuffix_opt
                "(\\.(" + Digits + ")(" + Exp + ")?)|" +

                // Hexadecimal strings
                "((" +
                // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "(\\.)?)|" +

                // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                // exclude any optional FloatTypeSuffixes in number string
                //  ")[pP][+-]?" + Digits + "))" + "[fFdD]?))" +
                ")[pP][+-]?" + Digits + "))" + "))" +
                "[\\x00-\\x20]*";// Optional trailing "whitespace"

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
        if (str.startsWith("-")) {
            str = str.substring(1);
            negative = true;
        }
        StringBuilder sb = new StringBuilder();
        int dot = str.indexOf(".");
        String trail = null;
        String ipart = null;
        // part to dot
        if (dot != -1) {
            ipart = str.substring(0, dot);
            trail = str.substring(dot + 1);
        } else {
            ipart = str;
        }
        // process string from the end
        for (int i = ipart.length() - 1, c = 1; i >= 0; i--, c++) {
            char ch = ipart.charAt(i);
            sb.append(ch);
            if (c % 3 == 0 && i != 0) {
                sb.append(",");
            }
        }
        if (trail != null) {
            return (negative ? "-" : "") + sb.reverse() + "." + trail;
        } else {
            return (negative ? "-" : "") + sb.reverse();
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
        if (str.startsWith("-")) {
            str = str.substring(1);
            negative = true;
        }
        StringBuilder sb = new StringBuilder();
        int dot = str.indexOf(".");
        String trail = null;
        String ipart = null;
        // part to dot
        if (dot != -1) {
            ipart = str.substring(0, dot);
            trail = str.substring(dot + 1);
        } else {
            ipart = str;
        }
        // process string from the end
        for (int i = ipart.length() - 1, c = 1; i >= 0; i--, c++) {
            char ch = ipart.charAt(i);
            if (i != 0 && c % 4 == 0 && ch == ',') {
                continue;
            }
            if (ch == ',') {
                c--;
            }
            sb.append(ch);
        }
        if (trail != null) {
            return (negative ? "-" : "") + sb.reverse() + "." + trail;
        } else {
            return (negative ? "-" : "") + sb.reverse();
        }
    }

    /**
     * Correct and validate input of number string.
     *
     * @param numberString input string
     * @return corrected and validated string, if validation failed
     * (string is not a number) returned null
     */
    public static String correctAndValidateInput(String numberString) {
        //This section validates the input and determines the sign of the number.
        //Whether the number is negative or not has no bearing on the number
        //of significant digits, however, it makes all the difference when
        //treating the number as a string, where it is convenient to be able
        //to assume it is positive (no leading minus sign).
        if (numberString == null || numberString.trim().equals("")) {
            return null;
        } else {
            try {
                /**
                 * Parse the string to know, if it is a digit.
                 */
                Double.parseDouble(numberString);
                //Eliminate leading minus or plus sign
                numberString = numberString.trim();
                if (numberString.length() > 0 &&
                    (numberString.charAt(0) == '-' || numberString.charAt(0) == '+')) {
                    numberString = numberString.substring(1);
                }
                //Eliminate leading zeroes only if its length
                //is more than 1 and number string has dot
                if (numberString.indexOf(".") != -1) {
                    while (numberString.length() > 1 &&
                        numberString.charAt(0) == '0') {
                        numberString = numberString.substring(1);
                    }
                }
                /**
                 * In case number has leading zeros and doesnt have
                 * dot - it is treated as decimal fraction
                 * 00345 -> 0.00345
                 */
                else {
                    if (numberString.length() > 0 && numberString.charAt(0) == '0' && !numberString.equals("0")) {
                        numberString = "." + numberString;
                    }
                }

                return numberString;
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
        /** zero filled array */
        int[] result = new int[i];
        /** array with ordered numbers */
        int[] source = new int[i];
        /** fill result array with random sequence */
        for (int k = 0; k < i; k++) {
            /** generate random value - position in source array */
            int v = MathUtils.rand.nextInt(i);
            /** if number in position is zero, take next one */
            while (source[v] == -1) {
                v = (v < i - 1) ? (v + 1) : 0;
            }
            /** copy integer */
            result[k] = v;
            /** this cell is used */
            source[v] = -1;
        }
        return result;
    }
}
