// Created: Feb 12, 2003 T 10:48:45 AM
package org.clematis.math.v2.utils;

import org.clematis.math.v2.io.InputFormatSettings;
import org.clematis.math.v2.io.NumberFormatter;

import java.math.BigDecimal;
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

    /**
     * Shuffled numbers from array should be put down as comma-separated string
     *
     * @param randomNumbers
     * @return comma-separated string
     */
    public static String shuffledNumbersToString(int[] randomNumbers) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < randomNumbers.length; i++) {
            sb.append(randomNumbers[i]);
            if (i < randomNumbers.length - 1) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    /**
     * Convert rational number to indicated division
     *
     * @param rationalNumber to convert
     * @return indicated division
     */
    public static String convertRational(String rationalNumber) {
        if (rationalNumber == null || rationalNumber.trim().equals("")) {
            return null;
        } else {
            try {
                /**
                 * Negative
                 */
                boolean isNegative = rationalNumber.startsWith("-");
                /**
                 * Correct and validate input
                 */
                rationalNumber = NumberFormatter.correctAndValidateInput(rationalNumber);
                /**
                 * The number of decimal places after dot
                 */
                int decimalPlaces = 0;
                /**
                 * In case the number has dot
                 */
                if (rationalNumber.indexOf(".") != -1) {
                    decimalPlaces = rationalNumber.length() - rationalNumber.indexOf(".") - 1;
                }
                /**
                 * Now we got decimal places -> get numenator and denominator
                 */
                rationalNumber = rationalNumber.replace(".", "");
                BigDecimal numenator = new BigDecimal(rationalNumber);
                BigDecimal denominator = BigDecimal.valueOf(Math.pow(10, decimalPlaces));
                /**
                 * Find greatest common divisor to simplify
                 */
                BigDecimal gcd = MathUtils.getGreatestCommonDivisor(numenator, denominator);
                /**
                 * Simplify
                 */
                if (!gcd.equals(new BigDecimal(0)) && !gcd.equals(new BigDecimal(1))) {
                    numenator = numenator.divide(gcd);
                    denominator = denominator.divide(gcd);
                }
                if (denominator.longValueExact() != 1) {
                    return (isNegative ? "-" : "") + numenator.toPlainString() + "/" + denominator.toPlainString();
                } else {
                    return (isNegative ? "-" : "") + numenator.toPlainString();
                }
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    /**
     * This method takes advantage of Euclid's algorithm to find greatest common divisor
     * <p>
     * In Euclid's Elements (Book VII) we find a way of calculating the gcd of two numbers,
     * without listing the divisors of either number. It is now called Euclid's Algorithm.
     * [An algorithm is a step by step process (or recipe) for doing something.]
     * General example:
     * <p>
     * a/b gives a remainder of r
     * b/r gives a remainder of s
     * r/s gives a remainder of t
     * ...
     * w/x gives a remainder of y
     * x/y gives no remainder
     * <p>
     * In this case, y is the gcd of a and b. If the first step produced no remainder,
     * then b (the lesser of the two numbers) is the gcd.
     *
     * @param divident integer number
     * @param divisor  integer number
     * @return greatest common divisor
     */
    public static long getGreatestCommonDivisor(long divident, long divisor) {
        long remainder = divident;

        while (remainder != 0) {
            remainder = divident % divisor;
            divident = divisor;
            divisor = remainder;
        }

        return divident;
    }

    /**
     * This method takes advantage of Euclid's algorithm to find greatest common divisor
     * Capable of carrying out big numbers
     * <p>
     * In Euclid's Elements (Book VII) we find a way of calculating the gcd of two numbers,
     * without listing the divisors of either number. It is now called Euclid's Algorithm.
     * [An algorithm is a step by step process (or recipe) for doing something.]
     * General example:
     * <p>
     * a/b gives a remainder of r
     * b/r gives a remainder of s
     * r/s gives a remainder of t
     * ...
     * w/x gives a remainder of y
     * x/y gives no remainder
     * <p>
     * In this case, y is the gcd of a and b. If the first step produced no remainder,
     * then b (the lesser of the two numbers) is the gcd.
     *
     * @param divident integer number
     * @param divisor  integer number
     * @return greatest common divisor
     */
    public static BigDecimal getGreatestCommonDivisor(BigDecimal divident, BigDecimal divisor) {
        BigDecimal remainder = divident;

        while (!remainder.equals(new BigDecimal(0))) {
            remainder = divident.divideAndRemainder(divisor)[1];
            divident = divisor;
            divisor = remainder;
        }

        return divident;
    }
}
