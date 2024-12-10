// Created: Mar 24, 2003 T 4:01:16 PM
package org.clematis.math.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities, including regular expressions search and replacement
 */
public class StringUtils {

    public static final int NON_BREAKING_SPACE = 160;

    /**
     * Search a string for all instances of a substring and replace it with another string.
     *
     * @param search          Substring to search for
     * @param replace         String to replace it with
     * @param sourceString          String to search through
     * @param caseIndependent flag
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplace(String search, String replace, String sourceString, boolean caseIndependent) {
        int spot;

        String returnString;
        String source = sourceString;

        if (caseIndependent) {
            spot = source.toLowerCase().indexOf(search.toLowerCase());
        } else {
            spot = source.indexOf(search);
        }

        if (spot > -1) {
            returnString = "";
        } else {
            returnString = source;
        }

        while (spot > -1) {
            if (spot == source.length() + 1) {
                returnString = returnString.concat(source.substring(0, source.length() - 1).concat(replace));
                source = "";
            } else if (spot > 0) {
                returnString = returnString.concat(source.substring(0, spot).concat(replace));
                source = source.substring(spot + search.length());
            } else {
                returnString = returnString.concat(replace);
                source = source.substring(spot + search.length());
            }
            if (caseIndependent) {
                spot = source.toLowerCase().indexOf(search.toLowerCase());
            } else {
                spot = source.indexOf(search);
            }
        }
        if (!source.equals(sourceString)) {
            return returnString.concat(source);
        } else {
            return returnString;
        }
    }

    /**
     * Search a string for all instances of a substring and replace
     * it with another string.
     *
     * @param search  Substring to search for
     * @param replace String to replace it with
     * @param source  String to search through
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplace(String search, String replace, String source) {
        return sReplace(search, replace, source, true);
    }

    /**
     * Search a string for all instances of a substring and replace
     * it with another string. Uses regular expressions.
     * <p/>
     * The full regular expression syntax accepted by RE is described here:
     * <p/>
     * <pre>
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Characters</font></b>
     * <p/>
     * <br>
     * <p/>
     *    <i>unicodeChar</i>          Matches any identical unicode character
     *    \                    Used to quote a meta-character (like '*')
     *    \\                   Matches a single '\' character
     *    \0nnn                Matches a given octal character
     *    \xhh                 Matches a given 8-bit hexadecimal character
     *    \\uhhhh               Matches a given 16-bit hexadecimal character
     *    \t                   Matches an ASCII tab character
     *    \n                   Matches an ASCII newline character
     *    \r                   Matches an ASCII return character
     *    \f                   Matches an ASCII form feed character
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Character Classes</font></b>
     * <p/>
     * <br>
     * <p/>
     *    [abc]                Simple character class
     *    [a-zA-Z]             Character class with ranges
     *    [^abc]               Negated character class
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Standard POSIX Character Classes</font></b>
     * <p/>
     * <br>
     * <p/>
     *    [:alnum:]            Alphanumeric characters.
     *    [:alpha:]            Alphabetic characters.
     *    [:blank:]            Space and tab characters.
     *    [:cntrl:]            Control characters.
     *    [:digit:]            Numeric characters.
     *    [:graph:]            Characters that are printable and are also visible.
     *                         (A space is printable, but not visible, while an `a' is both.)
     *    [:lower:]            Lower-case alphabetic characters.
     *    [:print:]            Printable characters (characters that are not control characters.)
     *    [:punct:]            Punctuation characters (characters that are not letter, digits,
     *                         control characters, or space characters).
     *    [:space:]            Space characters (such as space, tab, and formfeed, to name a few).
     *    [:upper:]            Upper-case alphabetic characters.
     *    [:xdigit:]           Characters that are hexadecimal digits.
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Non-standard POSIX-style Character Classes</font></b>
     * <p/>
     * <br>
     * <p/>
     *    [:javastart:]        Start of a Java identifier
     *    [:javapart:]         Part of a Java identifier
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Predefined Classes</font></b>
     * <p/>
     * <br>
     * <p/>
     *    .                    Matches any character other than newline
     *    \w                   Matches a "word" character (alphanumeric plus "_")
     *    \W                   Matches a non-word character
     *    \s                   Matches a whitespace character
     *    \S                   Matches a non-whitespace character
     *    \d                   Matches a digit character
     *    \D                   Matches a non-digit character
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Boundary Matchers</font></b>
     * <p/>
     * <br>
     * <p/>
     *    ^                    Matches only at the beginning of a line
     *    $                    Matches only at the end of a line
     *    \b                   Matches only at a word boundary
     *    \B                   Matches only at a non-word boundary
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Greedy Closures</font></b>
     * <p/>
     * <br>
     * <p/>
     *    A*                   Matches A 0 or more times (greedy)
     *    A+                   Matches A 1 or more times (greedy)
     *    A?                   Matches A 1 or 0 times (greedy)
     *    A{n}                 Matches A exactly n times (greedy)
     *    A{n,}                Matches A at least n times (greedy)
     *    A{n,m}               Matches A at least n but not more than m times (greedy)
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Reluctant Closures</font></b>
     * <p/>
     * <br>
     * <p/>
     *    A*?                  Matches A 0 or more times (reluctant)
     *    A+?                  Matches A 1 or more times (reluctant)
     *    A??                  Matches A 0 or 1 times (reluctant)
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Logical Operators</font></b>
     * <p/>
     * <br>
     * <p/>
     *    AB                   Matches A followed by B
     *    A|B                  Matches either A or B
     *    (A)                  Used for subexpression grouping
     * <p/>
     * <br>
     * <p/>
     *  <b><font face=times roman>Backreferences</font></b>
     * <p/>
     * <br>
     * <p/>
     *    \1                   Backreference to 1st parenthesized subexpression
     *    \2                   Backreference to 2nd parenthesized subexpression
     *    \3                   Backreference to 3rd parenthesized subexpression
     *    \4                   Backreference to 4th parenthesized subexpression
     *    \5                   Backreference to 5th parenthesized subexpression
     *    \6                   Backreference to 6th parenthesized subexpression
     *    \7                   Backreference to 7th parenthesized subexpression
     *    \8                   Backreference to 8th parenthesized subexpression
     *    \9                   Backreference to 9th parenthesized subexpression
     * <p/>
     * <br>
     * <p/>
     * </pre>
     *
     * @param search          substring to search for (regular expression)
     * @param pattern pattern to replace it with (includes \1, \2, etc to match parens)
     * @param source          string to search through
     * @param exclusions      - the array of match cases that are not to be replaced. This can
     *                        be a regular expression. May be null.
     * @param replaceCallback - replace callback function
     * @param matchCase       match case flag
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplaceReg(String search,
                                     String pattern,
                                     String source,
                                     String[] exclusions,
                                     IReplaceCallback replaceCallback,
                                     boolean matchCase) {
        /*
         * Compile regular expression
         */
        //RE regExpr = new RE(search);
        /*
         * Apply case independent parameter
         */
//        if (!matchCase) {
            //    regExpr.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
  //      }
        /*
         * While there are some mathes in the string,
         * cut the source remainder and make proper substitutions
         * on pieces of the source.
         */
        StringBuilder result = new StringBuilder();
        /*
         * Duplicate the source string to make a temp string
         * which we will cut as matching occurs.
         */
        String sourceRemainder = source;
        //  while (regExpr.match(sourceRemainder))
        // {
        /*
         * Replace placeholders like \1 with actual substrings (parens)
         * from next match case.
         */
        /*    int parenCount = regExpr.getParenCount();
   String trunk = sourceRemainder.substring(0, regExpr.getParenEnd(0));
   sourceRemainder = sourceRemainder.substring(regExpr.getParenEnd(0),
                                               sourceRemainder.length());
   if (! contains(regExpr.getParen(0), exclusions))
   {
       /**
        * Make replacements via pattern and form actual replace string
        */
        /*      String replace = pattern;
           for (char i = 0; i < parenCount; i++)
           {
               if (pattern.indexOf(i) != -1)
               {
                   String paren = regExpr.getParen(i);
                   if (! matchCase)
                   {
                       paren = paren.toLowerCase();
                   }
                   if (replaceCallback != null)
                   {
                       paren = replaceCallback.getString(paren);
                   }
                   replace = StringUtils.sReplace
                           (new String(new char[]{i}), paren, replace);
               }
           }
           trunk = regExpr.subst(trunk, replace, RE.REPLACE_FIRSTONLY);
       }
       result.append(trunk);
   }     */
        /*
         * Here we got a remainder of a source - append it to result string buffer
         */
        //result.append(sourceRemainder);
        // return result.toString();
        /*   }     */
        /*  catch (RESyntaxException e)
        {*/
        /*
         * If syntax error occured,
         * return source string without
         * changes
         */
        //log.error(e);
        /*   }   */
        return source;
    }

    /**
     * Find all regular occurrences of search string within string and
     * return tokenized string buffer, including occurrences as separate tokens.
     *
     * @param pattern regular expressions pattern
     * @param input   string
     * @return tokenized string array
     */
    public static List<String> tokenizeReg(Pattern pattern, String input) {
        int index = 0;
        List<String> matchList = new ArrayList<>();
        Matcher m = pattern.matcher(input);

        // Add segments before each match found
        while (m.find()) {
            String match = input.subSequence(index, m.start()).toString();

            if (!match.trim().isEmpty()) {
                matchList.add(match);
            }

            if (!m.group().trim().isEmpty()) {
                matchList.add(m.group());
            }

            index = m.end();
        }

        // If no match was found, return this
        if (index == 0) {
            matchList.add(input);
        } else {
            CharSequence sub = input.subSequence(index, input.length());
            // Add remaining segment
            if (!sub.toString().trim().isEmpty()) {
                matchList.add(sub.toString());
            }

            // Construct result
            int resultSize = matchList.size();
            while (resultSize > 0 && matchList.get(resultSize - 1).isEmpty()) {
                resultSize--;
            }
        }
        return matchList;
    }

    /**
     * Find all regular occurrences of search string within string and
     * return substrings of the initial string with occurrences in between in a form of separate tokens
     *
     * @param str       source string
     * @param search    string
     * @param matchCase match case flag
     * @return tokenized string array
     */
    public static List<String> tokenizeReg(String str, String search, boolean matchCase) {
        Pattern pattern;
        if (matchCase) {
            pattern = Pattern.compile(search);
        } else {
            pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        }

        return tokenizeReg(pattern, str);
    }

    /**
     * Checks to see whether if this string array contains given string
     *
     * @param str   given string
     * @param array to seek for the str in
     * @return true if str is found
     */
    private static boolean contains(String str, String[] array) {
        if (str != null && array != null) {

            for (final String aArray : array) {
                Pattern pattern = Pattern.compile(aArray, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(str);
                if (matcher.matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Match string with regular expression
     *
     * @param str       string to be matched
     * @param match     string representation of regular expression to
     *                  match string
     * @param matchCase match case flag
     * @return true, if string matches regular expression
     */
    public static boolean matchString(String str, String match, boolean matchCase) {
        if (str != null) {
            Pattern pattern;
            if (matchCase) {
                pattern = Pattern.compile(match);
            } else {
                pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);
            }

            return pattern.matcher(str).matches();
        }
        return false;
    }

    /**
     * Searches attribute value within sgml tag representation - attr="(value)".
     * Returns first attribute value without enclosing quotation marks.
     *
     * @param str      initial string
     * @param attrName name of required attribute
     * @return first attribute value without enclosing quotation marks, may be null
     */
    @SuppressWarnings({"checkstyle:MultipleStringLiterals", "checkstyle:NestedIfDepth"})
    public static String getAttributeValue(String str, String attrName) {
        String attributeValue = null;
        /* start of attribute name */
        int start = str.toLowerCase().indexOf(" " + attrName.toLowerCase());
        /* start and end of attribute value */
        if (start != -1) {
            attributeValue = str.substring(start + attrName.length() + 1);
            /* attribute begins with '=' sign */
            start = attributeValue.indexOf("=");
            if (start != -1) {
                attributeValue = attributeValue.substring(start + 1).trim();
            }
            /* now check if attribute is quoted */
            boolean quoted = attributeValue.startsWith("\"");
            /* delete first qoutation */
            if (quoted) {
                attributeValue = attributeValue.substring(1);
            }
            /* attribute ends with space or quotation */
            int end = quoted ? attributeValue.indexOf("\"") : attributeValue.indexOf(" ");
            if (end != -1) {
                attributeValue = attributeValue.substring(0, end);
            } else {
                /* attribute ends with slash */
                end = attributeValue.indexOf("/>");
                if (end != -1) {
                    attributeValue = attributeValue.substring(0, end);
                } else {
                    /* attribute ends with > */
                    end = attributeValue.indexOf(">");
                    if (end != -1) {
                        attributeValue = attributeValue.substring(0, end);
                    }
                }
            }
        }

        return attributeValue;
    }

    /**
     * Searches attribute value within sgml tag representation - attr="(value)".
     * Returns first attribute value without enclosing quotation marks and in case insensitive way.
     *
     * @param str      initial string
     * @param attrName name of required attribute
     * @return first attribute value without enclosing quotation marks, may be null
     */
    public static String getAttributeValueIgnoreCase(String str, String attrName) {
        String attributeValue = getAttributeValue(str, attrName.toUpperCase());
        if (attributeValue == null) {
            attributeValue = getAttributeValue(str, attrName.toLowerCase());
        }
        return attributeValue;
    }

    /**
     * Replaces ASCII codes in given string
     * For example, %24 with $
     *
     * @param str initial string
     * @return string with replaced ASCII codes.
     */
    public static String replaceASCII(String str) {
        return sReplaceReg("%([:alnum:]{2})", "\1", str, null, new ASCII(), false);
    }

    /**
     * Returns ASCII for char
     *
     * @param ch initial char
     * @return ASCII code
     */
    @SuppressWarnings("checkstyle:MultipleStringLiterals")
    public static String toASCII(char ch) {
        return "&#" + Integer.toString(ch) + ";";
    }

    /**
     * Strip tags from sgml formatted text
     *
     * @param str initial string
     * @return modified string
     */
    public static String stripTags(String str) {
        return sReplaceReg("\\x3c[/]*[:alnum:]*\\x3e", "", str);
    }

    /**
     * Search a string for all instances of a substring and replace
     * it with another string. Uses regular expressions.
     *
     * @param search          substring to search for (regular expression)
     * @param pattern  to replace it with (includes \1, \2, etc to match parens)
     * @param source          string to search through
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplaceReg(String search, String pattern, String source) {
        return sReplaceReg(search, pattern, source, null, null, false);
    }

    /**
     * Search a string for all instances of a substring and replace
     * it with another string. Uses regular expressions.
     *
     * @param search          substring to search for (regular expression)
     * @param pattern  to replace it with (includes \1, \2, etc to match parens)
     * @param source          string to search through
     * @param exclusions      - the array of match cases that are not to be replaced. This can
     *                        be a regular expression. May be null.
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplaceReg(String search, String pattern,
                                     String source, String[] exclusions) {
        return sReplaceReg(search, pattern, source, exclusions, null, false);
    }

    /**
     * Remove punctuation from given string
     *
     * @param inputString - the string to remove punctuation from
     * @return the string with punctuation removed
     */
    public static String removePunctuation(String inputString) {
        String str = inputString.trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '!' || ch == '?' || ch == ',' || ch == '.'
                || ch == ':' || ch == ';' || ch == '\'' || ch == '-' || ch == '_') {
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Remove all occurrences of certain chars from a given string
     *
     * @param inStr       the string to remove from
     * @param removeChars the characters to remove
     * @return the string with removeChars removed
     */
    public static String removeChars(String inStr, String removeChars) {
        StringBuilder outStr = new StringBuilder(inStr);
        if (removeChars != null) {
            int removeLen = removeChars.length();
            int start = 0;
            int end = inStr.indexOf(removeChars);
            if (end > -1) {
                outStr = new StringBuilder();
                while (end > -1) {
                    outStr.append(inStr, start, end);
                    start = end + removeLen;
                    end = inStr.indexOf(removeChars, start);
                }
                outStr.append(inStr.substring(start));
            }
        }
        return outStr.toString();
    }

    /**
     * Replaces carriage returns in text with single whitespace
     *
     * @param str to trim
     * @return trimmed string
     */
    public static String trimInside(String str) {
        /*
         * Get rid of carriage returns.
         */
        return StringUtils.sReplace("\r", " ", StringUtils.sReplace("\n", " ", str));
    }

    /**
     * Wraps lines at the given number of columns
     *
     * @param s    to wrap lines
     * @param cols the number of columns to form
     * @return wrapped string
     */
    public static String wrapLines(String s, int cols) {
        char[] c = s.toCharArray();
        char[] d = new char[c.length];

        int i = 0;
        int j = 0;
        int lastspace = -1;
        while (i < c.length) {
            if (c[i] == '\n') {
                j = 0;
            }
            if (j > cols && lastspace > 0) {
                d[lastspace] = '\n';
                j = i - lastspace;
                lastspace = -1;
            }
            if (c[i] == ' ') {
                lastspace = i;
            }
            d[i] = c[i];
            i++;
            j++;
        }
        return new String(d);
    }

    /**
     * Removes all whitespaces from the string
     * and carriage returns in text
     *
     * @param inputString to remove whitespaces
     * @return string with whitespaces removed
     */
    public static String removeWhitespaces(String inputString) {
        String str = inputString.trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == NON_BREAKING_SPACE) {
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Replaces all whitespaces from the string
     * and carriage returns in text with _
     *
     * @param inputString to replace whitespaces in
     * @return string with whitespaces replaced
     */
    public static String replaceWhitespaces(String inputString) {
        String str = inputString.trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == NON_BREAKING_SPACE) {
                ch = '_';
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Removes all whitespaces from the string
     * and carriage returns in text with single whitespace
     *
     * @param inputString to remove duplicate whitespaces
     * @return string with duplicate whitespaces removed
     */
    public static String removeDuplicateWhitespaces(String inputString) {
        String str = inputString.trim();
        StringBuilder sb = new StringBuilder();
        boolean deleteWhitespace = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == NON_BREAKING_SPACE) {
                if (deleteWhitespace) {
                    continue;
                } else {
                    deleteWhitespace = true;
                }
            } else {
                deleteWhitespace = false;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Replaces substring with new variant inside another string
     *
     * @param sourceStringInput source
     * @param stringRemove to remove
     * @param stringInsert to insert
     * @return modified string
     */
    public static String replaceString(String sourceStringInput, String stringRemove, String stringInsert) {
        String sourceString = sourceStringInput;
        if (sourceString != null) {
            boolean ok = false;
            int position = 0;
            while (!ok) {
                position = sourceString.indexOf(stringRemove, position);
                if (position > -1) {
                    String beginSourceString = sourceString.substring(0, position);
                    String endSourceString = sourceString.substring(position + stringRemove.length());
                    sourceString = beginSourceString + stringInsert + endSourceString;
                    position = beginSourceString.length() + stringInsert.length();
                } else {
                    ok = true;
                }
            }
        }
        return sourceString;
    }

    /**
     * Makes a formatted string from a given double value.
     * The delimiter will be a dot. The precision will be 2 digits
     * after the delimiter.
     *
     * @param value the double value.
     * @return the string representation of the double value.
     */
    public static String formatDouble(double value) {
        return (formatDouble((float) value, 2, true));
    }

    /**
     * Makes a formatted string from a given double value.
     * The delimeter will be a dot.
     *
     * @param value the double value.
     * @param precision the number of digits after the delimeter.
     * @return the string representation of the double value.
     */
    public static String formatDouble(double value, int precision) {
        return (formatDouble(value, precision, true));
    }

    /**
     * Makes a formatted string from a given double value.
     * This method returns <code>null</code> if the <code>precision</code> is
     * less than zero.
     *
     * @param value the double value.
     * @param precision the number of digits after the delimeter.
     * @param isDot  is delimiter a dot?
     * @return the string representation of the double value.
     */
    public static String formatDouble(double value, int precision, boolean isDot) {
        return formatDouble(value, precision, isDot, true);
    }

    /**
     * Makes a formatted string from a given double value.
     * This method returns <code>null</code> if the <code>precision</code> is
     * less than zero.
     *
     * @param value  the double value.
     * @param precision  the number of digits after the delimeter.
     * @param isDot   is delimiter a dot?
     * @param round if true, the value is rounded one half up.
     * @return the string representation of the double value.
     */
    @SuppressWarnings({"UnusedAssignment",
        "UnnecessaryUnboxing",
        "checkstyle:MagicNumber",
        "checkstyle:InnerAssignment",
        "checkstyle:ParameterName",
        "checkstyle:ReturnCount"
    })
    public static String formatDouble(double value, int precision, boolean isDot, boolean round) {
        // Check parameters.
        if (precision < 0) {
            return (null);
        }

        // Return value.
        String ret = null;

        // Check for too big and too small values.
        int idx = -1;
        ret = Double.toString(value);
        if ((idx = ret.indexOf("E")) > -1 || (idx = ret.indexOf("e")) > -1) {
            // x.xxxEyy...
            String dPart = ret.substring(0, idx);               // x.xxx
            String ePart = ret.substring(idx);    // Eyy
            double dv = Double.valueOf(dPart).doubleValue();
            return (formatDouble(dv, precision >>> 1, isDot, round) + ePart);
        }
        ret = null;

        // Get delimiter.
        String delim = isDot ? "." : ",";

        // Get multiplier.
        long mply = 1L;
        for (int i = 0; i < precision; i++) {
            mply *= 10L;
        }

        long sign = value < 0.0 ? -1L : 1L;
        int iround = round ? 1 : 0;

        // Get the value parts.
        if (precision == 0) {
            long ipart = (long) (value + sign * 1.0 / 2);
            return (Long.toString(ipart));
        }
        long ipart = (long) value;
        String ssign = value < 0.0 ? "-" : "";
        ipart *= sign;
        long fpart = (long) (value * (double) mply + sign * iround * 1.0 / 2) * sign - ipart * mply;

        if (fpart < 0) {
            fpart = -fpart;
        }

        // Format the fpart string.
        String fpartStr = Long.toString(fpart);
        String fpartStr1 = Long.toString(mply).substring(1);
        idx = fpartStr1.length() - fpartStr.length();

        if (idx > 0) {
            fpartStr = fpartStr1.substring(0, idx) + fpartStr;
        } else if (idx < 0) {
            fpartStr = fpartStr.substring(1);
            ipart++;
        }

        // Format the string.
        ret = ssign + ipart + delim + fpartStr;

        // Return result.
        return (ret);
    }

    /*
     * stringToVectorWord.
     *
     * @param  inputString string.
     * @param  inputString string separate.
     * @return  vector of words.
     */
    public static Vector<String> stringToVectorWord(String inputString, String separator) {

        Vector<String> array = new Vector<>(1, 1);
        int[] indx1 = new int[2];

        int length = separator.length();
        do {
            indx1[1] = inputString.indexOf(separator, indx1[0]);
            if (indx1[1] > -1) {
                array.addElement(inputString.substring(indx1[0], indx1[1]));
            } else {
                array.addElement(inputString.substring(indx1[0]));
                break;
            }
            indx1[0] = indx1[1] + length;
        } while (indx1[0] < inputString.length());

        return array;
    }

    /**
     * Tokenizes XML formatted string by tag (for example <math> ... </math>)
     *
     * @param source the input source string.
     * @param tag    the tag name ( in our example it is "math" ).
     * @return list containing tokens.
     * @throws Exception if something
     */
    @SuppressWarnings("checkstyle:ParameterAssignment")
    public static ArrayList<String> tokenizeByTag(String source, String tag)
        throws Exception {
        ArrayList<String> tokens = new ArrayList<String>();
        int start;
        int end;
        String openTag = "<" + tag;
        String closeTag = "</" + tag + ">";
        while ((start = source.indexOf(openTag)) >= 0) {
            if (start > 0) {
                tokens.add(source.substring(0, start));
            }
            end = source.indexOf(closeTag);
            if (end == -1 || end < start) {
                throw new Exception("StringUtils.tokenizeByTag: tag <" + tag + "> is not closed or opened");
            }
            String token = source.substring(start, end + closeTag.length());
            tokens.add(token);
            end += closeTag.length();
            source = source.substring(end);
        }
        if (!source.isEmpty()) {
            tokens.add(source);
        }

        return tokens;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Deprecated
    public static String escapeUnicodeString(String str) {
        if (str != null) {
            StringBuilder ostr = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                if ((ch >= 0x0020) && (ch <= 0x007e)) {
                    ostr.append(ch);
                } else {
                    ostr.append("\\u");
                    /*String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);
                    if (hex.length() == 2)
                        ostr += "00";
                    ostr += hex.toUpperCase(Locale.ENGLISH);*/
                    ostr.append(charToHex(str.charAt(i)));
                }
            }
            return (ostr.toString());
        } else {
            return null;
        }
    }


    @SuppressWarnings("checkstyle:MagicNumber")
    public static String charToHex(char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    @SuppressWarnings({"checkstyle:Indentation", "checkstyle:MagicNumber"})
    public static String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char[] hexDigit =
            {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
            };
        char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
        return new String(array);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static String convertSymbolsToHtmlEntities(String source) {
        if (source != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < source.length(); i++) {
                char ch = source.charAt(i);
                if ((ch >= 0x0020) && (ch <= 0x007f)) {
                    sb.append(ch);
                } else {
                    sb.append("&#").append((int) ch).append(";");
                }
            }
            return sb.toString();
        }
        return null;
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    public static String convertHtmlEntitiesToSymbols(String source) {
        Scanner st = new Scanner(source).useDelimiter("&#");
        String tmp;
        StringBuilder sb = new StringBuilder();
        //&#1234
        while (st.hasNext()) {
            tmp = st.next();
            if (tmp.indexOf(';') != -1 && isNumberString(tmp.substring(0, tmp.indexOf(';')))) {
                String str = tmp.substring(0, tmp.indexOf(';'));
                if (Integer.parseInt(str) == 10 || Integer.parseInt(str) == 13 || Integer.parseInt(str) > 31) {
                    sb.append((char) Integer.parseInt(tmp.substring(0, tmp.indexOf(';'))));
                    sb.append(tmp.substring(tmp.indexOf(';') + 1));
                } else {
                    sb.append(tmp.substring(tmp.indexOf(';') + 1));
                }
            } else {
                sb.append(tmp);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings({"checkstyle:MagicNumber", "checkstyle:ReturnCount"})
    public static boolean isNumberString(String source) {
        if ("".equals(source) || source == null) {
            return false;
        }
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if ((int) ch < 0x002f || (int) ch > 0x0039) {
                return false;
            }
        }
        return true;
    }

    /**
     * Interface to make additional processing of regular search results
     * and to return proper string to substitute.
     */
    public interface IReplaceCallback {
        String getString(String str);
    }

    /**
     * This class returns ASCII coded symbol if
     * given its hex presentation.
     */
    static class ASCII implements IReplaceCallback {
        /**
         * This methos tries to parse ASCII code
         *
         * @param str representation of ascii code
         * @return ascii character
         */
        @SuppressWarnings("checkstyle:MagicNumber")
        public String getString(String str) {
            try {
                int code = Integer.parseInt(str, 16);
                char ch = (char) code;
                return String.valueOf(ch);
            } catch (NumberFormatException ex) {
                return str;
            }
        }
    }
}
