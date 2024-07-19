// Created: Mar 24, 2003 T 4:01:16 PM
package org.clematis.math.utils;

import java.awt.*;
import java.io.BufferedReader;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities, including regular expressions search and replacement
 */
public class StringUtils {
    /**
     * Interface to make additional processing of regular search results
     * and to return proper string to substitute.
     */
    public interface sReplaceCallback {
        String getString(String str);
    }

    /**
     * This class returns ASCII coded symbol if
     * given its hex presentation.
     */
    static class sASCII implements sReplaceCallback {
        /**
         * This methos tries to parse ASCII code
         *
         * @param str representation of ascii code
         * @return ascii character
         */
        public String getString(String str) {
            try {
                int code = Integer.parseInt(str, 16);
                char ch = (char) code;
                return String.valueOf(ch);
            } catch (NumberFormatException ex) {
                /**
                 * Return the same string
                 */
                return str;
            }
        }
    }

    /**
     * Search a string for all instances of a substring and replace
     * it with another string.
     *
     * @param search          Substring to search for
     * @param replace         String to replace it with
     * @param source          String to search through
     * @param caseIndependent flag
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplace(String search, String replace, String source, boolean caseIndependent) {
        int spot;
        String returnString;
        final String origSource = source;

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
        if (!source.equals(origSource)) {
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
     *    [:graph:]            Characters that are printable and are also visible. (A space is printable, but not visible, while an `a' is both.)
     *    [:lower:]            Lower-case alphabetic characters.
     *    [:print:]            Printable characters (characters that are not control characters.)
     *    [:punct:]            Punctuation characters (characters that are not letter, digits, control characters, or space characters).
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
     * @param replace_pattern pattern to replace it with (includes \1, \2, etc to match parens)
     * @param source          string to search through
     * @param exclusions      - the array of match cases that are not to be replaced. This can
     *                        be a regular expression. May be null.
     * @param replaceCallback - replace callback function
     * @param matchCase       match case flag
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplaceReg(String search, String replace_pattern,
                                     String source, String[] exclusions,
                                     StringUtils.sReplaceCallback replaceCallback,
                                     boolean matchCase) {
        /*    try
        {*/
        /**
         * Compile regular expression
         */
        //RE regExpr = new RE(search);
        /**
         * Apply case independent parameter
         */
        if (!matchCase) {
            //    regExpr.setMatchFlags(RE.MATCH_CASEINDEPENDENT);
        }
        /**
         * While there are some mathes in the string,
         * cut the source remainder and make proper substitutions
         * on pieces of the source.
         */
        StringBuilder result = new StringBuilder();
        /**
         * Duplicate the source string to make a temp string
         * which we will cut as matching occurs.
         */
        String sourceRemainder = source;
        //  while (regExpr.match(sourceRemainder))
        // {
        /**
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
        /*      String replace = replace_pattern;
           for (char i = 0; i < parenCount; i++)
           {
               if (replace_pattern.indexOf(i) != -1)
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
        /**
         * Here we got a remainder of a source - append it to result string buffer
         */
        //result.append(sourceRemainder);
        // return result.toString();
        /*   }     */
        /*  catch (RESyntaxException e)
        {*/
        /**
         * If syntax error occured,
         * return source string without
         * changes
         */
        //log.error(e);
        /*   }   */
        return source;
    }

    /**
     * Find all regular occurences of search string within string and
     * return tokenized string buffer, including occurences as separate
     * tokens.
     *
     * @param pattern regular expressions pattern
     * @param input   string
     * @param limit
     * @return tokenized string array
     */
    public static String[] tokenizeReg(Pattern pattern, CharSequence input, int limit) {
        int index = 0;
        boolean matchLimited = limit > 0;
        ArrayList<String> matchList = new ArrayList<String>();
        Matcher m = pattern.matcher(input);

        // Add segments before each match found
        while (m.find()) {
            String match = null;

            if (!matchLimited || matchList.size() < limit - 1) {
                match = input.subSequence(index, m.start()).toString();
            } else if (matchList.size() == limit - 1) {
                match = input.subSequence(index, input.length()).toString();
            }

            if (!match.trim().equals("")) {
                matchList.add(match);
            }
            if (!m.group().trim().equals("")) {
                matchList.add(m.group());
            }

            index = m.end();
        }

        // If no match was found, return this
        if (index == 0) {
            return new String[]{input.toString()};
        }
        // Add remaining segment
        if (!matchLimited || matchList.size() < limit) {
            matchList.add(input.subSequence(index, input.length()).toString());
        }
        // Construct result
        int resultSize = matchList.size();
        if (limit == 0) {
            while (resultSize > 0 && matchList.get(resultSize - 1).equals("")) {
                resultSize--;
            }
        }
        String[] result = new String[resultSize];
        return matchList.subList(0, resultSize).toArray(result);
    }

    public static void main(String[] args) {
        String str =
            "</td>  </tr>  </table><br>  Enter the name without space, for example <strong>1,2-dinitrophenol</strong>.  <uni:link idref=\"sec14.1\"> Section 14.1</uni:link></br>  <uni:link idref=\"sec14.2\">Section 14.2</uni:link></p>";
        List<String> tokens = StringUtils.tokenizeReg(str, "<[^<>]+>", false);
        for (String token : tokens) {
            System.out.println(token);
        }
        String[] tokensStrings = StringUtils.tokenizeReg(Pattern.compile("<[^<>]+>"), str, str.length());
        for (String token : tokensStrings) {
            System.out.println(token);
        }
    }

    /**
     * Find all regular occurences of search string within string and
     * return tokenized string buffer, including occurences as separate
     * tokens.
     * <p/>
     * Example:
     * <p/>
     * <html>some text<table></table>some text</html>
     * <p/>
     * <html>
     * sometext
     * <table>
     * </table>
     * some text
     * </html>
     *
     * @param str       source string
     * @param search    string
     * @param matchCase match case flag
     * @return tokenized string array
     */
    public static List<String> tokenizeReg(String str, String search, boolean matchCase) {
        ArrayList<String> array = new ArrayList<String>();

        Pattern pattern;
        if (matchCase) {
            pattern = Pattern.compile(search);
        } else {
            pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        }

        return Arrays.asList(pattern.split(str));
    }

    /**
     * Checks to see whether if this string array contains given string
     *
     * @param str   given string
     * @param array to seek for the str in
     * @return true if str is found
     */
    private static boolean contains(String str, String[] array) {
        if (str == null || array == null) {
            return false;
        }
        for (final String aArray : array) {
            Pattern pattern = Pattern.compile(aArray, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                return true;
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
    public static String getAttributeValue(String str, String attrName) {
        String attributeValue = null;
        /** start of attribute name */
        int start = str.toLowerCase().indexOf(" " + attrName.toLowerCase());
        /** start and end of attribute value */
        if (start != -1) {
            attributeValue = str.substring(start + attrName.length() + 1);
            /** attribute begins with '=' sign */
            start = attributeValue.indexOf("=");
            if (start != -1) {
                attributeValue = attributeValue.substring(start + 1).trim();
            }
            /** now check if attribute is quoted */
            boolean quoted = attributeValue.startsWith("\"");
            /** delete first qoutation */
            if (quoted) {
                attributeValue = attributeValue.substring(1);
            }
            /** attribute ends with space or quotation */
            int end = quoted ? attributeValue.indexOf("\"") : attributeValue.indexOf(" ");
            if (end != -1) {
                attributeValue = attributeValue.substring(0, end);
            } else {
                /** attribute ends with slash */
                end = attributeValue.indexOf("/>");
                if (end != -1) {
                    attributeValue = attributeValue.substring(0, end);
                } else {
                    /** attribute ends with > */
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
        return sReplaceReg("%([:alnum:]{2})", "\1", str, null, new sASCII(), false);
    }

    /**
     * Returns ASCII for char
     *
     * @param ch initial char
     * @return ASCII code
     */
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
     * @param replace_pattern pattern to replace it with (includes \1, \2, etc to match parens)
     * @param source          string to search through
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplaceReg(String search, String replace_pattern, String source) {
        return sReplaceReg(search, replace_pattern, source, null, null, false);
    }

    /**
     * Search a string for all instances of a substring and replace
     * it with another string. Uses regular expressions.
     *
     * @param search          substring to search for (regular expression)
     * @param replace_pattern pattern to replace it with (includes \1, \2, etc to match parens)
     * @param source          string to search through
     * @param exclusions      - the array of match cases that are not to be replaced. This can
     *                        be a regular expression. May be null.
     * @return The source with all instances of <code>search</code>
     * replaced by <code>replace</code>
     */
    public static String sReplaceReg(String search, String replace_pattern,
                                     String source, String[] exclusions) {
        return sReplaceReg(search, replace_pattern, source, exclusions, null, false);
    }

    /**
     * Remove punctuation from given string
     *
     * @param str - the string to remove from
     * @return the string with punctuation removed
     */
    public static String removePunctuation(String str) {
        str = str.trim();
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
     * Remove all occurences of certain chars from a given string
     *
     * @param inStr       the string to remove from
     * @param removeChars the characters to remove
     * @return the string with removeChars removed
     */
    public static String removeChars(String inStr, String removeChars) {
        String outStr = inStr;
        if (removeChars != null) {
            int removeLen = removeChars.length();
            int start = 0;
            if (inStr != null) {
                int end = inStr.indexOf(removeChars);
                if (end > -1) {
                    outStr = "";
                    while (end > -1) {
                        outStr += inStr.substring(start, end);
                        start = end + removeLen;
                        end = inStr.indexOf(removeChars, start);
                    }
                    outStr += inStr.substring(start);
                }
            }
        }
        return outStr;
    }

    /**
     * Replaces carriage returns in text with single whitespace
     *
     * @param str to trim
     * @return trimmed string
     */
    public static String trimInside(String str) {
        /**
         * Get rid of carriage returns.
         */
        str = StringUtils.sReplace("\n", " ", str);
        return StringUtils.sReplace("\r", " ", str);
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
     * @param str to remove whitespaces
     * @return string with whitespaces removed
     */
    public static String removeWhitespaces(String str) {
        str = str.trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == 160) {
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
     * @param str to replace whitespaces in
     * @return string with whitespaces replaced
     */
    public static String replaceWhitespaces(String str) {
        str = str.trim();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == 160) {
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
     * @param str to remove duplicate whitespaces
     * @return string with duplicate whitespaces removed
     */
    public static String removeDuplicateWhitespaces(String str) {
        str = str.trim();
        StringBuilder sb = new StringBuilder();
        boolean deleteWhitespace = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r' || ch == 160) {
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
     * @param SourceString source
     * @param StringRemove to remove
     * @param StringInsert to insert
     * @return modified string
     */
    public static String replaceString(String SourceString, String StringRemove, String StringInsert) {
        if (SourceString == null) {
            return SourceString;
        }
        boolean OK = false;
        int Position = 0;
        while (!OK) {
            Position = SourceString.indexOf(StringRemove, Position);
            if (Position > -1) {
                String Begin_SourceString = SourceString.substring(0, Position);
                String End_SourceString = SourceString.substring(Position + StringRemove.length());
                SourceString = Begin_SourceString + StringInsert + End_SourceString;
                Position = Begin_SourceString.length() + StringInsert.length();
            } else {
                OK = true;
            }
        }
        return SourceString;
    }

    /*
     * Reads string from stream.
     */
    @SuppressWarnings({"ConstantConditions"})
    public static String readString(BufferedReader m_dataStream) throws NoSuchElementException {
        String str = null;
        do {
            do {

                try {
                    str = m_dataStream.readLine();

                    int idx = str == null ? -1 : str.indexOf(" ");
                    if (idx > -1) {
                        str = str.substring(0, idx);
                    }
                } catch (Exception ex) {
                    return str;
                }


                if (str != null) {
                    str = str.trim();
                } else {
                    return str;
                }
            }
            while (str.length() == 0);
        }
        while (str.startsWith("//"));

        return str;
    }

    /**
     * Converts a HTML color string to the <code>Color</code> object.
     * The HTML color is a string in <code>#RRGGBB</code> form, where
     * <code>RR</code> is red component (R - heximal digit in 0..F range),
     * <code>GG</code> is green component (G - heximal digit in 0..F range)
     * and <code>BB</code> is blue component (B - heximal digit in 0..F
     * range). For example: <code>#000000</code> - black, <code>#FFFFFF
     * </code> - white, <code>#FF0000</code> - red, etc.
     *
     * @param _htmlColor the HTML color string.
     * @return the <code>Color</code> object if successful, <code>null
     * </code> otherwise.
     */
    public static Color parseHTMLColor(String _htmlColor) {
        Color ret = null;

        // Only valid parameter is useful.
        if (_htmlColor != null) {
            // The HTML color is 7 characters long.
            if (_htmlColor.length() == 7) {
                // The HTML color starts from the '#' character.
                if (_htmlColor.charAt(0) == '#') {
                    int argb = 0xff000000;          // No transparency.
                    int comp;

                    // Read components.
                    for (int i = 1; i < 7; i++) {
                        comp = parseHexChar(_htmlColor.charAt(i));
                        if (comp < 0) {
                            // Not a HTML string.
                            return (null);
                        }
                        argb |= comp << (((7 - i) << 2) - 4);
                    }

                    // Create the Color from our ARGB value.
                    ret = new Color(argb);
                }
            }
        }

        // Return result.
        return (ret);
    }

    // PARSERS //////////////////////////////////////////////////////////////

    /**
     * Converts a heximal digit character to unsigned integer.
     *
     * @param _hexChar the character to convert.
     * @return unsigned integer in the 0..15 range if successful or
     * -1 if the character isn't a heximal digit character.
     */
    public static int parseHexChar(char _hexChar) {
        switch (_hexChar) {
            case '0':
                return (0);
            case '1':
                return (1);
            case '2':
                return (2);
            case '3':
                return (3);
            case '4':
                return (4);
            case '5':
                return (5);
            case '6':
                return (6);
            case '7':
                return (7);
            case '8':
                return (8);
            case '9':
                return (9);
            case 'a':
            case 'A':
                return (10);
            case 'b':
            case 'B':
                return (11);
            case 'c':
            case 'C':
                return (12);
            case 'd':
            case 'D':
                return (13);
            case 'e':
            case 'E':
                return (14);
            case 'f':
            case 'F':
                return (15);
        }

        // Can't convert.
        return (-1);
    }

    /**
     * Makes a formatted string from a given double value.
     * The delimeter will be a dot. The precision will be 2 digits
     * after the delimeter.
     *
     * @param _dval the double value.
     * @return the string representation of the double value.
     */
    public static String formatDouble(double _dval) {
        return (formatDouble((float) _dval, 2, true));
    }

    /**
     * Makes a formatted string from a given double value.
     * The delimeter will be a dot.
     *
     * @param _dval the double value.
     * @param _prec the number of digits after the delimeter.
     * @return the string representation of the double value.
     */
    public static String formatDouble(double _dval, int _prec) {
        return (formatDouble(_dval, _prec, true));
    }

    /**
     * Makes a formatted string from a given double value.
     * This method returns <code>null</code> if the <code>_prec</code> is
     * less than zero.
     *
     * @param _dval the double value.
     * @param _prec the number of digits after the delimeter.
     * @param _dot  is delimeter a dot?
     * @return the string representation of the double value.
     */
    public static String formatDouble(double _dval, int _prec, boolean _dot) {
        return formatDouble(_dval, _prec, _dot, true);
    }

    /**
     * Makes a formatted string from a given double value.
     * This method returns <code>null</code> if the <code>_prec</code> is
     * less than zero.
     *
     * @param _dval  the double value.
     * @param _prec  the number of digits after the delimeter.
     * @param _dot   is delimeter a dot?
     * @param _round if true, the value is rounded one half up.
     * @return the string representation of the double value.
     */
    @SuppressWarnings({"UnusedAssignment", "UnnecessaryUnboxing"})
    public static String formatDouble(double _dval, int _prec, boolean _dot, boolean _round) {
        // Check parameters.
        if (_prec < 0) {
            return (null);
        }

        // Return value.
        String ret = null;

        // Check for too big and too small values.
        int idx = -1;
        ret = Double.toString(_dval);
        if ((idx = ret.indexOf("E")) > -1 || (idx = ret.indexOf("e")) > -1) {
            // x.xxxEyy...
            String dPart = ret.substring(0, idx);               // x.xxx
            String ePart = ret.substring(idx);    // Eyy
            double dv = Double.valueOf(dPart).doubleValue();
            return (formatDouble(dv, _prec >>> 1, _dot, _round) + ePart);
        }
        ret = null;

        // Get delimeter.
        String delim = _dot ? "." : ",";

        // Get multiplyer.
        long mply = 1L;
        for (int i = 0; i < _prec; i++) {
            mply *= 10L;
        }

        long sign = _dval < 0.0 ? -1L : 1L;
        int iround = _round ? 1 : 0;

        // Get the value parts.
        if (_prec == 0) {
            long ipart = (long) (_dval + sign * 1.0 / 2);
            return (Long.toString(ipart));
        }
        long ipart = (long) _dval;
        String ssign = _dval < 0.0 ? "-" : "";
        ipart *= sign;
        long fpart = (long) (_dval * (double) mply + sign * iround * 1.0 / 2) * sign - ipart * mply;
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
     * @param  in_string string.
     * @param  in_string string separate.
     * @return  vector of words.
     */
    public static Vector<String> stringToVectorWord(String in_string, String in_separate) {
        Vector<String> out_vector = new Vector<String>(1, 1);
        int[] indx1;
        indx1 = new int[2];
        indx1[0] = 0;
        int sep_len = in_separate.length();
        do {
            indx1[1] = in_string.indexOf(in_separate, indx1[0]);
            if (indx1[1] > -1) {
                out_vector.addElement(in_string.substring(indx1[0], indx1[1]));
            } else {
                out_vector.addElement(in_string.substring(indx1[0]));
                break;
            }
            indx1[0] = indx1[1] + sep_len;
        }
        while (indx1[0] < in_string.length());

        return out_vector;
    }

    /**
     * Tokenizes XML formatted string by tag (for example <math> ... </math>)
     *
     * @param source the input source string.
     * @param tag    the tag name ( in our example it is "math" ).
     * @return list containing tokens.
     * @throws Exception if something
     */
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
        if (source.length() > 0) {
            tokens.add(source);
        }

        return tokens;
    }

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

    static public String charToHex(char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }

    static public String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char[] hexDigit =
            {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
            };
        char[] array = {hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f]};
        return new String(array);
    }

    public static String convertSymbolsToEntities(String source) {
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

    public static String convertEntitiesToSymbols(String source) {
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

}
