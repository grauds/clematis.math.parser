// $Id: Sig.java,v 1.1 2009-04-27 07:11:10 anton Exp $
// Created: Feb 14, 2003 T 12:31:45 PM

package org.clematis.math.v1.functions;

import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.MathUtils;
import org.clematis.math.v1.algorithm.AlgorithmUtils;
import org.clematis.math.v1.iExpressionItem;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * <code>Sig(n, x)</code>
 * Returns x expressed as a floating point number to n significant digits.
 */
public class Sig extends aFunction2 {
    /**
     * Get number format locale
     */
    public static final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);

    /**
     * Implementation of <code>calculate()</code> method of the
     * <code>iExpressionItem</code> interface.
     * Returns first argument expressed as a floating point number with number of
     * significant digits specified by second argument.
     *
     * @return <code>Constant</code> with formatted value or null on error.
     */
    public iExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'Sig': " + arguments.size());
            }
            iExpressionItem a1 = arguments.get(0).calculate();
            iExpressionItem a2 = arguments.get(1).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                Sig retvalue = new Sig();
                retvalue.setSignature("Sig");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }
            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            int sig = (int) c1.getNumber();

            Constant retvalue = new Constant(c2.getNumber(), sig);
            retvalue.setMultiplier(getMultiplier());
            retvalue.setSdEnable(true);
            return retvalue;
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }

    //**** THE FOLLOWING CODE IS BORROWED FROM EXAMPLE JAVASCRIPT PAGE OF NEIL CASEY ****

    /**
     * Validates double number to have a given number of significant digits
     * <p>
     * Note: zero number - 0 is considered to have one significant digit
     * and cannot be mutated to have more or less ones.
     *
     * @param numberString   given number string
     * @param digitsRequired number of significant digits
     * @return true if this double has these significant digits
     */
    public static boolean validate(String numberString, int digitsRequired) {
        /**
         * If number is zero, it has 1 sig digit
         */
        if (MathUtils.isZero(numberString)) {
            return true;
        }
        /**
         * Indices for the number string
         */
        int decimalPointIndex = 0;
        int mostSignificantDigitIndex = 0;
        int leastSignificantDigitIndex = 0;
        /**
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        numberString = MathUtils.correctAndValidateInput(numberString);
        /**
         * Wrong input, return null
         */
        if (numberString == null) {
            return false;
        }
        /**
         * Find index of scientific "e" in input string
         */
        int scientificIndex = numberString.toLowerCase().indexOf("e");
        if (scientificIndex != -1) {
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
                for (int i = 1; i < numberString.length(); i++) {
                    if (numberString.charAt(i) != '0') {
                        mostSignificantDigitIndex = i;
                        break;
                    }
                }
                //discards all zeroes found before MSD and stores MSD index.
                int significantDigits = numberString.substring(mostSignificantDigitIndex).length();
                if (significantDigits == digitsRequired) {
                    return true;
                }
                break;
            case -1: //If the number is an integer
                for (int i = numberString.length() - 1; i >= 0; i--) {
                    if (numberString.charAt(i) != '0') {
                        leastSignificantDigitIndex = i;
                        break;
                    }
                }
                //discards all zeroes found after LSD and stores LSD index.
                significantDigits = leastSignificantDigitIndex + 1;
                if (significantDigits == digitsRequired) {
                    return true;
                } else if (significantDigits < digitsRequired &&
                    numberString.length() >= digitsRequired) {
                    //if the integer has the correct number of figures but has
                    //too few nonzero digits, it is POSSIBLE that the number is
                    //being expressed correctly, however it is also possible that
                    //the data is incorrect. For example, if there are 3 significant
                    //digits required and the user enters "200," there may be 3
                    //significant digits, or there may be only one, or two -- one
                    //or both of the zeroes may be mere placeholders. Because of this
                    //ambiguity, the "maybe" condition is returned.
                    return true;
                }
                break;
            default: //If it is a real number with an integer and decimal value
                significantDigits = numberString.length() - 1;
                if (significantDigits == digitsRequired) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     * Returns significant digits from numbers like
     * <p>
     * 0.812938...
     *
     * @param numberString   input string, cut to .812938
     * @param digitsRequired required digits
     * @return cut string with required digits
     */
    private static String getSigDigitsDecimal(String numberString, int digitsRequired) {
        /**
         * Start count flag
         */
        boolean startCount = false;
        /**
         * Resulting string
         */
        StringBuilder sb = new StringBuilder();
        sb.append("0.");
        int counter = 0;
        /**
         * Here numberString is cut to .[input number]
         */
        for (int i = 1; i < numberString.length(); i++) {
            if (numberString.charAt(i) != '0') {
                startCount = true;
            }
            if (startCount) {
                sb.append(numberString.charAt(i));
                counter++;
                if (counter == digitsRequired) {
                    break;
                }
            } else {
                sb.append("0");
            }
        }
        if (counter < digitsRequired) {
            //if there are too few significant digits, this code adds trailing
            //zeroes to the number to the correct number of digits.
            for (int i = 0; i < digitsRequired - counter; i++) {
                sb.append("0");
            }
        }
        return sb.toString();
    }

    /**
     * Returns significant digits from numbers like
     * <p>
     * 15489.812938...
     *
     * @param numberString   input string
     * @param digitsRequired required digits
     * @return cut string with required digits
     */
    private static String getSigDigitsFloat(String numberString, int digitsRequired) {
        StringBuilder sb = new StringBuilder();
        boolean foundDot = false;
        boolean round = false;
        int counter = 0;
        for (int i = 0; i < numberString.length(); i++) {
            char ch = numberString.charAt(i);
            if (ch == '.') {
                foundDot = true;

                if (counter == digitsRequired) {
                    break;
                }
            } else {
                counter++;
            }

            if (counter > digitsRequired) {
                if (foundDot) {
                    break;
                } else if (!round) {
                    String temp = sb.toString();
                    temp = Decimal.addExtraDigit(temp, numberString);
                    sb = new StringBuilder();
                    sb.append(temp);
                    round = true;
                    ch = '0';
                } else {
                    ch = '0';
                }
            }

            sb.append(ch);
        }
        if (counter < digitsRequired) {
            //if there are too few significant digits, this code adds trailing
            //zeroes to the number to the correct number of digits.
            for (int i = 0; i < digitsRequired - counter; i++) {
                sb.append("0");
            }
        }

        if (round) {
            return sb.toString();
        } else {
            return Decimal.addExtraDigit(sb.toString(), numberString);
        }
    }

    /**
     * Cut number string to required number of significant digits
     * <p>
     * Note: zero number - 0 is considered to have one significant digit
     * and cannot be mutated to have more or less ones.
     *
     * @param numberString   number string
     * @param digitsRequired required number of significant digits
     * @return resulting number string
     */
    public static String getSigDigits(String numberString, int digitsRequired) {
        /**
         * Validate input
         */
        if (digitsRequired <= 0 || numberString == null || numberString.trim().equals("")) {
            return numberString;
        }
        /**
         * If number is zero, it has 1 sig digit
         */
        if (MathUtils.isZero(numberString)) {
            return "0";
        }
        /**
         * Resulting cut string
         */
        String cutString = null;
        /**
         * Negative flag
         */
        boolean negative = numberString.charAt(0) == '-';
        /**
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        numberString = MathUtils.correctAndValidateInput(numberString);
        /**
         * Wrong input, return null
         */
        if (numberString == null) {
            return null;
        }
        /**
         * Find index of scientific "e" in input string and if
         * found, store exponent for future reference
         */
        int scientificIndex = numberString.toLowerCase().indexOf("e");
        String exp = "";
        if (scientificIndex != -1) {
            exp = numberString.substring(scientificIndex);
            numberString = numberString.substring(0, scientificIndex);
        }
        /**
         * Get decimal point to find out the type of input number string
         */
        int decimalPointIndex = numberString.indexOf(".");
        /**
         * Apply algorithms depending on decimal point index
         */
        switch (decimalPointIndex) {
            case 0: //If the number is decimal only: -1 < n < 1
            {
                cutString = getSigDigitsDecimal(numberString, digitsRequired);
                /**
                 * Restore 0 before .[input number]
                 */
                numberString = "0" + numberString;
                numberString = Decimal.addExtraDigit(cutString, numberString) + exp;
                if (negative) {
                    return "-" + numberString;
                } else {
                    return numberString;
                }
            }
            case -1: //If the number is an integer
            {
                if (digitsRequired <= numberString.length()) {
                    if (scientificIndex != -1) {
                        /**
                         * Before cutting the number we should increment
                         * exp by difference between required digits and
                         * length of number string
                         */
                        int diff = numberString.length() - digitsRequired;
                        int iexp = 0;
                        if (exp != null && exp.trim().length() > 1) {
                            iexp = Integer.parseInt(exp.substring(1));
                        }
                        iexp += diff;
                        if (iexp != 0) {
                            exp = "E" + iexp;
                        } else {
                            exp = "";
                        }
                        cutString = numberString.substring(0, digitsRequired);
                    } else {
                        int diff = numberString.length() - digitsRequired;
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < diff; i++) {
                            sb.append("0");
                        }
                        cutString = numberString.substring(0, digitsRequired);
                        if (negative) {
                            cutString = "-" + Decimal.addExtraDigit(cutString, numberString);
                        } else {
                            cutString = Decimal.addExtraDigit(cutString, numberString);
                        }
                        cutString += sb.toString();
                    }
                } else {
                    //if there are too few significant digits, this code adds trailing
                    //zeroes to the number till the correct number of digits.
                    StringBuilder sb = new StringBuilder();
                    sb.append(numberString);
                    sb.append(".");
                    for (int i = 0; i < digitsRequired - numberString.length(); i++) {
                        sb.append("0");
                    }
                    cutString = sb.toString();
                }
                return cutString + exp;
            }
            default: //If it is a real number with an integer and decimal value
            {
                if (negative) {
                    cutString = "-" + getSigDigitsFloat(numberString, digitsRequired);
                } else {
                    cutString = getSigDigitsFloat(numberString, digitsRequired);
                }
            }
            return cutString + exp;
        }
    }

    /**
     * Returns first argument of Sig function
     *
     * @return first argument of Sig function
     */
    public int getSigDigits() {
        if (arguments != null && arguments.size() == 2) {
            iExpressionItem sigArg = arguments.get(0);
            if (sigArg instanceof Constant) {
                return (int) ((Constant) sigArg).getNumber();
            }
        }
        // undefined state
        return -1;
    }

    /**
     * Count significant digits
     *
     * @param numberString a number in which it is nessesary to count
     */
    public static int countSigDigits(String numberString) {
        int mostSignificantDigitIndex = 0;
        int leastSignificantDigitIndex = 0;
        /**
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        numberString = MathUtils.correctAndValidateInput(numberString);
        /**
         * Wrong input, return -1
         */
        if (numberString == null) {
            return -1;
        }
        /**
         * Find index of scientific "e" in input string and if
         * found, cut off
         */
        int scientificIndex = numberString.toLowerCase().indexOf("e");
        if (scientificIndex != -1) {
            numberString = numberString.substring(0, scientificIndex);
        }
        /**
         * Get decimal point to find out the type of input number string
         */
        int decimalPointIndex = numberString.indexOf(".");

        switch (decimalPointIndex) {
            case 0: //If the number is decimal only: -1 < n < 1
                for (int i = 1; i < numberString.length(); i++) {
                    if (numberString.charAt(i) != '0') {
                        mostSignificantDigitIndex = i;
                        break;
                    }
                }
                //discards all zeroes found before MSD and stores MSD index.
                return numberString.substring(mostSignificantDigitIndex).length();
            case -1: //If the number is an integer
                for (int i = numberString.length() - 1; i >= 0; i--) {
                    if (numberString.charAt(i) != '0') {
                        leastSignificantDigitIndex = i;
                        break;
                    }
                }
                //discards all zeroes found after LSD and stores LSD index.
                return leastSignificantDigitIndex + 1;
            default: //If it is a real number with an integer and decimal value
                return numberString.length() - 1;
        }
    }
}
