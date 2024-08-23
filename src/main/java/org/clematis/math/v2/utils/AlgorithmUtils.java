// Created: 14.04.2005 T 11:18:19

package org.clematis.math.v2.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.clematis.math.v2.Constant;
import org.clematis.math.v2.StringConstant;
import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.Parameter;
import org.clematis.math.v2.algorithm.ParameterReference;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.clematis.math.v2.functions.aFunction;
import org.clematis.math.v2.operations.SimpleFraction;
import org.clematis.math.v2.parsers.Node;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Text;
import org.jdom2.xpath.XPath;

/**
 * A collection of algorithm utilities
 */
public class AlgorithmUtils {
    /**
     * Regular expression to find parameter in text
     */
    public final static String FIND_EXPRESSION = "(\\x24[a-zA-Z0-9_]+)|(\\x24\\x7B[a-zA-Z0-9_]+\\x7D)";

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
     * Processes element's content and replaces 'uni:variable' elements by
     * values of corresponding parameters.
     *
     * @param element element to process
     * @return String containing element text.
     */
    public static Element substituteParametersXML(iParameterProvider algorithm, Element element) {
        if (element != null) {
            ArrayList new_content = new ArrayList();
            List list = element.getContent();
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object contentItem = it.next();
                if (contentItem instanceof Content) {
                    contentItem = ((Content) contentItem).clone();
                    ((Content) contentItem).detach();
                }
                if (contentItem instanceof Element child) {
                    if (child.getName().equals("variable") && child.getNamespacePrefix().equals("uni")) {
                        Parameter parameter = algorithm.getParameter(child.getAttributeValue("token"));
                        if (parameter != null) {
                            new_content.add(new Text(parameter.getOutputValue(true)));
                            continue;
                        }
                    } else {
                        new_content.add(substituteParametersXML(algorithm, (Element) contentItem));
                        continue;
                    }
                }
                /**
                 * Set old content item
                 */
                new_content.add(contentItem);
            }
            /**
             * Replace old content with new one
             */
            element.setContent(new_content);
        }
        return element;
    }

    /**
     * Returns true if this string is a valid parameter token
     *
     * @param str string
     * @return true if this string is a valid parameter token
     */
    public static boolean isParameterName(String str) {
        return StringUtils.matchString(str, FIND_EXPRESSION, true);
    }

    /**
     * Returns true, if expression item is suitable for being an argument for a function.
     *
     * @param item as argument candidate
     * @return true, if expression item is suitable for being an argument for a function.
     */
    public static boolean isGoodNumericArgument(Node item) {
        return (item instanceof Constant) || (item instanceof SimpleFraction);
    }

    /**
     * Returns numeric argument for a function. Accepts either constants or simple fractions.
     *
     * @param item as argument candidate
     * @return numeric argument for a function. Accepts either constants or simple fractions.
     */
    public static Constant getNumericArgument(Node item) throws AlgorithmException {
        if (item instanceof Constant) {
            return (Constant) item;
        } else if (item instanceof SimpleFraction) {
            return ((SimpleFraction) item).getProduct();
        }
        return null;
    }

    //************************ FIND PARAMETERS' DEPENDENCIES *********************

    /**
     * Finds all parameters dependenced from the parameter.
     *
     * @param param the parameter from which the dependencies are searched.
     * @return ArrayList with parameters if exist or null.
     */
    public static ArrayList findDependencies(iParameterProvider parameterProvider, Parameter param) {
        ArrayList<Parameter> dependencies = null;
        if (param.getExpressionRoot() != null) {
            dependencies = new ArrayList<Parameter>();
            findDependencies(parameterProvider, param.getExpressionRoot(), dependencies);
        }
        return dependencies;
    }

    /**
     * Recursively processes the expression tree and picks up all Parameters objects.
     *
     * @param exprItem     the expression root.
     * @param dependencies container for dependencies.
     */
    private static void findDependencies(iParameterProvider parameterProvider, Node exprItem,
                                         ArrayList<Parameter> dependencies) {
        if (exprItem instanceof aFunction function) {
            Node[] args = function.getChildren();
            for (int i = 0; i < args.length; i++) {
                findDependencies(parameterProvider, args[i], dependencies);
            }
        } else if (exprItem instanceof StringConstant constant) {
            ArrayList paramNames = findParameters(constant.getValue(), parameterProvider, false);
            for (int i = 0; i < paramNames.size(); i++) {
                String name = (String) paramNames.get(i);
                Parameter param = parameterProvider.getParameter(name);
                if (param != null) {
                    dependencies.add(param);
                }
            }
        } else if (exprItem instanceof ParameterReference paramRef) {
            dependencies.add(paramRef.getOrigin());
        }
    }

    /**
     * Finds parameters in string. Quoted parameters will be skipped,
     * if flag "skipInsideStrings" is set
     *
     * @param string            the explored string
     * @param skipInsideStrings quoted parameters will be skipped,
     *                          if this flag is set
     * @return array list with parameter names, may be empty, never null
     */
    public static ArrayList<String> findParameters(String string,
                                                   iParameterProvider provider,
                                                   boolean skipInsideStrings) {
        ArrayList<String> result = new ArrayList<String>();
        if (provider != null && string != null && !string.trim().equals("")) {
            List<String> tokens = StringUtils.tokenizeReg(string, FIND_EXPRESSION, false);
            int apos = 0;
            for (String token : tokens) {
                if (provider.getParameter(token) != null &&
                    (!skipInsideStrings || apos % 2 == 0)) {
                    result.add(token);
                } else {
                    int cursor = -1;
                    while ((cursor = token.indexOf("\"", cursor + 1)) != -1) {
                        apos++;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Reaplces parameters in string.
     *
     * @param string the explored string
     * @return array list with parameter names, may be empty, never null
     */
    public static String replaceParameters(String string, iParameterProvider provider) {
        StringBuilder result = new StringBuilder();
        if (provider != null && string != null && !string.trim().equals("")) {
            List<String> tokens = StringUtils.tokenizeReg(string, FIND_EXPRESSION, false);
            for (String token : tokens) {
                if (provider.getParameter(token) != null) {
                    result.append(provider.getParameter(token).getOutputValue(false));
                } else {
                    result.append(token);
                }
            }
        }
        return result.toString();
    }
}
