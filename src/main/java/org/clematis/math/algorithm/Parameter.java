package org.clematis.math.algorithm;

import org.clematis.math.AbstractConstant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.IParameterCalculator;
import org.clematis.math.XMLConstants;
import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.operations.SimpleFraction;
import org.clematis.math.parsers.v1.ExpressionParser;
import org.clematis.math.parsers.v1.ParameterCalculator;
import org.clematis.math.utils.StringUtils;
import org.jdom2.CDATA;
import org.jdom2.Element;

import lombok.Getter;
import lombok.Setter;

/**
 * Parameter is a product of one line of the algorithm
 */
@Setter
@Getter
public class Parameter extends SimpleParameter {
    /**
     * Precision of outcoming values for presentation.
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
     * Parameter can be a condition - the result of calculation can be taken to break
     * algorithm calculation and to start calculating over again.
     */
    protected boolean condition = false;
    /**
     * Object representation of the expression code.
     */
    private IExpressionItem expressionRoot = null;
    /**
     * Adaptor to calculate the code of the parameter
     */
    private IParameterCalculator parameterCalculator = null;
    /**
     * Calculated qu algorithm, this parameter belongs to
     */
    private Algorithm container = null;
    /**
     * Textual representation of the expression code.
     */
    private String code = null;
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
     * Is this parameter a correct answer representation
     */
    private boolean correctAnswer = false;

    /**
     * Constructor.
     *
     * @param name parameter name.
     * @param code algorithmic code.
     */
    public Parameter(String name, String code) {
        setName(name);
        setCode(code);
        setParameterCalculator(new ParameterCalculator());
    }

    /**
     * Constructor.
     *
     * @param name          parameter name.
     * @param currentResult <code>Constant</code> representing parameter value.
     */
    public Parameter(String name, AbstractConstant currentResult) {
        this(name, "");
        setCurrentResult(currentResult);
    }

    /**
     * Calculates parameter value.
     * It is an <code>AbstractConstant</code> object.
     */
    public void calculate(Algorithm algorithm, int currentLine) throws AlgorithmException {

        IExpressionItem result = getParameterCalculator().calculate(
            getName(),
            getCode(),
            algorithm,
            algorithm,
            currentLine
        );

        if (result instanceof AbstractConstant) {
            setCurrentResult((AbstractConstant) result);
        } else if (result instanceof SimpleFraction) {
            setCurrentResult(((SimpleFraction) result).getProduct());
        } else {
            setCurrentResult(null);
        }
    }

    public void parse(Algorithm paramProvider, int mode)
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
     * Sets parameter name
     *
     * @param name of parameter
     */
    public void setName(String name) {
        super.setName(name);
        if (name.equals(XMLConstants.CONDITION_NAME)) {
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

    public boolean getSdEnable() {
        if (container != null && container.getFormatSettings() != null) {
            return isSdApplicable() && container.getFormatSettings().isSdEnabled();
        } else {
            return this.isSdApplicable();
        }
    }

    public boolean isSdApplicable() {
        final boolean pureApplicable = sdApplicable && !isZero();

        if (container != null && container.getFormatSettings() != null) {
            if (!isSdIndependent()) {
                return !container.getFormatSettings().isToleranceEnabled() || pureApplicable;
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
        Element paramElement = new Element(XMLConstants.PARAM_ELEMENT_NAME);
        paramElement.setAttribute(XMLConstants.NAME_ATTRIBUTE_NAME, getName());

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
    public static Parameter loadResult(Element paramElement) {
        if (paramElement != null) {
            String name = paramElement.getAttributeValue(XMLConstants.NAME_ATTRIBUTE_NAME);
            AbstractConstant c = null;
            if (paramElement.getChild(XMLConstants.CONSTANT_ELEMENT_NAME) != null) {
                c = AbstractConstant.create(paramElement.getChild(XMLConstants.CONSTANT_ELEMENT_NAME));
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
        Element paramElement = new Element(XMLConstants.PARAM_ELEMENT_NAME);
        if (getName() != null) {
            paramElement.setAttribute(XMLConstants.NAME_ATTRIBUTE_NAME, getName());
        }
        if (isCorrectAnswer()) {
            paramElement.setAttribute(XMLConstants.ANSWER_ATTRIBUTE_NAME, Boolean.TRUE.toString());
            /*
             * Store numeric policy only for correct answer
             */
            paramElement.setAttribute(XMLConstants.SIG_ATTRIBUTE_NAME, Integer.toString(sdNumber));
            paramElement.setAttribute(XMLConstants.SDAPPLICABLE_ATTRIBUTE_NAME, Boolean.toString(isSdApplicable()));
            paramElement.setAttribute(XMLConstants.SDINDEPENDENT_ATTRIBUTE_NAME, Boolean.toString(isSdIndependent()));
        }
        if (isCondition()) {
            paramElement.setAttribute(XMLConstants.CONDITION_NAME, Boolean.TRUE.toString());
        }
        /*
         * Add formula code
         */
        Element codeElement = new Element(XMLConstants.CODE_ELEMENT_NAME);
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
        if (xml.getChild(XMLConstants.CONSTANT_ELEMENT_NAME) != null) {
            constant = AbstractConstant.create(xml.getChild(XMLConstants.CONSTANT_ELEMENT_NAME));
        }
        /*
         * Create constant wrapper - parameter
         */
        Parameter param = new Parameter(xml.getAttributeValue(XMLConstants.NAME_ATTRIBUTE_NAME), constant);
        /*
         * Read parameter configuration.
         */
        if (xml.getAttribute(XMLConstants.ANSWER_ATTRIBUTE_NAME) != null) {
            param.setCorrectAnswer(true);
        }
        if (xml.getAttribute(XMLConstants.CODE_ELEMENT_NAME) != null) {
            param.setCode(xml.getAttributeValue(XMLConstants.CODE_ELEMENT_NAME));
        } else if (xml.getChild(XMLConstants.CODE_ELEMENT_NAME) != null) {
            param.setCode(xml.getChildText(XMLConstants.CODE_ELEMENT_NAME));
        } else if (xml.getTextTrim() != null) {
            param.setCode(xml.getTextTrim());
        }
        if (xml.getAttribute(XMLConstants.CONDITION_NAME) != null) {
            param.setCondition(true);
        }
        if (xml.getAttribute(XMLConstants.SIG_ATTRIBUTE_NAME) != null) {
            try {
                param.setSdNumber(Integer.parseInt(xml.getAttributeValue(XMLConstants.SIG_ATTRIBUTE_NAME)));
            } catch (NumberFormatException ignored) {}
        }
        if (xml.getAttribute(XMLConstants.SDAPPLICABLE_ATTRIBUTE_NAME) != null) {
            try {
                param.setSdApplicable(Boolean.parseBoolean(xml.getAttributeValue(
                    XMLConstants.SDAPPLICABLE_ATTRIBUTE_NAME)));
            } catch (NumberFormatException ignored) {}
        }
        if (xml.getAttribute(XMLConstants.SDINDEPENDENT_ATTRIBUTE_NAME) != null) {
            try {
                param.setSdIndependent(Boolean.parseBoolean(xml.getAttributeValue(
                    XMLConstants.SDINDEPENDENT_ATTRIBUTE_NAME)));
            } catch (NumberFormatException ignored) {}
        }
        return param;
    }

    /**
     * Returns true, if this parameter is randomized
     */
    public boolean isRandomized() {
        return StringUtils.matchString(this.code, "rand|rint|rlist", false);
    }

}
