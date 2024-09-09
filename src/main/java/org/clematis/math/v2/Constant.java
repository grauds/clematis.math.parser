// Created: Jan 21, 2003 T 2:44:25 PM
package org.clematis.math.v2;

import java.math.BigDecimal;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v2.algorithm.Parameter;
import org.clematis.math.v2.functions.Decimal;
import org.clematis.math.v2.functions.Sig;
import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.v2.operations.SimpleFraction;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.string.StringMathParserConstants;
import org.clematis.math.MathUtils;
import org.jdom2.Element;

/**
 * Constant representation.
 * <p>
 * Use following code to reproduce constant behaviour
 * <code>
 * Constant c = new Constant(3.1415487987, 8);
 * c.setMultiplier( 1 );
 * c.setSdEnable( true );
 * OutputFormatSettings out = new OutputFormatSettings();
 * Parameter p = new Parameter("$d", c);
 * p.setCorrectAnswer( true );
 * p.setSdNumber( c.getSdNumber() );
 * Algorithm a = new Algorithm();
 * a.addParameter(p);
 * out.setSdEnable( false  );
 * a.setFormatSettings(out);
 * a.printParameters( System.out );
 * </code>
 */
public class Constant extends AbstractConstant {
    /**
     * Value of the constant.
     */
    private BigDecimal number = null;

    /**
     * Constructor.
     *
     * @param element xml representation of the constant.
     */
    public Constant(Element element) {
        if (element != null) {
            if (element.getAttribute("sig") != null) {
                try {
                    setSdNumber(Integer.parseInt(element.getAttributeValue("sig")));
                } catch (NumberFormatException ex) {
                }
            }
            if (element.getAttribute("sdenabled") != null) {
                setSdEnable(element.getAttributeValue("sdenabled").equals("1") ||
                    element.getAttributeValue("sdenabled").equals("true"));
            }
            if (element.getAttribute("sdapplicable") != null) {
                setSdApplicable(element.getAttributeValue("sdapplicable").equals("true"));
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
        super(constant);
        if (constant.number != null) {
            this.number = new BigDecimal(constant.number.toString());
        }
    }

    /**
     * Constructor.
     *
     * @param value constant's value.
     */
    public Constant(double value) {
        setValue(value);
    }

    /**
     * Constructor with formatted input.
     *
     * @param value textual representation of the constant's value.
     */
    public Constant(String value) {
        setValue(value);
    }

    /**
     * Empty public constructor
     */
    public Constant() {
    }

    /**
     * Constructor with big decimal value.
     *
     * @param value of big decimal.
     */
    public Constant(BigDecimal value) {
        this.number = value;
    }

    /**
     * Constructor.
     *
     * @param value textual representation of the constant's value.
     */
    public Constant(double value, int sig) {
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
        try {
            this.number = new BigDecimal(str);
        } catch (NumberFormatException ex) {
        }
    }

    public void setTokenName(String tokenName) {
        setValue(tokenName);
    }

    /**
     * Gets double value of the constant. Applies significant digits, if enabled by algorithm.
     * NOT for PRESENTATION purposes.
     *
     * @return <code>double</code> with constant's value.
     */
    public double getNumber() {
        /**
         * Apply significant digits
         */
        if (getExactBigDecimalValue() != null) {
            try {
                if (getSdNumber() > 0 && getSdEnable()) {
                    return Double.parseDouble(Sig.getSigDigits(getExactBigDecimalValue().toString(), getSdNumber()));
                }
            } catch (NumberFormatException ex) {
            }
            /**
             * If sig digits are not applied, provide exact number
             */
            return getExactBigDecimalValue().doubleValue();
        } else {
            return Double.NaN;
        }
    }

    /**
     * Returns BIG DECIMAL value WITHOUT FORMATTING with SIG digits
     *
     * @return big decimal number
     */
    private BigDecimal getExactBigDecimalValue() {
        if (number != null) {
            return number;
        } else if (token != null) {
            if (getTokenKind() == StringMathParserConstants.PI) {
                return new BigDecimal(Double.toString(Math.PI));
            } else if (getTokenKind() == StringMathParserConstants.EXP_E) {
                return new BigDecimal(Double.toString(Math.E));
            }
            return new BigDecimal(token);
        }
        return null;
    }

    /**
     * Returns string value for CALCULATION purposes
     *
     * @return either initial string or big decimal converted to string
     */
    public String getValue() {
        if (getTokenKind() == StringMathParserConstants.PI) {
            return Double.toString(Math.PI);
        } else if (getTokenKind() == StringMathParserConstants.EXP_E) {
            return Double.toString(Math.E);
        }

        if (number != null) {
            /**
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                return Sig.getSigDigits(getExactBigDecimalValue().toString(), getSdNumber());
            }
            /**
             * Sig digits are not applied
             */
            else {
                return getExactBigDecimalValue().toString();
            }
        } else if (token != null) {
            /**
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                return Sig.getSigDigits(token, getSdNumber());
            }
            /**
             * If sig digits are not applied - do not change the value (do not round to system precision)
             */
            else {
                return token;
            }
        }
        return "";
    }

    /**
     * Returns string value for PRESENTATION purposes
     *
     * @param fs
     * @return either initial string or big decimal converted to string
     */
    public String getValue(OutputFormatSettings fs) {
        String str = "";

        if (getTokenKind() == StringMathParserConstants.PI) {
            return Double.toString(Math.PI);
        } else if (getTokenKind() == StringMathParserConstants.EXP_E) {
            return Double.toString(Math.E);
        }

        if (number != null) {
            /**
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                str = Sig.getSigDigits(getExactBigDecimalValue().toString(), getSdNumber());
            }
            /**
             * If sig digits are not applied, round to system precision
             */
            else {
                str = Decimal.round(getExactBigDecimalValue().toString(), (-1) * Parameter.PRECISION, true);
                // get rid of trailing zeros in non formatted numbers (value does not change, formatting is ignored)
                // str = MathUtils.cutTrailingsZeros( str );
            }
        } else if (token != null) {
            /**
             * Apply significant digits
             */
            if (getSdNumber() > 0 && getSdEnable()) {
                str = Sig.getSigDigits(token, getSdNumber());
            }
            /**
             * If sig digits are not applied - do not change the value (round to system precision)
             */
            else {
                str = token;
            }
        }
        /**
         * Apply visual formatting
         */
        if (fs != null) {
            /**
             * Big decimal removes exponent notation ex.34000
             */
            if (fs.isNoExponent()) {
                BigDecimal bd = new BigDecimal(str);
                str = bd.toPlainString();
            }
            /**
             * If scientific notation is removed, it needs to be restored
             */
            else {
                BigDecimal bd = new BigDecimal(str);
                str = bd.toString();
                /*
                NumberFormat formatter = new DecimalFormat("0.000000E0", new DecimalFormatSymbols(Locale.US));
                str = formatter.format(new BigDecimal(str));
                */
            }
            /**
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
    public Node add(Node item) throws AlgorithmException {
        if (item instanceof Constant c) {
            /**  apply significant digits to output */
            String str1 = this.getValue();
            String str2 = c.getValue();
            /** create big decimals */
            if (str1 != null && str2 != null && !str1.trim().equals("") && !str2.trim().equals("")) {
                BigDecimal bd1 = new BigDecimal(str1);
                BigDecimal bd2 = new BigDecimal(str2);
                /** make operation */
                return new Constant(bd1.add(bd2));
            } else {
                throw new AlgorithmException("Addition of undefined values");
            }
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
    public Node multiply(Node item) throws AlgorithmException {
        if (item instanceof Constant c) {
            String str1 = this.getValue();
            String str2 = c.getValue();
            /** create big decimals */
            if (str1 != null && str2 != null && !str1.trim().equals("") && !str2.trim().equals("")) {
                BigDecimal bd1 = new BigDecimal(str1);
                BigDecimal bd2 = new BigDecimal(str2);
                /** make operation */
                return new Constant(bd1.multiply(bd2));
            } else {
                throw new AlgorithmException("Multiplication of undefined values");
            }
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
    public Node pow(Constant c) {
        if (getValue() != null) {
            if (((int) c.getNumber()) == c.getNumber()) {
                Constant ret = new Constant(this);
                int power = (int) c.getNumber();
                /**
                 * Division is handled by simple fraction
                 */
                if (power < 0) {
                    return new SimpleFraction(new Constant("1"),
                        new Constant(new BigDecimal(ret.getValue()).pow(Math.abs(power))));
                }
                return new Constant(new BigDecimal(ret.getValue()).pow(Math.abs(power)));
            }
        }
        return null;
    }

    /**
     * Returns a <tt>Constant</tt> whose value is <tt>(this / divisor)</tt>, The result is computed exactly, to
     * unlimited precision.
     *
     * @param c - divisor
     * @return <tt>Constant</tt> whose value is <tt>(this / divisor)</tt>
     */
    public Node divide(Constant c) {
        if (getValue() != null) {
            /**
             * Try to divide exactly first
             */
            try {
                return new Constant(new BigDecimal(getValue()).divide(new BigDecimal(c.getValue())));
            }
            /**
             * Construct simple fraction, if division failed
             */ catch (ArithmeticException ex) {
                return new SimpleFraction(new Constant(getValue()), c);
            }
        }
        return null;
    }

    /**
     * Compares this expression item with a given one and
     * returns true, if expression items are similar.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean isSimilar(Node item) {
        return (item instanceof Constant);
    }

    /**
     * Compares this expression item with a given one and returns
     * true, if expression items are equal.
     *
     * @param item expression item to compare
     * @return true, if expression items are similar
     */
    public boolean equals(Node item) {
        if (item instanceof Constant c) {
            return this.getNumber() == c.getNumber();
        } else if (item instanceof SimpleFraction sf) {
            try {
                return this.getNumber() == sf.getProduct().getNumber();
            } catch (AlgorithmException e) {
                return false;
            }
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
     * Sets multiplier
     *
     * @param multiplier
     */
    public void setMultiplier(double multiplier) {
        /**
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
        Element cn = new Element("cn", Node.NS_MATH);
        cn.setAttribute("type", "real");
        cn.setText(getValue());
        return cn;
    }

    /**
     * Returns textual representation of the <code>Constant</code> value.
     *
     * @return <code>String</code> containing the value of the constant.
     */
    public String toString() {
        if (getExactBigDecimalValue() != null) {
            if (getExactBigDecimalValue().compareTo(new BigDecimal(0)) >= 0) {
                return getValue();
            } else {
                return "(" + getValue() + ")";
            }
        } else {
            return "NaN";
        }
    }

    /**
     * Returns true if this constant is zero
     *
     * @return true if this constant is zero
     */
    public boolean isZero() {
        if (getExactBigDecimalValue() != null) {
            return MathUtils.isZero(getExactBigDecimalValue().toString());
        } else {
            return false;
        }
    }

    /**
     * Writes constant to xml element
     *
     * @return xml element, representing constant
     */
    public Element toXML() {
        Element element = new Element("constant");
        element.setAttribute("type", "number");
        if (sdNumber != 0) {
            element.setAttribute("sig", Integer.toString(sdNumber));
        }
        if (sdEnable) {
            element.setAttribute("sdenabled", String.valueOf(sdEnable));
        }
        element.setAttribute("sdapplicable", String.valueOf(sdApplicable));
        element.setText(getValue());
        return element;
    }
}
