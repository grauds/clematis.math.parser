// Created: 24.12.2004 T 10:30:42
package org.clematis.math.reference.parser.webtex;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.output.XMLOutputter;

/**
 *
 */
public class WebTex2MMLConverter {
    private static final Map<String, String> s_entityMap;

    static {
        s_entityMap = new HashMap<>();
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
        //s_entityMap.put( "\\pi",  "pi");
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

    /**
     * correct some errors and parse webtex expression
     *
     * @param webtexExpr
     * @throws ParseException
     */
    public Element parseUserWebTexExpr(String webtexExpr) throws ParseException {
        String expr = prepareExpression(webtexExpr);
        return parseWebTexExpr(expr);
    }

    /**
     * parse webtex expression
     *
     * @param webtexExpr
     * @throws ParseException
     */
    public Element parseWebTexExpr(String webtexExpr) throws ParseException {
        if (webtexExpr == null || webtexExpr.trim().isEmpty()) {
            throw new ParseException("empty expression");
        }

        //webtexExpr = prepareExpression(webtexExpr);

        WebTexParser parser = new WebTexParser(new StringReader(webtexExpr));
        SimpleNode expressionNode = parser.Start();

        //expressionNode.dump("");
        Element math = new Element("math");
        if (expressionNode.jjtGetNumChildren() == 1) {
            buildMathML((SimpleNode) expressionNode.children[0], math);
        } else {
            throw new IllegalStateException();
        }
        return math;
    }

    private void buildMathML(SimpleNode parserNode, Element mmlParent) {
        Element element = switch (parserNode.getId()) {
            case WebTexParserTreeConstants.JJTEQUALS_EXPR -> equalsExpr(parserNode);
            case WebTexParserTreeConstants.JJTPLUS_EXPR -> plusExpr(parserNode);
            case WebTexParserTreeConstants.JJTMINUS_EXPR -> minusExpr(parserNode);
            case WebTexParserTreeConstants.JJTMULT_EXPR -> timesExpr(parserNode);
            case WebTexParserTreeConstants.JJTINDEX_EXPR ->   //20050329 multuply expression for indexed vars -vtl
                indexExpr(parserNode);            //20050606 special case - simple index is part of variable name
            case WebTexParserTreeConstants.JJTDIV_EXPR -> divideExpr(parserNode);
            case WebTexParserTreeConstants.JJTPOWER_EXPR -> powerExpr(parserNode);
            case WebTexParserTreeConstants.JJTFUNCTION -> functionExpr(parserNode);
            case WebTexParserTreeConstants.JJTNUMBER -> number(parserNode);
            case WebTexParserTreeConstants.JJTVAR -> var(parserNode);
            case WebTexParserTreeConstants.JJTUNARY_MINUS -> unaryMinus(parserNode);
            case WebTexParserTreeConstants.JJTCONSTANT -> constant(parserNode);
            default -> new Element("error");
        };
        mmlParent.addContent(element);

    }

    private Element number(SimpleNode parserNode) {

        String number =
            Double.valueOf(parserNode.getTokenImage()) + "";//do not print integer numbers - mapple requirement
        return new Element("cn").addContent(number);
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

    private Element var(SimpleNode parserNode) {

        String image = parserNode.getTokenImage();
        Element ci = new Element("ci");

        EntityRef eref = getEntityRef(image);
        if (eref != null) {
            ci.addContent(eref);
        } else {
            ci.addContent(parserNode.getTokenImage());
        }

        return ci;

        /*
        //20050322 returns multuply expression for indexed vars (a_b_c -> a*b*c)  -vtl
        if (parserNode.children == null || parserNode.children.length == 0)
        {
            return ci;
        }
        else
        {
            Element apply = new Element("apply").addContent(new Element("times"));
            apply.addContent(ci);
            processChildrenDefault(parserNode, apply);
            return apply;
        }
        */
    }

    private Element unaryMinus(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("minus"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    //n-ary arithmetic
    private Element equalsExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("eq"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    //n-ary arithmetic
    private Element plusExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("plus"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    //n-ary arithmetic
    private Element timesExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("times"));
        processChildrenDefault(parserNode, apply);
        return apply;
    }

    private Element indexExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("times"));
        processChildrenDefault(parserNode, apply);

        //try convert expression to simple name

        //analyse times arguments
        StringBuilder varName = new StringBuilder();
        boolean isSimple = isSimple(apply, varName);
        if (isSimple) {
            apply = new Element("ci").addContent(varName.substring(0, varName.length() - 1));
        }

//        try
//        {
//            parserNode.dump("   ");
//            XMLOutputter xout = new XMLOutputter();
//            xout.output(apply, System.out);
//        } catch (IOException e)
//        {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        return apply;
    }

    /**
     * test what expression contains only ci, cn and times apply
     * if true - build varname
     *
     * @param apply
     * @param varName
     */
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
                varName.append(childEl.getTextTrim());
                varName.append("_");
            } else if ("cn".equals(name)) {
                String type = childEl.getAttributeValue("type");
                if (type != null && type.equals("constant")) {
                    varName.append(((EntityRef) childEl.getContent(0)).getName());
                    varName.append("_");
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

    //binary arithmetic
    private Element minusExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        minusExpr(apply, parserNode, parserNode.children.length - 1);
        return apply;
    }

    private void minusExpr(Element apply, SimpleNode parserNode, int position) {
        apply.addContent(new Element("minus"));

        if (position > 1) {
            Element childApply = new Element("apply");
            apply.addContent(childApply);
            minusExpr(childApply, parserNode, position - 1);
        } else {
            buildMathML((SimpleNode) parserNode.children[position - 1], apply);
        }

        buildMathML((SimpleNode) parserNode.children[position], apply);
    }

    //binary arithmetic
    private Element divideExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        divideExpr(apply, parserNode, parserNode.children.length - 1);
        return apply;
    }

    private void divideExpr(Element apply, SimpleNode parserNode, int position) {
        apply.addContent(new Element("divide"));

        if (position > 1) {
            Element childApply = new Element("apply");
            apply.addContent(childApply);
            divideExpr(childApply, parserNode, position - 1);
        } else {
            buildMathML((SimpleNode) parserNode.children[position - 1], apply);
        }

        buildMathML((SimpleNode) parserNode.children[position], apply);
    }

    //binary arithmetic
    private Element powerExpr(SimpleNode parserNode) {
        Element apply = new Element("apply");
        powerExpr(apply, parserNode, parserNode.children.length - 1);
        return apply;
    }

    private void powerExpr(Element apply, SimpleNode parserNode, int position) {
        apply.addContent(new Element("power"));

        if (position > 1) {
            Element childApply = new Element("apply");
            apply.addContent(childApply);
            powerExpr(childApply, parserNode, position - 1);
        } else {
            buildMathML((SimpleNode) parserNode.children[position - 1], apply);
        }

        buildMathML((SimpleNode) parserNode.children[position], apply);
    }

    private Element functionExpr(SimpleNode parserNode) {
        String fname = parserNode.getTokenImage();
        if ("sqrt".equals(fname)) {
            return functionSqrt(parserNode);
        } else if ("root".equals(fname)) {
            return functionRoot(parserNode);
        } else if (fname.equals("frac")) {
            fname = "divide";
        }

        //process by default

        Element apply = new Element("apply");
        apply.addContent(new Element(fname));

        /*
        for (Node child : parserNode.children)
        {
            SimpleNode arg = (SimpleNode)child;
            buildMathML((SimpleNode) arg.children[0], apply);// child[0]->ARG/EXPR
        }
        */
        for (int i = 0; i < parserNode.children.length; i++) {
            Node child = parserNode.children[i];
            SimpleNode arg = (SimpleNode) child;
            buildMathML((SimpleNode) arg.children[0], apply);// child[0]->ARG/EXPR
        }


        return apply;

    }

    private Element functionSqrt(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("root"));
        apply.addContent(new Element("degree").addContent(new Element("cn").addContent("2")));

        Node child = parserNode.children[0];
        SimpleNode arg = (SimpleNode) child;
        buildMathML((SimpleNode) arg.children[0], apply);// child[0]->ARG/EXPR

        return apply;
    }

    /**
     * <apply>
     * <root/>
     * <degree><ci type='integer'> n </ci></degree>
     * <ci> a </ci>
     * </apply>
     *
     * @param parserNode
     */

    private Element functionRoot(SimpleNode parserNode) {
        Element apply = new Element("apply");
        apply.addContent(new Element("root"));

        Element degree = new Element("degree");
        SimpleNode childDegreeArg = (SimpleNode) parserNode.children[0];
        buildMathML((SimpleNode) childDegreeArg.children[0], degree);
        apply.addContent(degree);

        SimpleNode childExprArg = (SimpleNode) parserNode.children[1];
        buildMathML((SimpleNode) childExprArg.children[0], apply);

        return apply;
    }


    private void processChildrenDefault(SimpleNode parserNode, Element apply) {
        /*
        for (Node child : parserNode.children)
        {
            buildMathML((SimpleNode)child, apply);
        }
        */
        for (int i = 0; i < parserNode.children.length; i++) {
            Node child = parserNode.children[i];
            buildMathML((SimpleNode) child, apply);
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    private static EntityRef getEntityRef(String webtexEntity) {
        String entytyRef = (String) s_entityMap.get(webtexEntity);
        if (entytyRef != null) {
            return new EntityRef(entytyRef);
        } else {

            return null;
        }
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    /**
     * preprocessor
     * prepare string to parse
     *
     * @param webtexExpr expression with known flash errors
     */
    private static String prepareExpression(String webtexExpr) {

        //System.out.println("**BEFORE************webtexExpr = " + webtexExpr);
        StringBuilder expr = new StringBuilder(webtexExpr);
        int pos;
        while ((pos = getExcessBracketPosition(expr)) != -1) {
            int length = expr.length();
            removeExcessBracket(expr, pos);
            if (length == expr.length()) {
                break; //avoid cycling for incorrect input, for example "-{e-{2x+1}"
            }
        }

        webtexExpr = expr.toString();

        {//merge parenthesis
            webtexExpr = webtexExpr.replace('{', '(');
            webtexExpr = webtexExpr.replace('}', ')');
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
                    //System.out.println("**AFTER************webtexExpr = " + expr);
                    return;
                }
            }
        }
    }

    public static void main(String[] args) throws ParseException {
        WebTex2MMLConverter conv = new WebTex2MMLConverter();
        String[] expr = new String[]
            {
                "s_(def)",
                "s_i_1",
                "s_i1",
                "s_(i1)",
                "v_0sqrt(2g/h/g)",
                "a_i_j",
                "a_(i_j)",
                "a_(ij)",
                "a_(i^j)",
                "e-{2x+1}",
                "-1+x",
                "(8x+4)/ (-1+x)",
                "a{+{b-c{*{-y}}}-x}+5",
                "-x^i",
                "-(x+1)/(-2)",
                "e+e^e^e^e^e",
                "({x+1)}",
                "\\frac{{{v}_{0}^{2}}}{2g}",
                "\\frac{{v}_{0}^{2}}{2g}",
                "{x+1}_{y+2}^{3}",
                "14*t+1",
                "f_1f_2",
                "a_b_c",
                "a_{b_c}",
                "a_{b_{c+1}}",
                "a1",
                "a_1",
                //"a_2+m^{2n}_{i+1}", error
                "a_2+m_{i+1}^{2n}",
                "L_{b^i}",
                "x+\\frac{x}{2}+\\frac{a_2}{\\gamma^2}",
                "a_2_3+b-c-d+e+f/g/h*i*j",
                "\\frac {a} {b}",
                "\\sinx*a/(b-c -d) = 10",
                "\\sinx*a/(b-ce -d*e) = 10",
                "a-bc-d",
                "x^4\\cos{7x}-7x^5\\sin{7x}",
                "a+b_i\\cdotc_5",

                //"2a--b",
                "2a-(-b)",

                "6*e^(2*\\sin(3*x))*\\csc(3*\\pi)",
                "\\pie",
                "10e+10",
                "e+e^x+a^e^x",
                "\\sqrt x",
                "\\root y x",
                "\\alpha*x",
                "\\Pi\\Omega",
                "6e^{2\\sin{3x}}\\cos{3x}",
                "a2+2a+A_i_j",
            };

        for (int i = 0; i < expr.length; i++) {
            System.out.println("expr = " + expr[i]);
            Element mml = conv.parseUserWebTexExpr(expr[i]);
            System.out.println("MML");
            print(mml);
            System.out.println("\n---------------------------");
        }

        System.out.println("5 = " + (double) 5e-10);
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