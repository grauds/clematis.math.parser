// Created: Jan 21, 2003 T 2:44:25 PM
package org.clematis.math.v1;

import java.math.BigDecimal;

import org.clematis.math.AlgorithmException;
import org.clematis.math.IExpressionItem;
import org.clematis.math.MathUtils;
import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.functions.Decimal;
import org.clematis.math.v1.functions.Sig;
import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.XMLConstants;
import org.clematis.math.v1.operations.SimpleFraction;
import org.jdom2.Element;

/**
 * Constant representation.
 * <p>
 * Use following code to reproduce constant behaviour
 * <code>
 *      Constant c = new Constant(3.1415487987, 8);
 *      c.setMultiplier( 1 );
 *      c.setSdEnable( true );
 *      OutputFormatSettings out = new OutputFormatSettings();
 *      Parameter p = new Parameter("$d", c);
 *      p.setCorrectAnswer( true );
 *      p.setSdNumber( c.getSdNumber() );
 *      Algorithm a = new Algorithm();
 *      a.addParameter(p);
 *      out.setSdEnable( false  );
 *      a.setFormatSettings(out);
 *      a.printParameters( System.out );
 * </code>
 */
public class Constant extends AbstractConstant {

    public static final String DEFAULT_SIGNIFICANT_DIGITS = "1";
    /**
     * Value of the constant is initialized with zero.
     */
    private BigDecimal number = BigDecimal.ZERO;
    /**
     * Constructor.
     *
     * @param element xml representation of the constant.
     */
    public Constant(Element element) {
        super();
        if (element != null) {
            if (element.getAttribute(XMLConstants.SIG_ATTRIBUTE_NAME) != null) {
                try {
                    setSdNumber(Integer.parseInt(element.getAttributeValue(XMLConstants.SIG_ATTRIBUTE_NAME)));
                } catch (NumberFormatException ignored) {}
            }
            if (element.getAttribute(XMLConstants.SDENABLED_ATTRIBUTE_NAME) != null) {
                try {
                    setSdEnable(element.getAttributeValue(XMLConstants.SDENABLED_ATTRIBUTE_NAME)
                        .equals(DEFAULT_SIGNIFICANT_DIGITS)
                        || element.getAttributeValue(XMLConstants.SDENABLED_ATTRIBUTE_NAME)
                        .equals(Boolean.TRUE.toString())
                    );
                } catch (NumberFormatException ignored) {}
            }
            this.number = new BigDecimal(element.getText());
        }
    }

    /**
     * Constructor for copy.
     *
     * @param constant textual representation of the constant's value.
     */
    public Constant(Constant constant) {
        super();
        this.number = constant.number;
        this.value = constant.value;
        this.sdEnable = constant.sdEnable;
        this.sdNumber = constant.sdNumber;
        this.parameter = constant.parameter;
    }

    /**
     * Constructor.
     *
     * @param value constant's value.
     */
    public Constant(double value) {
        super();
        setValue(value);
    }

    /**
     * Constructor with formatted input.
     *
     * @param value textual representation of the constant's value.
     */
    public Constant(String value) {
        super();
        setValue(value);
    }

    /**
     * Constructor with big decimal value.
     *
     * @param value of big decimal.
     */
    public Constant(BigDecimal value) {
        super();
        this.number = value;
    }

    /**
     * Constructor.
     *
     * @param value textual representation of the constant's value.
     */
    public Constant(double value, int sig) {
        super();
        setValue(value);
        this.sdNumber = sig;
    }

    /**
     * Copies the constant.
     *
     * @return instance of <code>Constant</code> class with the same value.
     */
    public AbstractConstant copy() {
        return new Constant(this);
    }

    /**
     * Sets value of the constant.
     *
     * @param value new constant's value.
     */
    public void setValue(double value) {
        // get rid of zeros in integers
        String str = Double.toString(value);
        if (MathUtils.isInteger(str) && str.endsWith(".0")) {
            str = str.substring(0, str.length() - 2);
        }
        this.number = new BigDecimal(str);
    }

    /**
     * Gets double value of the constant. Applies significant digits, if enabled by algorithm.
     *
     * @return <code>double</code> with constant's value.
     */
    public double getNumber() {
        /*
         * Apply significant digits
         */
        try {
            if (getSdNumber() > 0 && getSdEnable()) {
                return Double.parseDouble(Sig.formatWithSigDigits(getExactBigDecimalValue().toString(), getSdNumber()));
            }
        } catch (NumberFormatException ignored) {}
        /*
         * If sig digits are not applied, provide exact number
         */
        return getExactBigDecimalValue().doubleValue();
    }

    /**
     * Returns BIG DECIMAL value without formatting with sig digits
     *
     * @return big decimal number
     */
    private BigDecimal getExactBigDecimalValue() {
        if (value != null) {
            return new BigDecimal(value);
        }
        return number;
    }

    /**
     * Returns string value for CALCULATION purposes
     *
     * @return either initial string or big decimal converted to string
     */
    private String getExactStringValue() {
        String result = "";

        if (number != null) {
            /*
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                result = Sig.formatWithSigDigits(getExactBigDecimalValue().toString(), getSdNumber());
            } else {
                /*
                 * Sig digits are not applied
                 */
                result = getExactBigDecimalValue().toString();
            }
        } else if (value != null) {
            /*
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                result = Sig.formatWithSigDigits(value, getSdNumber());
            } else {
                /*
                 * If sig digits are not applied - do not change the value (do not round to system precision)
                 */
                result = value;
            }
        }
        return result;
    }

    /**
     * Returns string value for PRESENTATION purposes
     *
     * @param fs format settings for output
     * @return either initial string or big decimal converted to string
     */
    public String getValue(OutputFormatSettings fs) {
        String str = "";

        if (number != null) {
            /*
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                str = Sig.formatWithSigDigits(getExactBigDecimalValue().toString(), getSdNumber());
            } else {
                /*
                 * If sig digits are not applied, round to system precision
                 */
                str = Decimal.round(getExactBigDecimalValue().toString(), (-1) * Parameter.PRECISION, true);
                // get rid of trailing zeros in non formatted numbers (value does not change, formatting is ignored)
                // str = MathUtils.cutTrailingsZeros( str );
            }
        } else if (value != null) {
            /*
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                str = Sig.formatWithSigDigits(value, getSdNumber());
            } else {
                /*
                 * If sig digits are not applied - do not change the value (round to system precision)
                 */
                str = value;
            }
        }
        /*
         * Apply visual formatting
         */
        if (fs != null) {
            /*
             * Big decimal removes exponent notation ex.34000
             */
            if (fs.isNoExponent()) {
                BigDecimal bd = new BigDecimal(str);
                str = bd.toPlainString();
            } else {
                /*
                 * If scientific notation is removed, it needs to be restored
                 */
                BigDecimal bd = new BigDecimal(str);
                str = bd.toString();
            }
            /*
             * Switch on grouping
             */
            if (fs.isGrouping()) {
                str = MathUtils.groupThousands(str);
            } else {
                str = MathUtils.ungroupThousands(str);
            }
        } else {
            BigDecimal bd = new BigDecimal(MathUtils.ungroupThousands(str));
            str = bd.toString();
        }

        return str;
    }

    /**
     * Adds the constants.
     *
     * @param item expression item.
     * @return modified this <code>Constant</code> object or
     * null when  <code>item</code> is not instance of <code>Constant</code> class.
     */
    public IExpressionItem add(IExpressionItem item) throws AlgorithmException {
        if (item instanceof Constant c) {
            /*  apply significant digits to output */
            String str1 = this.getExactStringValue();
            String str2 = c.getExactStringValue();
            /* create big decimals */
            BigDecimal bd1 = new BigDecimal(str1);
            BigDecimal bd2 = new BigDecimal(str2);
            /* make operation */
            return new Constant(bd1.add(bd2));
        }
        return item.add(this);
    }

    /**
     * Multiplies the constant by constant or variable.
     *
     * @param item expression item.
     * @return modified this <code>Constant</code> object or
     * null when  <code>item</code> is not instance of <code>Constant</code> class.
     */
    public IExpressionItem multiply(IExpressionItem item) throws AlgorithmException {
        if (item instanceof Constant c) {
            String str1 = this.getExactStringValue();
            String str2 = c.getExactStringValue();
            /* create big decimals */
            BigDecimal bd1 = new BigDecimal(str1);
            BigDecimal bd2 = new BigDecimal(str2);
            /* make operation */
            return new Constant(bd1.multiply(bd2));
        }
        return item.multiply(this);
    }

    /**
     * Returns a <tt>Constant</tt> whose value is
     * <tt>(this<sup>n</sup>)</tt>, The power is computed exactly, to
     * unlimited precision.
     *
     * @param c - constant
     * @return a <tt>Constant</tt> whose value is
     * <tt>(this<sup>n</sup>)</tt>
     */
    public IExpressionItem pow(Constant c) {
        IExpressionItem result = null;

        if (getValue(null) == null && ((int) c.getNumber()) == c.getNumber()) {
            Constant ret = new Constant(this);
            int power = (int) c.getNumber();
            /*
             * Division is handled by simple fraction
             */
            if (power < 0) {
                result = new SimpleFraction(new Constant(DEFAULT_SIGNIFICANT_DIGITS),
                    new Constant(new BigDecimal(ret.getValue(null)).pow(Math.abs(power))));
            } else {
                result = new Constant(new BigDecimal(ret.getValue(null)).pow(Math.abs(power)));
            }
        }
        return result;
    }

    /**
     * Returns a <tt>Constant</tt> whose value is <tt>(this / divisor)</tt>, The result is computed exactly, to
     * unlimited precision.
     *
     * @param c - divisor
     * @return <tt>Constant</tt> whose value is <tt>(this / divisor)</tt>
     */
    public IExpressionItem divide(Constant c) {
        IExpressionItem result = null;
        if (getValue(null) != null) {
            /*
             * Try to divide exactly first
             */
            try {
                result = new Constant(new BigDecimal(getValue(null)).divide(new BigDecimal(c.getValue(null))));
            } catch (ArithmeticException ex) {
                /*
                 * Construct simple fraction if division failed
                 */
                result = new SimpleFraction(new Constant(getValue(null)), c);
            }
        }
        return result;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean aKindOf(IExpressionItem item) {
        return (item instanceof Constant);
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(IExpressionItem item) {
        if (item instanceof Constant c) {
            return this.getNumber() == c.getNumber();
        }
        return false;
    }

    /**
     * Return multiplier
     *
     * @return constant coefficient
     */
    public double getMultiplier() {
        return 1;
    }

    /**
     * Sets multiplier for the constant, it is a trivial math expression contained within constant itself
     *
     * @param multiplier for the constant.
     */
    public void setMultiplier(double multiplier) {
        /*
         * Avoid trivial multiplication
         */
        if (multiplier != 1) {
            setValue(getNumber() * multiplier);
        }
    }

    /**
     * Provides mathml formatted element, representing
     * expression subtree.
     *
     * @return mathml formatted element
     */
    public Element toMathML() {
        Element cn = new Element("cn", NS_MATH);
        cn.setAttribute(XMLConstants.TYPE_ATTRIBUTE_NAME, "real");
        cn.setText(getValue(null));
        return cn;
    }

    /**
     * Returns textual representation of the <code>Constant</code> value.
     *
     * @return <code>String</code> containing the value of the constant.
     */
    public String toString() {
        if (getExactBigDecimalValue().compareTo(new BigDecimal(0)) > 0) {
            return getValue(null);
        } else {
            return "(" + getValue(null) + ")";
        }
    }

    /**
     * Returns true if this constant is zero
     *
     * @return true if this constant is zero
     */
    public boolean isZero() {
        return MathUtils.isZero(getExactBigDecimalValue().toString());
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public Element toXML() {
        Element element = new Element("constant");
        element.setAttribute(XMLConstants.TYPE_ATTRIBUTE_NAME, "number");
        if (sdNumber != 0) {
            element.setAttribute(XMLConstants.SIG_ATTRIBUTE_NAME, Integer.toString(sdNumber));
        }
        if (sdEnable) {
            element.setAttribute(XMLConstants.SDENABLED_ATTRIBUTE_NAME, String.valueOf(sdEnable));
        }
        element.setText(getValue(null));
        return element;
    }
}
