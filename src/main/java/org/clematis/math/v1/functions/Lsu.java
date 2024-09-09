// Created: Feb 18, 2003 T 6:04:16 PM
package org.clematis.math.v1.functions;

import java.math.BigDecimal;

import static org.clematis.math.MathUtils.DECIMAL_SEPARATOR;
import static org.clematis.math.MathUtils.E;
import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.MathUtils;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.utils.AlgorithmUtils;

/**
 * lsu( n, x )
 * Returns the least significant unit of x in the n'th place.
 */
public class Lsu extends AbstractMathMLFunction {
    /**
     * Implementation of <code>calculate()</code> method of the
     * <code>iExpressionItem</code> interface.
     * Returns least significant unit of second argument in the place specified
     * by first argument.
     *
     * @return <code>Constant</code> with formatted value or null on error.
     */
    public IExpressionItem calculate() throws AlgorithmException {
        try {
            if (arguments.size() != 2) {
                throw new AlgorithmException("Invalid number of arguments in function 'Lsu': " + arguments.size());
            }

            IExpressionItem a1 = arguments.get(0).calculate();
            IExpressionItem a2 = arguments.get(1).calculate();

            if (!AlgorithmUtils.isGoodNumericArgument(a1) || !AlgorithmUtils.isGoodNumericArgument(a2)) {
                Lsu retvalue = new Lsu();
                retvalue.setSignature("Lsu");
                retvalue.addArgument(a1);
                retvalue.addArgument(a2);
                retvalue.setMultiplier(retvalue.getMultiplier() * getMultiplier());
                return retvalue;
            }

            Constant c1 = AlgorithmUtils.getNumericArgument(a1);
            Constant c2 = AlgorithmUtils.getNumericArgument(a2);
            int sig = (int) c1.getNumber();
            double newValue = Lsu.getLSU(c2.getNumber(), sig);
            return new Constant(newValue * getMultiplier());
        } catch (AlgorithmException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AlgorithmException("Failed calculation in " + signature + " due to " + ex);
        }
    }

    private static double getLSU(double digit, int sigDigits) {
        return getLSU(correctPlainInteger(digit), sigDigits);
    }

    public static double getLSU(String stringDigit, int sigDigits) {
        String roundedDigit = Sig.formatWithSigDigits(stringDigit, sigDigits);
        int power = Lsu.getLsuPower(roundedDigit, sigDigits);
        return Math.pow(BigDecimal.TEN.intValue(), power);
    }

    public static int getLsuPower(double number) {
        return getLsuPower(correctPlainInteger(number));
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private static int getLsuPower(String numberString, int sigDigits) {
        /*
         * Indices for the number string
         */
        int decimalPointIndex;
        /*
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        String number = MathUtils.correctAndValidateInput(numberString);
        /*
         * Wrong input, return zero
         */
        if (number == null) {
            return 0;
        }
        /*
         * Find index of scientific "e" in input string and if
         * found, store exponent for future reference
         */
        int scientificIndex = number.toLowerCase().indexOf(E);
        String exp = "";
        if (scientificIndex != -1) {
            exp = number.substring(scientificIndex + 1);
            number = number.substring(0, scientificIndex);
        }
        /*
         * Get decimal point to find out the type of input number string
         */
        decimalPointIndex = number.indexOf(DECIMAL_SEPARATOR);
        /*
         * Apply algorithms depending on decimal point index
         */
        switch (decimalPointIndex) {
            case 0: {
                //If the number is decimal only: -1 < n < 1
                // find non zero digit
                boolean foundNonZero = false;
                for (int i = 1; i < number.length(); i++) {
                    if (number.charAt(i) != '0') {
                        //mostSignificantDigitIndex = i;
                        foundNonZero = true;
                        break;
                    }
                }
                /*
                 * Return - MSDI + exp, ie, 0.0876 ->  10^(-4)
                 */
                int i = exp.isEmpty() ? 0 : Integer.parseInt(exp);
                if (foundNonZero) {
                    return -(number.length() - 1) + i;
                } else {
                    return i;
                }
            }
            case -1: {
                //If the number is an integer
                /*
                 * Return LENGTH - sigDigits - 1 + exp, ie, 1456000 -> 10^(3)
                 */
                return number.length() - sigDigits + (exp.isEmpty() ? 0 : Integer.parseInt(exp));
            }
            default: {
                // find non zero digit
                //    boolean foundNonZero = false;
              /*      for(int i=decimalPointIndex + 1; i < numberString.length() ; i++)
                    {
                       if ( numberString.charAt(i) != '0' )
                       {
                         // mostSignificantDigitIndex = i - decimalPointIndex;
                           foundNonZero = true;
                           break;
                       }
                    }     */
                /*
                 * Return - MSDI + exp, ie, 9870.0876 ->  10^(-4)
                 */
                /*    if ( foundNonZero )
                    {*/
                return -(number.length() - (decimalPointIndex + 1))
                    + (exp.isEmpty() ? 0 : Integer.parseInt(exp));
               /*     }
                    else
                    {
                        return (exp.equals("") ? 0 : Integer.parseInt(exp));
                    }    */
            }
        }
    }

    @SuppressWarnings("checkstyle:ReturnCount")
    private static int getLsuPower(String numberString) {
        /*
         * Indices for the number string
         */
        int decimalPointIndex;
        int leastSignificantDigitIndex = 0;
        /*
         * Trim leading zeroes and validate input string as a number.
         * Note, that numbers like 0.1226 become .1226
         */
        String number = MathUtils.correctAndValidateInput(numberString);
        /*
         * Wrong input, return zero
         */
        if (number == null) {
            return 0;
        }
        /*
         * Find index of scientific "e" in input string and if
         * found, store exponent for future reference
         */
        int scientificIndex = number.toLowerCase().indexOf("e");
        String exp = "";
        if (scientificIndex != -1) {
            exp = number.substring(scientificIndex + 1);
            number = number.substring(0, scientificIndex);
        }
        /*
         * Get decimal point to find out the type of input number string
         */
        decimalPointIndex = numberString.indexOf(DECIMAL_SEPARATOR);
        /*
         * Apply algorithms depending on decimal point index
         */
        switch (decimalPointIndex) {
            case 0: {
                //If the number is decimal only: -1 < n < 1
                // find non zero digit
                boolean foundNonZero = false;
                for (int i = 1; i < numberString.length(); i++) {
                    if (numberString.charAt(i) != '0') {
                        //mostSignificantDigitIndex = i;
                        foundNonZero = true;
                        break;
                    }
                }
                /*
                 * Return - MSDI + exp, ie, 0.0876 ->  10^(-4)
                 */
                if (foundNonZero) {
                    return -(numberString.length() - 1) + (exp.isEmpty() ? 0 : Integer.parseInt(exp));
                } else {
                    return (exp.isEmpty() ? 0 : Integer.parseInt(exp));
                }
            }
            case -1: {
                //If the number is an integer
                for (int i = numberString.length() - 1; i >= 0; i--) {
                    if (numberString.charAt(i) != '0') {
                        leastSignificantDigitIndex = i;
                        break;
                    }
                }
                /*
                 * Return LENGTH - LSDI - 1 + exp, ie, 1456000 -> 10^(3)
                 */
                return numberString.length() - leastSignificantDigitIndex - 1
                    + (exp.isEmpty() ? 0 : Integer.parseInt(exp));
            }
            default: {
                // find non zero digit
           /*         boolean foundNonZero = false;
                    for(int i=decimalPointIndex + 1; i < numberString.length() ; i++)
                    {
                       if ( numberString.charAt(i) != '0' )
                       {
                         // mostSignificantDigitIndex = i - decimalPointIndex;
                           foundNonZero = true;
                           break;
                       }
                    }    */
                /*
                 * Return - MSDI + exp, ie, 9870.0876 ->  10^(-4)
                 */
               /*     if ( foundNonZero )
                    {*/
                return -(numberString.length() - (decimalPointIndex + 1))
                    + (exp.isEmpty() ? 0 : Integer.parseInt(exp));
               /*     }
                    else
                    {
                        return (exp.equals("") ? 0 : Integer.parseInt(exp));
                    }   */
            }
        }
    }

    /**
     * todo correct this
     *
     * @param digit to correct
     */
    private static String correctPlainInteger(double digit) {
        String str = Double.toString(digit);
        if (MathUtils.isPlainInteger(str)) {
            str = str.substring(0, str.indexOf("."));
        }
        return str;
    }
}
