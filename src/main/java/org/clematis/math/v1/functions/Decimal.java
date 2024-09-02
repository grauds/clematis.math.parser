// Created: 21.10.2003 T 10:59:38
package org.clematis.math.v1.functions;

import java.math.BigDecimal;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IExpressionItem;
import org.clematis.math.MathUtils;
import org.clematis.math.v1.algorithm.AlgorithmUtils;

/**
 * Returns x expressed as a floating-point number rounded to n decimal places.
 */
@SuppressWarnings("checkstyle:TodoComment")
/* TODO: refactor to reduce cyclomatic complexity, write unit tests for this class */
public class Decimal extends aFunction2 {

    public static final String MINUS_SIGNS = "-";
    public static final String DOT = ".";
    public static final String ZERO = "0";

    /**
     * Calculate a subtree of expression items
     *
     * @return expression item instance
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'Decimal': " + arguments.size());
            }
            IExpressionItem a1 = arguments.get(0).calculate();
            IExpressionItem a2 = arguments.get(1).calculate();
            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                Decimal retvalue = new Decimal();
                retvalue.setSignature("Decimal");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            int n = (int) c1.getNumber();
            // round to -n power of ten
            String formatted = Decimal.round(Double.toString(c2.getNumber()), (-1) * n);
            Constant retvalue = new Constant(formatted);
            retvalue.setMultiplier(getMultiplier());
            return retvalue;
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }

    /**
     * Rounds float numbers after dot. Does not support scientific notation
     *
     * @param cutStr     roughly cut initial string
     * @param initialStr initial string
     * @return rounded string
     */
    @SuppressWarnings({"checkstyle:ReturnCount", "checkstyle:NestedIfDepth"})
    protected static String addExtraDigit(String cutStr, String initialStr) {
        String result = cutStr;

        if (initialStr.length() > result.length()) {
            char next = initialStr.charAt(result.length());

            if (next == '.') {
                if (result.length() + 2 > initialStr.length()) {
                    return result;
                }
                next = initialStr.charAt(result.length() + 1);
            }

            boolean round = next > '4';

            if (round) {
                boolean negative = (result.charAt(0) == '-');
                if (negative) {
                    result = result.substring(1);
                }
                /*
                 * Recursively add one point to
                 * all chars from the end to
                 * the beginning of the string
                 */
                return (negative ? MINUS_SIGNS : "") + addExtraDigit(result);
            }
        }
        return result;
    }

    /**
     * Recursively add one point to all chars from the end to
     * the beginning of the string if flag is set
     *
     * @param str string
     * @return rounded result
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    private static String addExtraDigit(String str) {
        StringBuilder sb = new StringBuilder();
        boolean extraDigit = true;

        for (int i = str.length() - 1; i >= 0; i--) {
            char digit = str.charAt(i);
            /*
             * In case we have some digit to increase
             */
            if (MathUtils.isDigit(digit)) {
                if (extraDigit) {
                    if (digit == '9') {
                        digit = '0';
                    } else {
                        digit++;
                        extraDigit = false;
                    }
                }
            }
            sb.append(digit);
        }
        if (extraDigit) {
            sb.append("1");
        }

        String result = sb.reverse().toString();
        // get rid of dot at the end of the string, for example in "1."
        if (result.endsWith(DOT)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * Rounds float numbers. Supports scientific notation.
     * Place parameter means place relative to separating dot of mantissa,
     * and it is the power of 10, positive or negative.
     *
     * @param numberString initial string
     * @param place        relative to separating dot
     * @return rounded string
     */
    public static String round(String numberString, int place) {
        return round(numberString, place, false);
    }

    /**
     * Rounds float numbers. Supports scientific notation.
     * Place parameter means place relative to separating dot,
     * and it is the power of 10, positive or negative.
     *
     * @param numberString initial string
     * @param place        relative to separating dot
     * @param mantissa     this flag allows to take into account
     *                     tens power of scientific notation, if place is given relative to
     *                     absolute dot, not to dot of mantissa
     * @return rounded string
     */
    @SuppressWarnings({"checkstyle:CyclomaticComplexity",
        "checkstyle:ReturnCount",
        "checkstyle:ParameterAssignment",
        "checkstyle:MethodLength"
    })
    public static String round(String numberString, int place, boolean mantissa) {
        /*
         * Validate input
         */
        if (numberString == null || numberString.trim().isEmpty()) {
            return numberString;
        }
        /*
         * Find index of scientific "e" in input string and if
         * found, store exponent for future reference
         */
        int scientificIndex = numberString.toLowerCase().indexOf("e");
        /*
         * Store exponent
         */
        String exp = "";
        if (scientificIndex != -1) {
            /* get only mantissa */
            if (mantissa) {
                exp = numberString.substring(scientificIndex);
                numberString = numberString.substring(0, scientificIndex);
            } else {
                /* get common number notation, without exponent */
                BigDecimal bd = new BigDecimal(numberString);
                numberString = bd.toString();
            }
        }
        /*
         * Negative flag
         */
        boolean negative = numberString.charAt(0) == '-';
        /*
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        numberString = MathUtils.correctAndValidateInput(numberString);
        /*
         * Wrong input, return null
         */
        if (numberString == null) {
            return null;
        }
        /*
         * Get decimal point to find out the type of input number string
         */
        int decimalPointIndex = numberString.indexOf(DOT);
        /*
         * Apply algorithms depending on decimal point index
         */
        switch (decimalPointIndex) {
            case 0: {
                // If the number is decimal only: -1 < n < 1
                // place can be only negative, 1 based
                // .7542102
                if (place <= 0 && Math.abs(place) + 1 < numberString.length()) {

                    // shift to next char
                    char next = numberString.charAt(Math.abs(place) + 1);

                    // initial extra digit
                    boolean extraDigit = next > '4';

                    // cut number string
                    numberString = numberString.substring(0, Math.abs(place) + 1);

                    // addExtraDigit
                    if (extraDigit) {
                        numberString = addExtraDigit(numberString);
                    }

                    // if one point were added, reinitialize extra digit
                    extraDigit = !numberString.startsWith(DOT);

                    // cut trailing zeros
                    //numberString = cutTrailingsZeros( numberString );
                    if (numberString.trim().equals(DOT)) {
                        return ZERO;
                    }
                    // is resulting string empty?
                    boolean isEmpty = numberString.trim().isEmpty();
                    // if extra digit, do not reappend zero
                    if (extraDigit) {
                        return ((negative && !isEmpty) ? MINUS_SIGNS : "") + numberString + exp;
                    } else {
                        return ((negative && !isEmpty) ? MINUS_SIGNS : "") + ZERO + numberString + exp;
                    }
                }
                break;
            }
            case -1: {
                //If the number is an integer
                // place cannot be negative, zero based
                // 93242350
                if (place >= 0 && place < numberString.length()) {
                    // initial extra digit
                    boolean extraDigit = false;
                    // place greater than 0 and less that length - 1 to shift to next char
                    if (place > 0) {
                        char next = numberString.charAt(numberString.length() - place);
                        extraDigit = next > '4';
                    }
                    // cut number string
                    numberString = numberString.substring(0, numberString.length() - place);
                    // addExtraDigit
                    if (extraDigit) {
                        numberString = addExtraDigit(numberString);
                    }
                    // add cut zeros to the end - 1 - to zero based place
                    numberString = numberString + ZERO.repeat(place);
                    return (negative ? MINUS_SIGNS : "") + /*cutTrailingsZeros( */numberString /*)*/ + exp;
                }
                break;
            }
            default: {
                // If it is a real number with an integer and decimal value
                // place can be of any sign
                // 14523.12456
                if (place < decimalPointIndex && place > -(numberString.length() - decimalPointIndex - 1)) {
                    // initial extra digit
                    boolean extraDigit = false;
                    // shift to next char
                    int index = decimalPointIndex - ((place > 0) ? place : (place - 1));

                    char next = numberString.charAt(index);
                    if (next == '.') {
                        next = numberString.charAt(index + 1);
                    }

                    extraDigit = next > '4';

                    // cut number string
                    numberString = numberString.substring(0, index);

                    // addExtraDigit
                    if (extraDigit) {
                        numberString = addExtraDigit(numberString);
                    }
                    // if place is positive, make zerofilling
                    if (place > 0) {
                        numberString = numberString + ZERO.repeat(place);
                    }
                    if (numberString.endsWith(DOT)) {
                        numberString = numberString.substring(0, numberString.length() - 1);
                    }
                    return (negative ? MINUS_SIGNS : "") + numberString + exp;
                }
                break;
            }
        }
        // do not change number
        if (!numberString.startsWith(DOT)) {
            return (negative ? MINUS_SIGNS : "") +  /*cutTrailingsZeros(*/ numberString/* ) */ + exp;
        } else {
            return (negative ? MINUS_SIGNS : "") + ZERO + /*cutTrailingsZeros( */numberString /*)*/ + exp;
        }
    }
}
