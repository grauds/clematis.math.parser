// Created: 02.03.2007 T 10:27:32
package org.clematis.math.v2.io;

/**
 * Digits formatter
 *
 * @version 1.0
 */
public class NumberFormatter {
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
                /**
                 * Eliminate leading minus or plus sign
                 */
                numberString = numberString.trim();
                if (numberString.length() > 0 && (numberString.charAt(0) == '-' || numberString.charAt(0) == '+')) {
                    numberString = numberString.substring(1);
                }
                /**
                 * Eliminate leading zeroes only if its length
                 * is more than 1 and number string has dot
                 */
                if (numberString.indexOf(".") != -1) {
                    while (numberString.length() > 1 && numberString.charAt(0) == '0') {
                        numberString = numberString.substring(1);
                    }
                }
                /**
                 * In case number has leading zeros and doesnt have
                 * dot - it is treated as decimal fraction
                 * 00345 -> 0.00345
                 */
                else if (numberString.length() > 0 && numberString.charAt(0) == '0' && !numberString.equals("0")) {
                    numberString = "." + numberString;
                }

                return numberString;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
