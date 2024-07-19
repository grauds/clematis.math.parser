// Created: Feb 18, 2003 T 6:04:16 PM
package org.clematis.math.functions;

import org.clematis.math.Constant;
import org.clematis.math.algorithm.AlgorithmException;
import org.clematis.math.algorithm.iParameterProvider;
import org.clematis.math.io.NumberFormatter;
import org.clematis.math.parsers.Node;
import org.clematis.math.utils.AlgorithmUtils;
import org.clematis.math.utils.MathUtils;

/**
 * lsu( n, x )
 * Returns the least significant unit of x in the n'th place.
 */
public class Lsu extends aFunction2 {
    /**
     * Implementation of <code>calculate()</code> method of the
     * <code>Node</code> interface.
     * Returns least significant unit of second argument in the place specified
     * by first argument.
     *
     * @param parameterProvider
     * @return <code>Constant</code> with formatted value or null on error.
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        try {
            if (getArguments().size() <= 1 || getArguments().size() > 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'Lsu': " + getArguments().size());
            }

            Node a1 = getArguments().get(0).calculate(parameterProvider);
            Node a2 = getArguments().get(1).calculate(parameterProvider);

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                Lsu retvalue = new Lsu();
                retvalue.setSignature("Lsu");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            int sig = (int) c1.getNumber();
            double newValue = Lsu.getLSU(c2.getNumber(), sig);
            return new Constant(newValue);
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + getSignature() + " due to " + ex);
        }
    }

    private static double getLSU(double digit, int sigDigits) {
        return getLSU(correctPlainInteger(digit), sigDigits);
    }

    public static double getLSU(String stringDigit, int sigDigits) {
        String roundedDigit = Sig.getSigDigits(stringDigit, sigDigits);
        int power = Lsu.getLsuPower(roundedDigit, sigDigits);
        return Math.pow(10, power);
    }

    public static int getLsuPower(double number) {
        return getLsuPower(correctPlainInteger(number));
    }

    private static int getLsuPower(String numberString, int sigDigits) {
        /**
         * Indices for the number string
         */
        int decimalPointIndex = 0;
        /**
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        numberString = NumberFormatter.correctAndValidateInput(numberString);
        /**
         * Wrong input, return zero
         */
        if (numberString == null) {
            return 0;
        }
        /**
         * Find index of scientific "e" in input string and if
         * found, store exponent for future reference
         */
        int scientificIndex = numberString.toLowerCase().indexOf("e");
        String exp = "";
        if (scientificIndex != -1) {
            exp = numberString.substring(scientificIndex + 1);
            if (exp.startsWith("+")) {
                exp = exp.substring(1);
            }
            numberString = numberString.substring(0, scientificIndex);
        }
        /**
         * Get decimal point to find out the type of input number string
         */
        decimalPointIndex = numberString.indexOf(".");
        /**
         * Apply algorithms depending on decimal point index
         */
        switch (decimalPointIndex) {
            case 0: //If the number is decimal only: -1 < n < 1
            {
                // find non zero digit
                boolean foundNonZero = false;
                for (int i = 1; i < numberString.length(); i++) {
                    if (numberString.charAt(i) != '0') {
                        //mostSignificantDigitIndex = i;
                        foundNonZero = true;
                        break;
                    }
                }
                /**
                 * Return - MSDI + exp, ie, 0.0876 ->  10^(-4)
                 */
                if (foundNonZero) {
                    return -(numberString.length() - 1) + (exp.equals("") ? 0 : Integer.parseInt(exp));
                } else {
                    return (exp.equals("") ? 0 : Integer.parseInt(exp));
                }
            }
            case -1: //If the number is an integer
            {
                /**
                 * Return LENGTH - sigDigits - 1 + exp, ie, 1456000 -> 10^(3)
                 */
                return numberString.length() - sigDigits + (exp.equals("") ? 0 : Integer.parseInt(exp));
            }
            default: {
                return -(numberString.length() - (decimalPointIndex + 1)) +
                    (exp.equals("") ? 0 : Integer.parseInt(exp));
            }
        }
    }

    private static int getLsuPower(String numberString) {
        /**
         * Indices for the number string
         */
        int decimalPointIndex = 0;
        int leastSignificantDigitIndex = 0;
        /**
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        numberString = NumberFormatter.correctAndValidateInput(numberString);
        /**
         * Wrong input, return zero
         */
        if (numberString == null) {
            return 0;
        }
        /**
         * Find index of scientific "e" in input string and if
         * found, store exponent for future reference
         */
        int scientificIndex = numberString.toLowerCase().indexOf("e");
        String exp = "";
        if (scientificIndex != -1) {
            exp = numberString.substring(scientificIndex + 1);
            if (exp.startsWith("+")) {
                exp = exp.substring(1);
            }
            numberString = numberString.substring(0, scientificIndex);
        }
        /**
         * Get decimal point to find out the type of input number string
         */
        decimalPointIndex = numberString.indexOf(".");
        /**
         * Apply algorithms depending on decimal point index
         */
        switch (decimalPointIndex) {
            case 0: //If the number is decimal only: -1 < n < 1
            {
                // find non zero digit
                boolean foundNonZero = false;
                for (int i = 1; i < numberString.length(); i++) {
                    if (numberString.charAt(i) != '0') {
                        //mostSignificantDigitIndex = i;
                        foundNonZero = true;
                        break;
                    }
                }
                /**
                 * Return - MSDI + exp, ie, 0.0876 ->  10^(-4)
                 */
                if (foundNonZero) {
                    return -(numberString.length() - 1) + (exp.equals("") ? 0 : Integer.parseInt(exp));
                } else {
                    return (exp.equals("") ? 0 : Integer.parseInt(exp));
                }
            }
            case -1: //If the number is an integer
            {
                for (int i = numberString.length() - 1; i >= 0; i--) {
                    if (numberString.charAt(i) != '0') {
                        leastSignificantDigitIndex = i;
                        break;
                    }
                }
                /**
                 * Return LENGTH - LSDI - 1 + exp, ie, 1456000 -> 10^(3)
                 */
                return numberString.length() - leastSignificantDigitIndex - 1 +
                    (exp.equals("") ? 0 : Integer.parseInt(exp));
            }
            default: {
                return -(numberString.length() - (decimalPointIndex + 1)) +
                    (exp.equals("") ? 0 : Integer.parseInt(exp));
            }
        }
    }

    /**
     * todo correct this
     *
     * @param digit
     */
    private static String correctPlainInteger(double digit) {
        String str = Double.toString(digit);
        if (MathUtils.isPlainInteger(str)) {
            str = str.substring(0, str.indexOf("."));
        }
        return str;
    }
}
