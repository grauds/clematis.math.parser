// Created: 14.04.2005 T 11:18:19
package org.clematis.math.v1.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.utils.StringUtils;
import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.IOptions;
import org.clematis.math.v1.StringConstant;
import org.clematis.math.v1.algorithm.IParameterProvider;
import org.clematis.math.v1.algorithm.Key;
import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.operations.SimpleFraction;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.xpath.XPath;

/**
 * A collection of algorithm utilities
 */
public class AlgorithmUtils {

    public static final String ATTRIBUTE_CODE = "code";
    public static final String TOKEN_DELIMITER = "\"";
    public static final String IN_LINE_MESSAGE = " in line ";
    private static final String UNDEFINED_VARIABLE_MESSAGE = "Undefined variable: ";
    /**
     * Algorithm XML may be static, i.e. has only empty <algorithm> elements.
     * In this case algorithm does nothing and considered empty
     *
     * @param algorithmXML XML representing the algorithm.
     * @return true if algorithm is empty
     */
    public static boolean isEmpty(Element algorithmXML) {
        try {
            XPath path = XPath.newInstance(".//param");
            List<?> params = path.selectNodes(algorithmXML);

            boolean hasCode = false;
            if (algorithmXML.getChild(ATTRIBUTE_CODE) != null) {
                hasCode = !algorithmXML.getChildText(ATTRIBUTE_CODE).trim().isEmpty();
            }

            return params.isEmpty() && !hasCode;
        } catch (JDOMException e) {
            return false;
        }
    }

    /**
     * Remove algorithm="1" attributes recursively, if no algorithm
     * is found in question or such algorithm is empty.
     *
     * @param items - list of 'item' elements
     */
    public static void removeAlgorithmicAttributes(List<Element> items) {
        for (Element item : items) {
            item.removeAttribute("algorithm");
        }
    }

    /**
     * Processes element's content and replaces 'uni:variable' elements by
     * values of corresponding parameters.
     *
     * @param element element to process
     * @return String containing element text.
     */
    public static String substituteParameters(IParameterProvider algorithm, Element element) {
        StringBuilder sb = new StringBuilder();
        List<Content> list = element.getContent();

        for (Object contentItem : list) {
            if (contentItem instanceof Text) {
                sb.append(((Text) contentItem).getText());
            } else if (contentItem instanceof Element child
                && child.getName().equals("variable")
                && child.getNamespacePrefix().equals("uni")
            ) {

                Parameter parameter = algorithm.getParameter(child.getAttributeValue("name"));
                if (parameter != null) {
                    sb.append(parameter.getOutputValue(true));
                }
            }
        }
        return sb.toString();
    }

    /**
     * Recursively goes through the expression tree and collects all strings.
     *
     * @param exprRoot the expression root.
     * @param strList  the strings container.
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    public static void findStringVariants(IParameterProvider provider,
                                          IExpressionItem exprRoot,
                                          HashSet<String> strList
    ) {
        if (exprRoot instanceof IOptions logic) {
            ArrayList<IExpressionItem> variants = logic.getOptions();
            for (IExpressionItem variant : variants) {
                findStringVariants(provider, variant, strList);
            }
        } else if (exprRoot instanceof StringConstant) {
            String argumentString = ((StringConstant) exprRoot).getValue(null);
            /* this argument may contain parameter which is a multivariant logic instance */
            if (isParameterName(argumentString)) {
                Parameter p = provider.getParameter(argumentString);
                if (p != null) {
                    findStringVariants(provider, p.getExpressionRoot(), strList);
                }
            } else {
                strList.add(argumentString);
            }
        }
    }

    /**
     * Returns true if this string is a valid parameter name
     *
     * @param str string
     * @return true if this string is a valid parameter name
     */
    public static boolean isParameterName(String str) {
        return StringUtils.matchString(str, Parameter.FIND_EXPRESSION, true);
    }

    /**
     * Finds and replaces parameters in string. Quoted parameters will be skipped,
     * if flag "skipInsideStrings" is set
     *
     * @param string            the explored string
     * @param currentLine       the current line of the algorithm
     * @param skipInsideStrings quoted parameters will be skipped,
     *                          if this flag is set
     * @return array list with parameter names, may be empty, never null
     */
    @SuppressWarnings({
        "checkstyle:NestedIfDepth",
        "checkstyle:ParameterAssignment",
        "checkstyle:CyclomaticComplexity",
        "checkstyle:InnerAssignment"
    })
    public static String replaceParameters(String string,
                                           IParameterProvider provider,
                                           int currentLine,
                                           boolean skipInsideStrings
    ) throws AlgorithmException {
        if (provider != null && string != null && !string.trim().isEmpty()) {

            /* this parameter stops recursion if failed to find something, that looks like parameter */
            boolean failedToFindParameter = false;

            /* not found */
            StringBuilder notFoundParams = new StringBuilder();
            List<String> tokens = StringUtils.tokenizeReg(string, Parameter.FIND_EXPRESSION, false);

            /* number of apos signs */
            int apos = 0;

            /* substitution step results */
            StringBuilder result = new StringBuilder();
            for (String token : tokens) {
                /* filter only parameters */
                if (isParameterName(token) && (!skipInsideStrings || apos % 2 == 0)) {
                    Parameter param = provider.getParameter(token);

                    if (param != null) {
                        if (param.getContainer() != null) {
                            Key key = param.getContainer().getParameterKey(token);
                            if (key != null && key.getLine() > currentLine) {
                                throw new AlgorithmException(
                                    UNDEFINED_VARIABLE_MESSAGE
                                    + param.getName()
                                    + IN_LINE_MESSAGE
                                    + (currentLine + 1)
                                );
                            }
                        }

                        if (param.lessThanZero()) {
                            result.append("(").append(param.getOutputValue(false)).append(")");
                        } else {
                            result.append(param.getOutputValue(false));
                        }
                    } else {
                        /* cannot find parameter - leave it as it is*/
                        int cursor = -1;
                        while ((cursor = token.indexOf(TOKEN_DELIMITER, cursor + 1)) != -1) {
                            apos++;
                        }
                        result.append(token);
                        failedToFindParameter = true;
                        notFoundParams.append(token);
                        notFoundParams.append(",");
                    }
                } else {
                    int cursor = -1;
                    while ((cursor = token.indexOf(TOKEN_DELIMITER, cursor + 1)) != -1) {
                        apos++;
                    }
                    result.append(token);
                }
            }
            /* save modified string */
            string = result.toString();

            if (failedToFindParameter) {
                throw new AlgorithmException(UNDEFINED_VARIABLE_MESSAGE
                    + notFoundParams.toString().subSequence(0, notFoundParams.toString().length() - 1)
                    + IN_LINE_MESSAGE
                    + (currentLine + 1));
            }
        }
        return string;
    }

    /**
     * Returns true, if expression item is suitable for being an argument for a function.
     *
     * @param item as argument candidate
     * @return true, if expression item is suitable for being an argument for a function.
     */
    public static boolean isGoodNumericArgument(IExpressionItem item) {
        return (item instanceof AbstractConstant) || (item instanceof SimpleFraction);
    }

    /**
     * Returns numeric argument for a function. Accepts either constants or simple fractions.
     *
     * @param item as argument candidate
     * @return numeric argument for a function. Accepts either constants or simple fractions.
     */
    public static Constant getNumericArgument(IExpressionItem item) throws AlgorithmException {
        Constant result = null;
        if (item instanceof Constant) {
            result = (Constant) item;
        } else if (item instanceof SimpleFraction) {
            result = ((SimpleFraction) item).getProduct();
        }
        return result;
    }
}
