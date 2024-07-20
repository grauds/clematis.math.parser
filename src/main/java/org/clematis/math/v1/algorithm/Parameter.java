// Created: Jan 21, 2003 T 5:23:42 PM
package org.clematis.math.v1.algorithm;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.AlgorithmException;
import org.clematis.math.v1.Constant;
import org.clematis.math.v1.ExpressionParser;
import org.clematis.math.v1.OutputFormatSettings;
import org.clematis.math.v1.iExpressionItem;
import org.clematis.math.v1.operations.SimpleFraction;
import org.clematis.math.utils.StringUtils;
import org.jdom2.CDATA;
import org.jdom2.Element;

import java.util.HashSet;

/**
 * Parameter is a special constant, that is a product of
 * algorithm work, done before formula is presented to a student.
 */
public class Parameter extends SimpleParameter {
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
    private int sdNumber = 3;
    /**
     * Is this parameter contains MathML code
     */
    private boolean containsMathML = false;
    /**
     * Precision of outcoming values
     * for presentation.
     */
    public static final int PRECISION = 12;
    /**
     * Parameter can be a condition - the result
     * of calculation can be taken to break
     * algorithm calculation and to start
     * calculating over again.
     */
    protected boolean condition = false;
    /**
     * Special condition name of parameter
     */
    public final static String CONDITION_NAME = "condition";

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
        iExpressionItem result = null;
        /**
         * If code is not null, Parameter is mutable, if null - immutable via calculation
         */
        if (code != null) {
            try {
                //** preprocess code string */
                String _code = code;
                _code = AlgorithmUtils.replaceParameters(_code, paramProvider, currentLine, false);
                //** parse processed code string */
                ExpressionParser exprParser = new ExpressionParser(_code, paramProvider, null, paramProvider);
                expressionRoot = exprParser.parse();
                //** calculate expression root */
                if (expressionRoot != null) {
                    result = expressionRoot.calculate(paramProvider);
                }
            } catch (AlgorithmException ex) {
                throw new AlgorithmException("Error in " + this.getName() + "=" + code + ": " + ex.getMessage());
            }
            if (result instanceof AbstractConstant) {
                setCurrentResult((AbstractConstant) result);
            } else if (result instanceof SimpleFraction) {
                setCurrentResult(((SimpleFraction) result).getProduct());
            } else {
                throw new AlgorithmException("Parameter: " + this.getName() + "=" + code + " is not a CONSTANT: " +
                    (result == null ? null : result.toString()));
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
            throw new AlgorithmException("Failed parsing of expression root for parameter: " + this.getName() +
                " code: " + code + " because of " + ex);
        }
    }

    /**
     * Returns expression root
     *
     * @return expression root
     */
    public iExpressionItem getExpressionRoot() {
        return expressionRoot;
    }

    /**
     * Sets the parameter value. May be null.
     *
     * @param constant the parameter value
     */
    void setCurrentResult(AbstractConstant constant) {
        super.setCurrentResult(constant);
        if (currentResult != null) {
            currentResult.setParameter(this);
        }
    }

    /**
     * Gets parameter algorithmic code used for value dynamic calculation.
     *
     * @return <code>String</code> containing parameter algorithmic code.
     */
    public String getCode() {
        return code;
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
        // save code
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
        if (getCurrentResult() != null &&
            getCurrentResult() instanceof Constant &&
            isCondition()) {
            double result = ((Constant) getCurrentResult()).getNumber();

            if (result == 1) {
                return true;
            } else return result != 0;
        }

        return true;
    }

    public boolean isCondition() {
        return condition;
    }

    public void setCondition(boolean condition) {
        this.condition = condition;
    }

    public boolean isCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(boolean correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getCorrectAnswerIdent() {
        return correctAnswerIdent;
    }

    public Algorithm getContainer() {
        return container;
    }

    void setContainer(Algorithm container) {
        this.container = container;
    }

    void setCorrectAnswerIdent(String correctAnswerIdent) {
        this.correctAnswerIdent = correctAnswerIdent;
    }

    //********************************* NUMERIC POLICY ********************************
    public boolean getSdEnable() {
        if (container != null && container.getFormatSettings() != null) {
            if (isSdApplicable()) {
                return container.getFormatSettings().getSdEnable();
            } else {
                return false;
            }
        } else {
            return this.isSdApplicable();
        }
    }

    public boolean isSdApplicable() {
        final boolean pure_applicable = sdApplicable && !isZero();

        if (container != null && container.getFormatSettings() != null) {
            if (!isSdIndependent()) {
                return !container.getFormatSettings().isqTolerance() || pure_applicable;
            }
        }

        return pure_applicable;
    }

    public void setSdApplicable(boolean sdApplicable) {
        this.sdApplicable = sdApplicable;
    }

    /**
     * Sets the number of significant digits.
     *
     * @param sdNumber the number of significant digits.
     */
    public void setSdNumber(int sdNumber) {
        this.sdNumber = sdNumber;
    }

    public int getSdNumber() {
        return sdNumber;
    }

    public boolean isSdIndependent() {
        return sdIndependent;
    }

    public void setSdIndependent(boolean sdIndependent) {
        this.sdIndependent = sdIndependent;
    }
//********************************            ********************************

    /**
     * Gets text representation of parameter value.
     *
     * @param format
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
        Element paramElement = new Element("param");
        paramElement.setAttribute("name", getName());

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
            String name = paramElement.getAttributeValue("name");
            AbstractConstant c = null;
            if (paramElement.getChild("constant") != null) {
                c = AbstractConstant.create(paramElement.getChild("constant"));
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
        Element paramElement = new Element("param");
        if (getName() != null) {
            paramElement.setAttribute("name", getName());
        }
        if (isCorrectAnswer()) {
            paramElement.setAttribute("answer", "true");
            /**
             * Store numeric policy only for correct answer
             */
            paramElement.setAttribute("sig", Integer.toString(sdNumber));
            paramElement.setAttribute("sdapplicable", isSdApplicable() ? "true" : "false");
            paramElement.setAttribute("sdindependent", isSdIndependent() ? "true" : "false");
        }
        if (getCorrectAnswerIdent() != null) {
            paramElement.setAttribute("answer_ident", getCorrectAnswerIdent());
        }
        if (isCondition()) {
            paramElement.setAttribute(Parameter.CONDITION_NAME, "true");
        }
        if (containsMathML) {
            paramElement.setAttribute("contains_mathml", "true");
        }
        /**
         * Add formula code
         */
        Element codeElement = new Element("code");
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
        /**
         * Create constant
         */
        AbstractConstant constant = null;
        if (xml.getChild("constant") != null) {
            constant = AbstractConstant.create(xml.getChild("constant"));
        }
        /**
         * Create constant wrapper - parameter
         */
        Parameter param = new Parameter(xml.getAttributeValue("name"), constant);
        /**
         * Read parameter configuration.
         */
        if (xml.getAttribute("answer") != null) {
            param.setCorrectAnswer(true);
        }
        if (xml.getAttribute("answer_ident") != null) {
            param.setCorrectAnswerIdent(xml.getAttributeValue("answer_ident"));
        }
        if (xml.getAttribute("code") != null) {
            param.setCode(xml.getAttributeValue("code"));
        } else if (xml.getChild("code") != null) {
            param.setCode(xml.getChildText("code"));
        } else if (xml.getTextTrim() != null) {
            param.setCode(xml.getTextTrim());
        }
        if (xml.getAttribute(Parameter.CONDITION_NAME) != null) {
            param.setCondition(true);
        }
        if (xml.getAttribute("contains_mathml") != null) {
            param.setContainsMathML(true);
        }
        //***    let parameter rule constant's sig tuning if it is correct answer
        if (xml.getAttribute("sig") != null) {
            try {
                param.setSdNumber(Integer.parseInt(xml.getAttributeValue("sig")));
            } catch (NumberFormatException ex) {
            }
        }
        if (xml.getAttribute("sdapplicable") != null) {
            try {
                param.setSdApplicable(Boolean.parseBoolean(xml.getAttributeValue("sdapplicable")));
            } catch (NumberFormatException ex) {
            }
        }
        if (xml.getAttribute("sdindependent") != null) {
            try {
                param.setSdIndependent(Boolean.parseBoolean(xml.getAttributeValue("sdindependent")));
            } catch (NumberFormatException ex) {
            }
        }
        //***    let parameter rule constant's sig tuning if it is correct answer

        return param;
    }

    /**
     * Gets list with strings found in expression tree.
     *
     * @return list with strings.
     * @throws Exception
     */
    public HashSet<String> getStringDependencies()
        throws Exception {
        HashSet<String> strList = new HashSet<String>();
        AlgorithmUtils.findStringVariants(container, expressionRoot, strList);
        return strList;
    }

    /**
     * Does the parameter code contain MathML code.
     *
     * @return true/false
     */
    public boolean isContainsMathML() {
        return containsMathML;
    }

    /**
     * Sets flag indicating that parameter code contains MathML code.
     *
     * @param containsMathML the flag value.
     */
    public void setContainsMathML(boolean containsMathML) {
        this.containsMathML = containsMathML;
    }

    /**
     * Returns true, if this parameter is randomized with
     */
    public boolean isRandomized() {
        return StringUtils.matchString(this.code, "rand|rint|rlist", false);
    }

}
