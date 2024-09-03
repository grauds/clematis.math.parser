// Created: 24.12.2004 T 10:30:42
package org.clematis.math.v2.parsers.webtex;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.clematis.math.MathUtils;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;
import org.clematis.math.v2.parsers.WebTexParserTreeConstants;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.output.XMLOutputter;

/**
 * Converts webtex expression to content MathML
 *
 * @version 1.0
 * <p/>
 * converts webtex expression to content MathML
 */
public class WebTex2MMLConverter {
    private static final Map<String, String> s_entityMap;

    static {
        s_entityMap = new HashMap<String, String>();
        s_entityMap.put("\\alpha", "alpha");
        s_entityMap.put("\\beta", "beta");
        s_entityMap.put("\\gamma", "gamma");
        s_entityMap.put("\\delta", "delta");
        s_entityMap.put("\\epsilon", "epsilon");
        s_entityMap.put("\\varepsilon", "varepsilon");
        s_entityMap.put("\\zeta", "zeta");
        s_entityMap.put("\\eta", "eta");
        s_entityMap.put("\\theta", "theta");
        s_entityMap.put("\\vartheta", "vartheta");
        s_entityMap.put("\\iota", "iota");
        s_entityMap.put("\\kappa", "kappa");
        s_entityMap.put("\\lambda", "lambda");
        s_entityMap.put("\\mu", "mu");
        s_entityMap.put("\\nu", "nu");
        s_entityMap.put("\\xi", "xi");
        s_entityMap.put("\\o", "o");
        s_entityMap.put("\\varpi", "varpi");
        s_entityMap.put("\\rho", "rho");
        s_entityMap.put("\\varrho", "varrho");
        s_entityMap.put("\\sigma", "sigma");
        s_entityMap.put("\\tau", "tau");
        s_entityMap.put("\\upsilon", "upsilon");
        s_entityMap.put("\\phi", "phi");
        s_entityMap.put("\\varphi", "varphi");
        s_entityMap.put("\\chi", "chi");
        s_entityMap.put("\\psi", "psi");
        s_entityMap.put("\\omega", "omega");
        s_entityMap.put("\\Delta", "Delta");
        s_entityMap.put("\\Gamma", "Gamma");
        s_entityMap.put("\\Lambda", "Lambda");
        s_entityMap.put("\\Omega", "Omega");
        s_entityMap.put("\\Phi", "Phi");
        s_entityMap.put("\\Pi", "Pi");
        s_entityMap.put("\\Psi", "Psi");
        s_entityMap.put("\\Sigma", "Sigma");
        s_entityMap.put("\\Theta", "Theta");
        s_entityMap.put("\\Xi", "Xi");
        s_entityMap.put("\\Upsilon", "Upsilon");

    }

    boolean onlyFloatNumbers = true;

    public boolean isOnlyFloatNumbers() {
        return onlyFloatNumbers;
    }

    public void setOnlyFloatNumbers(boolean onlyFloatNumbers) {
        this.onlyFloatNumbers = onlyFloatNumbers;
    }

    public Element parseUserWebTexExpr(String webtexExpr) throws ParseException {
        String expr = prepareExpression(webtexExpr);
        return parseWebTexExpr(expr);
    }

    public Element parseWebTexExpr(String webtexExpr) throws ParseException {
        if (webtexExpr == null || webtexExpr.trim().length() == 0) {
            throw new ParseException("empty expression");
        }

        WebTexParser parser = new WebTexParser(new StringReader(webtexExpr));

        SimpleNode expressionNode = null;
        try {
            expressionNode = parser.Start();
        } catch (ParseException e) {
            throw e;
        } catch (TokenMgrError e) {
            throw new ParseException(e.getMessage());
        }

        Element math = new Element("math");
        if (expressionNode.jjtGetNumChildren() == 1) {
            buildMathML((SimpleNode) expressionNode.getChildren()[0], math);
        } else {
            throw new IllegalStateException();
        }
        return math;
    }

    private void buildMathML(SimpleNode parserNode, Element mmlParent) {
        Element element;
        switch (parserNode.getId()) {
            case WebTexParserTreeConstants.JJTEQUALS_EXPR:
                element = equalsExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTPLUS_EXPR:
                element = plusExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTMINUS_EXPR:
                element = minusExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTMULT_EXPR:
                element = timesExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTINDEX_EXPR:   //20050329 multuply expression for indexed vars -vtl
                element =
                    indexExpr(parserNode);            //20050606 special case - simple index is part of variable token
                break;
            case WebTexParserTreeConstants.JJTDIV_EXPR:
                element = divideExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTPOWER_EXPR:
                element = powerExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTVECTOR_EXPR:
                element = vectorExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTFUNCTION:
                element = functionExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTIDENTIFIER:
                element = identifier(parserNode);
                break;
            case WebTexParserTreeConstants.JJTNUMBER:
                element = number(parserNode);
                break;
            case WebTexParserTreeConstants.JJTVAR:
                element = parameter(parserNode);
                break;
            case WebTexParserTreeConstants.JJTUNARY_PLUS:
                element = unaryPlus(parserNode);
                break;
            case WebTexParserTreeConstants.JJTUNARY_MINUS:
                element = unaryMinus(parserNode);
                break;
            case WebTexParserTreeConstants.JJTCONSTANT:
                element = constant(parserNode);
                break;
            case WebTexParserTreeConstants.JJTDELTA_EXPR:
                element = deltaExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTHAT_EXPR:
                element = hatExpr(parserNode);
                break;
            case WebTexParserTreeConstants.JJTSTRING:
                element = new Element("cn");
                element.setText(parserNode.toString());
                break;
           /* case WebTexParserTreeConstants.JJTLINE:
                element = new Element("cn");
                element.setText(parserNode.toString());
                break;*/
            default:
                //log.error("element = " + parserNode.getId());
                element = new Element("error");

        }
        mmlParent.addContent(element);

    }

    private Element number(SimpleNode parserNode) {
        if (onlyFloatNumbers) {
            String number =
                Double.valueOf(parserNode.getTokenName()) + "";//do not print integer numbers - mapple requirement
            return new Element("cn").addContent(number);
        } else {
            String number = parserNode.getTokenName();
            number = MathUtils.convertRational(number);
            return new Element("cn").addContent(number);
        }
    }

    private Element constant(SimpleNode parserNode) {
        Element cn = new Element("cn").setAttribute("type", "constant");
        if (parserNode.getTokenKind() == WebTexParserConstants.EXP_E) {
            cn.addContent(new EntityRef("ee"));
        } else if (parserNode.getTokenKind() == WebTexParserConstants.PI) {
            cn.addContent(new EntityRef("pi"));
        }
        return cn;
    }

    private Element identifier(SimpleNode parserNode) {
        String image = parserNode.getTokenName();
        Element ci = new Element("ci");

        EntityRef eref = getEntityRef(image);
        if (eref != null) {
            ci.addContent(eref);
        } else {
            ci.addContent(parserNode.getTokenName());
        }

        return ci;
    }

    private Element parameter(SimpleNode parserNode) {
        String image = parserNode.getTokenName();
        Element variable = new Element("variable");//, Constants.NS_UNI);

        variable.setAttribute("token", image);

        return variable;
    }

    private Element unaryMinus(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("minus"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    private Element unaryPlus(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("plus"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    private Element deltaExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("times"));
        apply.addContent(new Element("cn").addContent("1.0"));

        if (parserNode != null && parserNode.getChildren() != null) {
            SimpleNode arg = (SimpleNode) parserNode.getChildren()[0];

            if (arg.getChildren() != null && arg.getChildren().length > 0) {
                buildMathML((SimpleNode) arg.getChildren()[0], apply);
            } else {
                buildMathML(arg, apply);
            }

            Element last = ciLast(apply);
            if (last != null) {
                EntityRef eRef = getChildEntityRef(last);
                String vname = eRef != null ? eRef.getName() : last.getTextTrim();
                last.setText("delt_" + vname);
            }
        }

        return apply;
    }

    private Element hatExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("times"));
        apply.addContent(new Element("cn").addContent("1.0"));
        SimpleNode arg = (SimpleNode) parserNode.getChildren()[0];

        if (arg.getChildren() != null && arg.getChildren().length > 0) {
            buildMathML((SimpleNode) arg.getChildren()[0], apply);
        } else {
            buildMathML(arg, apply);
        }

        Element last = ciLast(apply);
        if (last != null) {
            EntityRef eRef = getChildEntityRef(last);
            String vname = eRef != null ? eRef.getName() : last.getTextTrim();
            last.setText("hat_" + vname);
        }

        return apply;
    }

    private Element equalsExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("eq"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    private Element plusExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("plus"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    private Element timesExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("times"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    private Element indexExpr(SimpleNode parserNode) {
        SimpleNode childBase = (SimpleNode) parserNode.getChildren()[0];
        SimpleNode childIndex = (SimpleNode) parserNode.getChildren()[1];

        Element apply = new Element("apply");
        apply.addContent(new Element("times"));

        StringBuilder varIndex = new StringBuilder();
        Element apply1 = new Element("apply");

        processChildrenDefault((SimpleNode) parserNode.getChildren()[1], apply1);

        apply.getChildren();

        if (childIndex.getChildren() == null) {
            varIndex.append(childIndex.getTokenName());
        } else {
            isSimple(apply1, varIndex);
        }

        buildMathML(childBase, apply);

        Element last = ciLast(apply);
        if (last != null) {
            EntityRef eRef = getChildEntityRef(last);
            String vname = eRef != null ? eRef.getName() : last.getTextTrim();
            last.setText(vname + "_" + varIndex);
        }
        apply.addContent(new Element("cn").addContent("1.0"));


        return apply;
    }

    private Element ciLast(Element apply) {
        Element last = null;
        List children = apply.getChildren();

        for (Object child : children) {
            Element childEl = (Element) child;
            String name = childEl.getName();
            if ("ci".equals(name)) {
                last = childEl;
            } else if ("apply".equals(name)) {
                last = ciLast(childEl);

            }

        }
        return last;
    }

    private boolean isSimple(Element apply, StringBuilder varName) {
//        expr = s_i_1
//        MML
//        <math><ci>s_i_1</ci></math>
//        ---------------------------
//        expr = s_i1
//        MML
//        <math><apply><times /><ci>s_i</ci><cn>1.0</cn></apply></math>
//        ---------------------------
//        expr = s_(i1)
//        MML
//        <math><ci>s_i_1</ci></math>

        List children = apply.getChildren();
        boolean isSimple = true;

        for (Object child : children) {
            Element childEl = (Element) child;
            String name = childEl.getName();
            if ("times".equals(name)) {
                //skip
            } else if ("ci".equals(name)) {
                EntityRef eRef = getChildEntityRef(childEl);
                String vname = eRef != null ? eRef.getName() : childEl.getTextTrim();
                varName.append(vname);
                varName.append("_");
            } else if ("cn".equals(name)) {
                String type = childEl.getAttributeValue("type");
                if (type != null && type.equals("constant")) {
                    EntityRef eRef = getChildEntityRef(childEl);
                    if (eRef != null) {
                        varName.append(eRef.getName());
                        varName.append("_");
                    }
                } else {
                    double value = Double.valueOf(childEl.getTextTrim());
                    varName.append((int) Math.round(value));
                    varName.append("_");
                }
            } else if ("apply".equals(name)) {
                isSimple = isSimple(childEl, varName);
                if (!isSimple) {
                    break;
                }
            } else {
                isSimple = false;
                break;
            }
        }
        return isSimple;
    }

    private Element minusExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        minusExpr(apply, parserNode, parserNode.getChildren().length - 1);
        return apply;
    }

    private void minusExpr(Element apply, SimpleNode parserNode, int position) {
        apply.addContent(new Element("minus"));

        if (position > 1) {
            Element childApply = new Element("apply");
            apply.addContent(childApply);
            minusExpr(childApply, parserNode, position - 1);
        } else {
            buildMathML((SimpleNode) parserNode.getChildren()[position - 1], apply);
        }

        buildMathML((SimpleNode) parserNode.getChildren()[position], apply);
    }

    private Element divideExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        divideExpr(apply, parserNode, parserNode.getChildren().length - 1);
        return apply;
    }

    private void divideExpr(Element apply, SimpleNode parserNode, int position) {
        apply.addContent(new Element("divide"));

        if (position > 1) {
            Element childApply = new Element("apply");
            apply.addContent(childApply);
            divideExpr(childApply, parserNode, position - 1);
        } else {
            buildMathML((SimpleNode) parserNode.getChildren()[position - 1], apply);
        }

        buildMathML((SimpleNode) parserNode.getChildren()[position], apply);
    }

    private Element powerExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        powerExpr(apply, parserNode, parserNode.getChildren().length - 1);
        return apply;
    }

    private void powerExpr(Element apply, SimpleNode parserNode, int position) {
        apply.addContent(new Element("power"));

        if (position > 1) {
            Element childApply = new Element("apply");
            apply.addContent(childApply);
            powerExpr(childApply, parserNode, position - 1);
        } else {
            buildMathML((SimpleNode) parserNode.getChildren()[position - 1], apply);
        }

        buildMathML((SimpleNode) parserNode.getChildren()[position], apply);
    }

    private Element vectorExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("times"));
        apply.addContent(new Element("cn").addContent("1.0"));
        SimpleNode arg = (SimpleNode) parserNode.getChildren()[0];

        if (arg.getChildren() != null && arg.getChildren().length > 0) {
            buildMathML((SimpleNode) arg.getChildren()[0], apply);
        }

        Element last = ciLast(apply);
        if (last != null) {
            EntityRef eRef = getChildEntityRef(last);
            String vname = eRef != null ? eRef.getName() : last.getTextTrim();
            last.setText("vector_" + vname);
        }

        return apply;
    }

    private Element functionExpr(SimpleNode parserNode) {
        String fname = parserNode.getTokenName();
        if ("sqrt".equals(fname)) {
            return functionSqrt(parserNode);
        } else if ("root".equals(fname)) {
            return functionRoot(parserNode);
        } else if (fname.equals("frac")) {
            fname = "divide";
        } else if (fname.equals("log")) {
            //log(10, x) => lg(x)
            //log(e , x) => ln(x)
            Node[] children = parserNode.getChildren();
            if (children.length == 2) {
                SimpleNode powerExpr = (SimpleNode) children[0].getChildren()[0];
                if (powerExpr.getId() == WebTexParserTreeConstants.JJTCONSTANT
                    && powerExpr.getTokenKind() == WebTexParserConstants.EXP_E) {
                    return functionLogSpecialCase(parserNode, "ln");
                } else if (powerExpr.getId() == WebTexParserTreeConstants.JJTNUMBER
                    && Double.valueOf(powerExpr.getTokenName()) == 10.0) {
                    return functionLogSpecialCase(parserNode, "log");
                }
            }
        } else if (fname.equals("lg")) {
            fname = "log";
        }

        Element apply = new Element("apply");
        apply.addContent(new Element(fname));

        for (int i = 0; i < parserNode.getChildren().length; i++) {
            Node child = parserNode.getChildren()[i];
            SimpleNode arg = (SimpleNode) child;
            buildMathML((SimpleNode) arg.getChildren()[0], apply);
        }

        return apply;
    }

    private Element functionSqrt(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("power"));

        SimpleNode arg = (SimpleNode) parserNode.getChildren()[0];
        buildMathML((SimpleNode) arg.getChildren()[0], apply);

        Element divExpr = new Element("apply");
        divExpr.addContent(new Element("divide"));
        divExpr.addContent(new Element("cn").addContent("1.0"));
        divExpr.addContent(new Element("cn").addContent("2.0"));

        apply.addContent(divExpr);

        return apply;
    }

    private Element functionLogSpecialCase(SimpleNode parserNode, String fname) {
        Element apply = new Element("apply");
        apply.addContent(new Element(fname));

        Node child = parserNode.getChildren()[1];
        SimpleNode arg = (SimpleNode) child;
        buildMathML((SimpleNode) arg.getChildren()[0], apply);// child[0]->ARG/EXPR

        return apply;
    }

    private Element functionRoot(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("power"));

        SimpleNode childDegreeArg = (SimpleNode) parserNode.getChildren()[0];
        SimpleNode childExprArg = (SimpleNode) parserNode.getChildren()[1];

        buildMathML((SimpleNode) childExprArg.getChildren()[0], apply);

        Element divExpr = new Element("apply");
        divExpr.addContent(new Element("divide"));
        divExpr.addContent(new Element("cn").addContent("1.0"));
        buildMathML((SimpleNode) childDegreeArg.getChildren()[0], divExpr);

        apply.addContent(divExpr);

        return apply;
    }

    private void processChildrenDefault(SimpleNode parserNode, Element apply) {
        if (parserNode.getChildren() == null) {
            return;
        }
        for (int i = 0; i < parserNode.getChildren().length; i++) {
            Node child = parserNode.getChildren()[i];
            buildMathML((SimpleNode) child, apply);
        }
    }

    private static EntityRef getChildEntityRef(Element element) {
        List content = element.getContent();
        for (Object obj : content) {
            if (obj instanceof EntityRef) {
                return (EntityRef) obj;
            }
        }
        return null;
    }

    private static EntityRef getEntityRef(String webtexEntity) {
        String entytyRef = s_entityMap.get(webtexEntity);
        if (entytyRef != null) {
            return new EntityRef(entytyRef);
        } else {

            return null;
        }
    }

    /**
     * preprocessor
     * prepare string to parse
     *
     * @param webtexExpr expression with known flash errors
     */
    private static String prepareExpression(String webtexExpr) {
        StringBuilder expr = new StringBuilder(webtexExpr);
        int pos;
        while ((pos = getExcessBracketPosition(expr)) != -1) {
            int length = expr.length();
            removeExcessBracket(expr, pos);
            if (length == expr.length()) {
                break;
            }
        }

        webtexExpr = expr.toString();

        {
            webtexExpr = webtexExpr.replace('{', '(');
            webtexExpr = webtexExpr.replace('}', ')');
            webtexExpr = webtexExpr.replaceAll("\\(\\)", "");
        }

        return webtexExpr;
    }

    private static int getExcessBracketPosition(StringBuilder expr) {
        int pos = expr.indexOf("{+");
        if (pos == -1) {
            pos = expr.indexOf("{*");
        }
        if (pos == -1) {
            pos = expr.indexOf("{/");
        }
        if (pos == -1) {
            pos = expr.indexOf("{^");
        }
        if (pos == -1) {
            pos = expr.indexOf("{-}");
        }
        if (pos == -1) {
            pos = expr.indexOf("-{");
        }
        if (pos == -1) {
            pos = expr.indexOf("*{");
        }
        if (pos == -1) {
            pos = expr.indexOf("/{");
        }
        if (pos == -1) {
            pos = expr.indexOf("+{");
        }

        return pos;
    }

    private static void removeExcessBracket(StringBuilder expr, int pos) {
        int counter = 0;
        int openPos = pos;
        for (int i = pos; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '{') {
                if (counter == 0) {
                    openPos = i;
                }
                counter++;
            } else if (c == '}') {
                counter--;
                if (counter == 0) {
                    expr.deleteCharAt(i);
                    expr.deleteCharAt(openPos);
                    return;
                }
            }
        }
    }

    private static void print(Element mml) {
        XMLOutputter xout = new XMLOutputter();
        try {
            xout.output(mml, System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.flush();
    }
}