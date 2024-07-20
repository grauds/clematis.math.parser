// Created: 14.04.2005 T 11:18:19
package org.clematis.math.v1.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.StringConstant;
import org.clematis.math.v1.iExpressionItem;
import org.clematis.math.v1.iMultivariantLogic;
import org.clematis.math.v1.operations.SimpleFraction;
import org.clematis.math.utils.StringUtils;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.xpath.XPath;

/**
 * A collection of algorithm utilities
 */
public class AlgorithmUtils {
    /**
     * Algorithm XML may be static, i.e. has only empty <algorithm> elements.
     * In this case algorithm does nothing and considered empty
     *
     * @param algorithmXML XML representing the algorithm.
     * @return true if algorithm is empty
     */
    public static boolean isEmpty(Element algorithmXML) {
        try {
            boolean qu_params = false;
            XPath path = XPath.newInstance(".//param");
            List params = path.selectNodes(algorithmXML);
            qu_params = (params.size() != 0);

            boolean bsh_code = false;
            if (algorithmXML.getChild("code") != null) {
                bsh_code = !algorithmXML.getChildText("code").trim().equals("");
            }

            return !qu_params && !bsh_code;
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
    public static void removeAlgorithmicAttributes(List items) {
        for (int i = 0; i < items.size(); i++) {
            Element item = (Element) items.get(i);
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
    public static String substituteParameters(iParameterProvider algorithm, Element element) {
        StringBuilder sb = new StringBuilder();
        List list = element.getContent();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Object contentItem = it.next();
            if (contentItem instanceof Text) {
                sb.append(((Text) contentItem).getText());
            } else if (contentItem instanceof Element child) {
                if (child.getName().equals("variable") && child.getNamespacePrefix().equals("uni")) {
                    Parameter parameter = algorithm.getParameter(child.getAttributeValue("name"));
                    if (parameter != null) {
                        sb.append(parameter.getOutputValue(true));
                    }
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
     * @throws Exception
     */
    public static void findStringVariants(iParameterProvider provider,
                                          iExpressionItem exprRoot, HashSet<String> strList)
        throws Exception {
        if (exprRoot instanceof iMultivariantLogic logic) {
            ArrayList<iExpressionItem> variants = logic.getVariants();
            for (iExpressionItem variant : variants) {
                findStringVariants(provider, variant, strList);
            }
        } else if (exprRoot instanceof StringConstant) {
            String argumentString = ((StringConstant) exprRoot).getValue(null);
            /** this argument may contain parameter which is a multivariant logic instance */
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
     * @param currentLine
     * @param skipInsideStrings quoted parameters will be skipped,
     *                          if this flag is set
     * @return array list with parameter names, may be empty, never null
     */
    public static String replaceParameters(String string, iParameterProvider provider, int currentLine,
                                           boolean skipInsideStrings)
        throws AlgorithmException {
        if (provider != null && string != null && !string.trim().equals("")) {
            List<String> tokens;
            /** this parameter stops recursion if failed to find something, that looks like parameter */
            boolean failedToFindParameter = false;
            /** not found */
            StringBuffer notFoundParams = new StringBuffer();
            /** repeat until string has parameters */
            while ((tokens = StringUtils.tokenizeReg(string, Parameter.FIND_EXPRESSION, false)).size() > 1
                && !failedToFindParameter) {
                /** number of apos signs */
                int apos = 0;
                /** substitution step results */
                StringBuilder result = new StringBuilder();
                for (String token : tokens) {
                    /** filter only parameters */
                    if (isParameterName(token) && (!skipInsideStrings || apos % 2 == 0)) {
                        Parameter param = provider.getParameter(token);

                        if (param != null) {
                            if (param.getContainer() != null) {
                                Key key = param.getContainer().getParameterKey(token);
                                if (key != null && key.getLine() > currentLine) {
                                    throw new AlgorithmException(
                                        "Undefined variable: " + param.getName() + " in line " + (currentLine + 1));
                                }
                            }

                            if (param.lessThanZero()) {
                                result.append("(" + param.getOutputValue(false) + ")");
                            } else {
                                result.append(param.getOutputValue(false));
                            }
                        } else {
                            /** cannot find parameter - leave it as it is*/
                            int cursor = -1;
                            while ((cursor = token.indexOf("\"", cursor + 1)) != -1) {
                                apos++;
                            }
                            result.append(token);
                            failedToFindParameter = true;
                            notFoundParams.append(token);
                            notFoundParams.append(",");
                        }
                    } else {
                        int cursor = -1;
                        while ((cursor = token.indexOf("\"", cursor + 1)) != -1) {
                            apos++;
                        }
                        result.append(token);
                    }
                }
                /** save modified string */
                string = result.toString();
            }

            if (failedToFindParameter) {
                throw new AlgorithmException("Undefined variable: " +
                    notFoundParams.toString().subSequence(0, notFoundParams.toString().length() - 1) + " in line " +
                    (currentLine + 1));
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
    public static boolean isGoodNumericArgument(iExpressionItem item) {
        return (item instanceof AbstractConstant) || (item instanceof SimpleFraction);
    }

    /**
     * Returns numeric argument for a function. Accepts either constants or simple fractions.
     *
     * @param item as argument candidate
     * @return numeric argument for a function. Accepts either constants or simple fractions.
     */
    public static Constant getNumericArgument(iExpressionItem item) throws AlgorithmException {
        if (item instanceof Constant) {
            return (Constant) item;
        } else if (item instanceof SimpleFraction) {
            return ((SimpleFraction) item).getProduct();
        }
        return null;
    }
}
