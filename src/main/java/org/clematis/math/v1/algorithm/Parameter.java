// Created: Jan 21, 2003 T 5:23:42 PM
package org.clematis.math.v1.algorithm;

import java.util.HashSet;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.ExpressionParser;
import org.clematis.math.v1.OutputFormatSettings;
import org.clematis.math.v1.iExpressionItem;
import org.clematis.math.v1.operations.SimpleFraction;
import org.clematis.math.v2.utils.StringUtils;
import org.jdom2.CDATA;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Parameter is a special constant, that is a product of
 * algorithm work, done before formula is presented to a student.
 */
@Setter
@Getter
public class Parameter extends SimpleParameter {
    /**
     * Special condition name of parameter
     */
    public static final String CONDITION_NAME = "condition";
    /**
     * Precision of outcoming values
     * for presentation.
     */
    public static final int PRECISION = 12;
    /**
     * Equals sign constant
     */
    public static final String EQUALS_SIGN = "=";
    /**
     * Colon sign constant
     */
    public static final String COLON = ": ";
    /**
     * Name of XML element for parameter
     */
    public static final String PARAM_ELEMENT_NAME = "param";
    /**
     * Name of the XML attribute for parameter XML element
     */
    public static final String NAME_ATTRIBUTE_NAME = "name";
    /**
     * Name of the XML element for a constant
     */
    public static final String CONSTANT_ELEMENT_NAME = "constant";
    /**
     * Name of the XML attribute for answer
     */
    public static final String ANSWER_ATTRIBUTE_NAME = "answer";
    /**
     * Name of the XML attribute for sig
     */
    public static final String SIG_ATTRIBUTE_NAME = "sig";
    /**
     * Name of the XML attribute for sd applicable
     */
    public static final String SDAPPLICABLE_ATTRIBUTE_NAME = "sdapplicable";
    /**
     * Name of the XML attribute for sd independent
     */
    public static final String SDINDEPENDENT_ATTRIBUTE_NAME = "sdindependent";
    /**
     * Name of the XML attribute for answer identifier
     */
    public static final String ANSWER_IDENT_ATTRIBUTE_NAME = "answer_ident";
    /**
     * Name of the XML attribute for MathML flag
     */
    public static final String CONTAINS_MATHML_ATTRIBUTE_NAME = "contains_mathml";
    /**
     * Name of the XML attribute for code
     */
    public static final String CODE_ELEMENT_NAME = "code";
    /**
     * Parameter can be a condition - the result of calculation can be taken to break
     * algorithm calculation and to start calculating over again.
     */
    protected boolean condition = false;
    /**
     * Object representation of the expression code.
     */
    private iExpressionItem expressionRoot = null;
    /**
     * Calculated qu algorithm, this parameter belongs to
     */
    private Algorithm container = null;
    /**
     * Textual representation of the expression code.
     */
    private String code = null;
    /**
     * Is this parameter a correct answer representation
     */
    private boolean correctAnswer = false;
    /**
     * Correct answer ident. This is an ident from varequal,
     * should be not null if correctAnswer flag is set to true.
     */
    private String correctAnswerIdent = null;
    /**
     * This flag allows application of significant digits.
     */
    private boolean sdApplicable = true;
    /**
     * Sd applicable impossible to turn off by tolerance level
     */
    private boolean sdIndependent = false;
    /**
     * Number of significant digits can be established from outside.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private int sdNumber = 3;
    /**
     * Is this parameter contains MathML code
     */
    private boolean containsMathML = false;
    /**
     * Constructor.
     *
     * @param name parameter name.
     * @param code algorithmic code.
     */
    public Parameter(String name, String code) {
        setName(name);
        setCode(code);
    }

    /**
     * Constructor.
     *
     * @param name          parameter name.
     * @param currentResult <code>Constant</code> representing parameter value.
     */
    public Parameter(String name, AbstractConstant currentResult) {
        setName(name);
        setCurrentResult(currentResult);
    }

    /**
     * Calculates parameter value.
     * It is an <code>AbstractConstant</code> object.
     */
    void calculate(Algorithm paramProvider, int currentLine) throws AlgorithmException {
        /*
         * If code is not null, Parameter is mutable, if null - immutable via calculation
         */
        iExpressionItem result = null;
        if (code != null) {
            try {
                //** parse processed code string */
                ExpressionParser exprParser = new ExpressionParser(
                    AlgorithmUtils.replaceParameters(code, paramProvider, currentLine, false),
                    paramProvider,
                    null,
                    paramProvider
                );
                expressionRoot = exprParser.parse();
                //** calculate expression root */
                if (expressionRoot != null) {
                    result = expressionRoot.calculate(paramProvider);
                }
            } catch (AlgorithmException ex) {
                throw new AlgorithmException("Error in "
                    + this.getName()
                    + EQUALS_SIGN
                    + code
                    + COLON
                    + ex.getMessage());
            }
            if (result instanceof AbstractConstant) {
                setCurrentResult((AbstractConstant) result);
            } else if (result instanceof SimpleFraction) {
                setCurrentResult(((SimpleFraction) result).getProduct());
            } else {
                throw new AlgorithmException("Parameter: "
                    + this.getName()
                    + EQUALS_SIGN
                    + code
                    + " is not a CONSTANT: "
                    + (result == null ? null : result.toString()));
            }
        }
    }

    /**
     * Calculates parameter value.
     * It is an <code>AbstractConstant</code> object.
     */
    void parseWithFullTree(Algorithm paramProvider)
        throws AlgorithmException {
        parse(paramProvider, ExpressionParser.MODE_COPY_PARAMETER_AS_TREE);
    }

    /**
     * Calculates parameter value.
     * It is an <code>AbstractConstant</code> object.
     */
    void parseWithReferences(Algorithm paramProvider)
        throws AlgorithmException {
        parse(paramProvider, ExpressionParser.MODE_COPY_PARAMETER_AS_REFERENCE);
    }

    private void parse(Algorithm paramProvider, int mode)
        throws AlgorithmException {
        try {
            ExpressionParser exprParser = new ExpressionParser(code, paramProvider, null, paramProvider);
            exprParser.setMode(mode);
            expressionRoot = exprParser.parse();
        } catch (Exception ex) {
            throw new AlgorithmException("Failed parsing of expression root for parameter: "
                + this.getName()
                + EQUALS_SIGN
                + code
                + COLON
                + ex.getMessage());
        }
    }

    /**
     * Sets the parameter value. May be null.
     *
     * @param constant the parameter value
     */
    void setCurrentResult(AbstractConstant constant) {
        super.setCurrentResult(constant);
        if (getCurrentResult() != null) {
            getCurrentResult().setParameter(this);
        }
    }

    /**
     * This function checks whether if code contains root, and if it doesn't,
     * tries to supplement boolean root, "and" or "or" functions. This works only
     * if all parts of code string return 1.0 or 0.0 values. Overwise, constant
     * will be never yeilded.
     *
     * @param code string
     */
    void setCode(String code) {
        this.code = code;
    }

    /**
     * Sets parameter name
     *
     * @param name of parameter
     */
    void setName(String name) {
        super.setName(name);
        if (name.equals(Parameter.CONDITION_NAME)) {
            condition = true;
        }
    }

    public boolean isConditionPassed() {

        if (getCurrentResult() != null
            && getCurrentResult() instanceof Constant
            && isCondition()
        ) {
            return ((Constant) getCurrentResult()).getNumber() != 0;
        }

        return true;
    }

    void setContainer(Algorithm container) {
        this.container = container;
    }

    void setCorrectAnswerIdent(String correctAnswerIdent) {
        this.correctAnswerIdent = correctAnswerIdent;
    }

    public boolean getSdEnable() {
        if (container != null && container.getFormatSettings() != null) {
            return isSdApplicable() && container.getFormatSettings().getSdEnable();
        } else {
            return this.isSdApplicable();
        }
    }

    public boolean isSdApplicable() {
        final boolean pureApplicable = sdApplicable && !isZero();

        if (container != null && container.getFormatSettings() != null) {
            if (!isSdIndependent()) {
                return !container.getFormatSettings().isqTolerance() || pureApplicable;
            }
        }

        return pureApplicable;
    }
    /**
     * Gets text representation of parameter value.
     *
     * @param format flag
     * @return <code>String</code> representing parameter value.
     */
    public String getOutputValue(boolean format) {
        if (getCurrentResult() != null) {
            OutputFormatSettings fs = new OutputFormatSettings();
            if (format && getContainer() != null) {
                fs = getContainer().getFormatSettings();
            }
            return getCurrentResult().getValue(fs);
        }
        return code;
    }

    private boolean isZero() {
        if (getCurrentResult() != null) {
            return getCurrentResult().isZero();
        }
        return false;
    }

    /**
     * Saves parameter calculation results into simple element
     * <param name="$a">
     * 53.78945643
     * </param>
     *
     * @return <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    public Element saveResult() {
        Element paramElement = new Element(PARAM_ELEMENT_NAME);
        paramElement.setAttribute(NAME_ATTRIBUTE_NAME, getName());

        if (getCurrentResult() != null) {
            paramElement.addContent(getCurrentResult().toXML());
        }

        return paramElement;
    }

    /**
     * Loads parameter calculation results from simple element
     * <param name="$a">
     * 53.78945643
     * </param>
     *
     * @param paramElement <code>Element</code> representing root of calculated algorithm's JDOM.
     */
    static Parameter loadResult(Element paramElement) {
        if (paramElement != null) {
            String name = paramElement.getAttributeValue(NAME_ATTRIBUTE_NAME);
            AbstractConstant c = null;
            if (paramElement.getChild(CONSTANT_ELEMENT_NAME) != null) {
                c = AbstractConstant.create(paramElement.getChild(CONSTANT_ELEMENT_NAME));
            }
            return new Parameter(name, c);
        }
        return null;
    }

    /**
     * Serialize parameter to XML element
     *
     * @return xml representation of this parameter
     */
    public Element toXML() {
        Element paramElement = new Element(PARAM_ELEMENT_NAME);
        if (getName() != null) {
            paramElement.setAttribute(NAME_ATTRIBUTE_NAME, getName());
        }
        if (isCorrectAnswer()) {
            paramElement.setAttribute(ANSWER_ATTRIBUTE_NAME, Boolean.TRUE.toString());
            /*
             * Store numeric policy only for correct answer
             */
            paramElement.setAttribute(SIG_ATTRIBUTE_NAME, Integer.toString(sdNumber));
            paramElement.setAttribute(SDAPPLICABLE_ATTRIBUTE_NAME, Boolean.toString(isSdApplicable()));
            paramElement.setAttribute(SDINDEPENDENT_ATTRIBUTE_NAME, Boolean.toString(isSdIndependent()));
        }
        if (getCorrectAnswerIdent() != null) {
            paramElement.setAttribute(ANSWER_IDENT_ATTRIBUTE_NAME, getCorrectAnswerIdent());
        }
        if (isCondition()) {
            paramElement.setAttribute(Parameter.CONDITION_NAME, Boolean.TRUE.toString());
        }
        if (containsMathML) {
            paramElement.setAttribute(CONTAINS_MATHML_ATTRIBUTE_NAME, Boolean.TRUE.toString());
        }
        /*
         * Add formula code
         */
        Element codeElement = new Element(CODE_ELEMENT_NAME);
        codeElement.addContent(new CDATA(code));
        paramElement.addContent(codeElement);

        return paramElement;
    }

    /**
     * Create parameter from XML element
     *
     * @param xml representation of this parameter
     * @return parameter
     */
    public static Parameter create(Element xml) {
        /*
         * Create constant
         */
        AbstractConstant constant = null;
        if (xml.getChild(CONSTANT_ELEMENT_NAME) != null) {
            constant = AbstractConstant.create(xml.getChild(CONSTANT_ELEMENT_NAME));
        }
        /*
         * Create constant wrapper - parameter
         */
        Parameter param = new Parameter(xml.getAttributeValue(NAME_ATTRIBUTE_NAME), constant);
        /*
         * Read parameter configuration.
         */
        if (xml.getAttribute(ANSWER_ATTRIBUTE_NAME) != null) {
            param.setCorrectAnswer(true);
        }
        if (xml.getAttribute(ANSWER_IDENT_ATTRIBUTE_NAME) != null) {
            param.setCorrectAnswerIdent(xml.getAttributeValue(ANSWER_IDENT_ATTRIBUTE_NAME));
        }
        if (xml.getAttribute(CODE_ELEMENT_NAME) != null) {
            param.setCode(xml.getAttributeValue(CODE_ELEMENT_NAME));
        } else if (xml.getChild(CODE_ELEMENT_NAME) != null) {
            param.setCode(xml.getChildText(CODE_ELEMENT_NAME));
        } else if (xml.getTextTrim() != null) {
            param.setCode(xml.getTextTrim());
        }
        if (xml.getAttribute(Parameter.CONDITION_NAME) != null) {
            param.setCondition(true);
        }
        if (xml.getAttribute(CONTAINS_MATHML_ATTRIBUTE_NAME) != null) {
            param.setContainsMathML(true);
        }

        if (xml.getAttribute(SIG_ATTRIBUTE_NAME) != null) {
            try {
                param.setSdNumber(Integer.parseInt(xml.getAttributeValue(SIG_ATTRIBUTE_NAME)));
            } catch (NumberFormatException ignored) {}
        }
        if (xml.getAttribute(SDAPPLICABLE_ATTRIBUTE_NAME) != null) {
            try {
                param.setSdApplicable(Boolean.parseBoolean(xml.getAttributeValue(SDAPPLICABLE_ATTRIBUTE_NAME)));
            } catch (NumberFormatException ignored) {}
        }
        if (xml.getAttribute(SDINDEPENDENT_ATTRIBUTE_NAME) != null) {
            try {
                param.setSdIndependent(Boolean.parseBoolean(xml.getAttributeValue(SDINDEPENDENT_ATTRIBUTE_NAME)));
            } catch (NumberFormatException ignored) {}
        }
        return param;
    }

    /**
     * Gets set with strings found in expression tree.
     *
     * @return set with strings
     */
    public HashSet<String> getStringDependencies() {
        HashSet<String> strList = new HashSet<>();
        AlgorithmUtils.findStringVariants(container, expressionRoot, strList);
        return strList;
    }

    /**
     * Returns true, if this parameter is randomized
     */
    public boolean isRandomized() {
        return StringUtils.matchString(this.code, "rand|rint|rlist", false);
    }

}
