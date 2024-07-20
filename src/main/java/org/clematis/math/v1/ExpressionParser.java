// Created: Jan 17, 2003 T 7:46:47 PM
package org.clematis.math.v1;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import org.clematis.math.v1.algorithm.DefaultParameterProvider;
import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.algorithm.ParameterReference;
import org.clematis.math.v1.algorithm.iFunctionProvider;
import org.clematis.math.v1.algorithm.iSimpleParameterProvider;
import org.clematis.math.v1.algorithm.iVariableProvider;
import org.clematis.math.v1.functions.aFunction;
import org.clematis.math.v1.operations.Addition;
import org.clematis.math.v1.operations.Multiplication;
import org.clematis.math.v1.operations.Power;

/**
 * Parses mathematical expression.
 */
public class ExpressionParser {
    /**
     * Parsing modes
     */
    public static final int MODE_COPY_PARAMETER_AS_CONSTANT = 1;
    public static final int MODE_COPY_PARAMETER_AS_TREE = 2;
    public static final int MODE_COPY_PARAMETER_AS_REFERENCE = 3;
    /**
     * Expression
     */
    String m_expression = null;
    /**
     * Current operator
     */
    char m_operator = '\0';
    /**
     * Previous operator
     */
    char m_prevOperator = '\0';
    /**
     * Operand
     */
    String m_operand = null;
    /**
     * Parameter provider, always not null
     */
    iSimpleParameterProvider m_paramProvider = new DefaultParameterProvider();
    /**
     * Variable provider
     */
    iVariableProvider m_varProvider = null;
    /**
     * Function provider
     */
    iFunctionProvider m_functionProvider = null;
    /**
     * Buffer to pick up letters of operand
     */
    StringBuilder m_operandBuffer = new StringBuilder();
    /**
     * String iterator
     */
    StringCharacterIterator m_strIterator = null;
    /**
     * Copy constant flag. If set to false, expression root
     * is copied in evaluation of variable
     */
    private int mode = MODE_COPY_PARAMETER_AS_CONSTANT;

    /**
     * Empty constructor
     *
     * @param in_expression expression to parse
     */
    public ExpressionParser(String in_expression) {
        m_expression = in_expression;
        m_strIterator = new StringCharacterIterator(m_expression);
        m_operator = CharacterIterator.DONE;
    }

    /**
     * Constructor with parameter provider. Each parameter starts with
     * dollar sign - $. This constructor is made private to avoid implicit usage
     * without function factory.
     *
     * @param in_expression    expression to parse
     * @param in_paramProvider parameter provider
     */
    private ExpressionParser(String in_expression, iSimpleParameterProvider in_paramProvider) {
        this(in_expression);
        this.m_paramProvider = in_paramProvider;
    }

    /**
     * Constructor with parameter and variable providers.
     *
     * @param in_expression    expression to parse
     * @param in_paramProvider parameter provider
     * @param in_varProvider   parameter provider
     */
    public ExpressionParser(String in_expression, iSimpleParameterProvider in_paramProvider,
                            iVariableProvider in_varProvider) {
        this(in_expression, in_paramProvider);
        this.m_varProvider = in_varProvider;
    }

    /**
     * Constructor parameter, variable and function providers.
     *
     * @param in_expression    expression to parse
     * @param in_paramProvider parameter provider
     * @param in_varProvider   parameter provider
     */
    public ExpressionParser(String in_expression, iSimpleParameterProvider in_paramProvider,
                            iVariableProvider in_varProvider,
                            iFunctionProvider in_functionProvider) {
        this(in_expression, in_paramProvider, in_varProvider);
        this.m_functionProvider = in_functionProvider;
    }

    /**
     * Sets parameter replacement mode
     * Can be either
     * <p/>
     * MODE_COPY_PARAMETER_AS_CONSTANT = 1;
     * MODE_COPY_PARAMETER_AS_TREE = 2;
     * MODE_COPY_PARAMETER_AS_REFERENCE = 3;
     *
     * @param mode
     */
    public void setMode(int mode) {
        this.mode = mode;
    }

    /**
     * Advances one step further in parsing string
     *
     * @return true if success
     */
    private boolean getNextOperatorAndOperand() {
        /**
         * Take next char as operator
         */
        if (m_operator == m_strIterator.current()) {
            m_operator = m_strIterator.next();
        }
        /**
         * No operands, no operators
         */
        if (m_strIterator.current() == CharacterIterator.DONE) {
            m_operator = CharacterIterator.DONE;
            return false;
        }
        /**
         * Clean operand buffer
         */
        m_operandBuffer = m_operandBuffer.delete(0, m_operandBuffer.length());
        /**
         * Remember previous char
         */
        char prevCh = m_strIterator.current();
        /**
         * Adds chars as simple text, without parsing,
         * if this flag is set to true
         */
        boolean text = false;

        for (char ch = m_strIterator.current(); ch != CharacterIterator.DONE; ch = m_strIterator.next()) {
            // plain text mode switcher
            if (ch == '"') {
                text = !text;
            }
            if (!text) {
                if (Character.isWhitespace(ch)) {
                    continue;
                }
                // chars - operators delimiters
                if (isCharDelimeter(ch)) {
                    m_prevOperator = m_operator;
                    m_operator = ch;
                    break;
                }
                /** parsing after exponent **/
                if (ch == 'e' || ch == 'E') {
                    /** only in case of for example ...8E */
                    if (prevCh >= '0' && prevCh <= '9') {
                        m_operandBuffer.append(ch);
                        /** Counter of chars after E */
                        int char_counter = 0;
                        /** Take next chars */
                        while (true) {
                            ch = m_strIterator.next();
                            if (ch != CharacterIterator.DONE &&
                                (char_counter == 0 && (ch == '+' || ch == '-'))
                                || (ch >= '0' && ch <= '9')) {
                                char_counter++;
                                m_operandBuffer.append(ch);
                            }
                            /**
                             * Return to previous
                             */
                            else {
                                ch = m_strIterator.previous();
                                break;
                            }
                        }
                        prevCh = ch;
                        continue;
                    }
                }
            }
            // append char to operator
            m_operandBuffer.append(ch);
            prevCh = ch;
        }
        /**
         * Finally got operand
         */
        m_operand = m_operandBuffer.toString();
        /**
         * Get last operand, no more operators
         */
        if (m_strIterator.current() == CharacterIterator.DONE) {
            m_operator = CharacterIterator.DONE;
            return true;
        }
        /**
         * Filter only valid operators
         */
        return isOperator(m_operator);
    }

    /**
     * This function returns true if char is one of delimeter
     * chars in expressions
     *
     * @param ch to test
     * @return true if char is one of delimeter
     * chars in expressions
     */
    private boolean isCharDelimeter(char ch) {
        return (ch < 'a' || ch > 'z') && (ch < 'A' || ch > 'Z') &&
            (ch < '0' || ch > '9') &&
            ch != '"' && ch != '$' && ch != '.' &&
            ch != '{' && ch != '}' && ch != '_' || ch == ',';
    }

    /**
     * This function returns true if string is a number
     *
     * @param str string to test
     * @return true if string is a number
     */
    private boolean isNumber(String str) {
        if (str == null || str.trim().equals("")) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    /**
     * This function returns true if char is operator
     *
     * @param ch to test
     * @return true if char is operator
     */
    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' ||
            ch == '*' || ch == '/' ||
            ch == '^' ||
            ch == '(' || ch == ')' ||
            ch == ',' || ch == Character.MAX_VALUE ||
            ch == CharacterIterator.DONE;
    }

    /**
     * This function returns true if string is parameter
     *
     * @param str to test
     * @return true if string is parameter
     */
    private boolean isParameter(String str) {
        return str.startsWith("$");
    }

    /**
     * This function returns true if string is a math constant
     * PI or E
     *
     * @param str to test
     * @return if string is a math constant
     * PI or E
     */
    private Constant isMathConstant(String str) {
        if ("pi".equalsIgnoreCase(str)) {
            return new Constant(Math.PI);
        } else if ("e".equalsIgnoreCase(str)) {
            return new Constant(Math.E);
        }
        return null;
    }
//************************* EVALUATION AND PARSE METHODS *************************

    private class InfLoopChecker {
        private int prev;

        public InfLoopChecker() {
            prev = m_strIterator.getIndex();
        }

        public void check() throws AlgorithmException {
            if (prev == m_strIterator.getIndex()) {
                throw new AlgorithmException("Infinite loop detected for \"" +
                    m_expression + "\" (" +
                    (prev + 1) + ": \"" +
                    m_strIterator.current() + "\")");
            } else {
                prev = m_strIterator.getIndex();
            }
        }
    }

    public iExpressionItem parse() throws AlgorithmException {
        checkBrackets();

        /** array of items */
        List<iExpressionItem> items = new ArrayList<iExpressionItem>();
        /** add first item */
        iExpressionItem item = evalExpression();
        if (item != null) {
            items.add(item);
        }
        /** iterate until all items divided by blanks are collected */
        final InfLoopChecker checker = new InfLoopChecker();
        while (m_strIterator.current() != CharacterIterator.DONE) {
            item = evalPlusMinus();
            if (item != null) {
                items.add(item);
            } else {
                break;
            }
            checker.check();
        }

        if (items.size() > 1) {
            if (m_functionProvider == null) {
                throw new AlgorithmException("Function provider is null for \"" +
                    m_expression + '\"');
            }

            final aFunction and = m_functionProvider.getFunction("and");
            if (and != null) {
                for (final iExpressionItem itm : items) {
                    and.addArgument(itm);
                }
            }
            return and;
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            throw new AlgorithmException("Empty items array for \"" +
                m_expression + '\"');
        }
    }

    private iExpressionItem evalExpression() throws AlgorithmException {
        if (!getNextOperatorAndOperand()) {
            return null;
        }
        return evalPlusMinus();
    }

    private iExpressionItem evalPlusMinus() throws AlgorithmException {
        iExpressionItem calculable = evalMultDiv();

        final InfLoopChecker checker = new InfLoopChecker();
        while (m_operator == '+' || m_operator == '-') {
            boolean sub = m_operator == '-';
            if (getNextOperatorAndOperand()) {
                iExpressionItem secondOperand = evalMultDiv();
                if (sub) {
                    secondOperand.setMultiplier(secondOperand.getMultiplier() * (-1));
                }
                calculable = new Addition(calculable, secondOperand);
            }
            checker.check();
        }
        return calculable;
    }

    private iExpressionItem evalMultDiv() throws AlgorithmException {
        iExpressionItem calculable = evalPower();

        final InfLoopChecker checker = new InfLoopChecker();
        while (m_operator == '*' || m_operator == '/') {
            boolean div = m_operator == '/';
            if (getNextOperatorAndOperand()) {
                iExpressionItem secondOperand = null;
                if (div) {
                    secondOperand = evalPower();
                } else {
                    secondOperand = evalMultDiv();
                }
                if (div) {
                    secondOperand = new Power(secondOperand, new Constant(-1));
                }
                calculable = new Multiplication(calculable, secondOperand);
            }
            checker.check();
        }
        return calculable;
    }

    private iExpressionItem evalPower() throws AlgorithmException {
        iExpressionItem calculable = evalUnaryOperation();
        if (m_operator == '^') {
            if (getNextOperatorAndOperand()) {
                iExpressionItem secondOperand = evalPower();
                calculable = new Power(calculable, secondOperand);
            }
        }

        return calculable;
    }

    private iExpressionItem evalUnaryOperation() throws AlgorithmException {
        iExpressionItem calculable = null;
        if ((m_operator == '-' || m_operator == '+') && "".equals(m_operand)) {
            boolean negative = m_operator == '-';
            if (!getNextOperatorAndOperand()) {
                return null;
            }
            calculable = evalMultDiv();
            if (negative) {
                calculable = calculable.multiply(new Constant(-1.0));
            }
        } else {
            calculable = evalBrackets();
        }
        return calculable;
    }

    private iExpressionItem evalBrackets() throws AlgorithmException {
        iExpressionItem calculable = null;
        if (m_operator == '(') {
            if (m_operand.length() == 0) {
                //Operator '('
                calculable = evalExpression();
                if (m_operator != ')') {
                    //ERROR: unclosed brackets
                }
                getNextOperatorAndOperand();
                if (m_operator == '(') {
                    iExpressionItem secondOperand = evalExpression();
                    calculable = new Multiplication(calculable, secondOperand);
                    getNextOperatorAndOperand();
                } else if (m_operator == '^') {
                    getNextOperatorAndOperand();
                    iExpressionItem secondOperand = null;
                    if (m_operator == '(') {
                        secondOperand = evalBrackets();
                    } else {
                        secondOperand = evalVariable();
                    }
                    calculable = new Power(calculable, secondOperand);
                }
            } else {
                //Function
                if (m_functionProvider != null) {
                    iFunction function = m_functionProvider.getFunction(m_operand);
                    if (function == null) {
                        throw new AlgorithmException("Function factory cannot create function " + m_operand);
                    }
                    calculable = evalExpression();
                    function.addArgument(calculable);
                    final InfLoopChecker checker = new InfLoopChecker();
                    while (m_operator == ',') {
                        calculable = evalExpression();
                        function.addArgument(calculable);
                        checker.check();
                    }
                    if (m_operator == ')') {
                        getNextOperatorAndOperand();
                        calculable = function;
                    }
                }
            }
        } else {
            calculable = evalVariable();
        }
        return calculable;
    }

    private iExpressionItem evalVariable() throws AlgorithmException {
        iExpressionItem result = null;
        /** parameter */
        if (isParameter(m_operand)) {
            result = getParameter(m_operand);
            if (result == null) {
                // result = new Variable(m_operand);
                result = Variable.create(m_operand);
            }
        }
        /** number */
        else if (isNumber(m_operand)) {
            result = new Constant(m_operand);
        }
        /** string */
        else if (m_operand.length() > 0 && m_operand.charAt(0) == '"') {
            result = new StringConstant(m_operand);
        }
        /** math constant */
        else if (isMathConstant(m_operand) != null) {
            result = isMathConstant(m_operand);
        }
        /** variable */
        else if (m_operand != null && !m_operand.trim().equals("")) {
            result = Variable.create(m_varProvider, m_operand);
        }
        return result;
    }

    private iExpressionItem getParameter(String name) throws AlgorithmException {
        iExpressionItem result = null;

        if (m_paramProvider != null) {
            if (mode == MODE_COPY_PARAMETER_AS_CONSTANT) {
                AbstractConstant constant = m_paramProvider.getParameterConstant(name);
                if (constant != null) {
                    result = constant.copy();
                }
            } else if (mode == MODE_COPY_PARAMETER_AS_TREE) {
                Parameter param = m_paramProvider.getParameter(name);
                if (param != null) {
                    result = param.getExpressionRoot();
                }
            } else if (mode == MODE_COPY_PARAMETER_AS_REFERENCE) {
                Parameter param = m_paramProvider.getParameter(name);
                if (param != null) {
                    result = new ParameterReference(param);
                }
            }
        }
        return result;
    }

    private void checkBrackets() throws AlgorithmException {
        int nOpened = 0;
        int nClosed = 0;
        //Calculate '(' and ')'
        for (int i = 0; i < m_expression.length(); i++) {
            char ch = m_expression.charAt(i);
            if (ch == '(') {
                nOpened++;
            } else if (ch == ')') {
                nClosed++;
            }
        }
        if (nOpened != nClosed) {
            throw new AlgorithmException("Number of '(' and ')' is different in: " + m_expression);
        }
    }
}
