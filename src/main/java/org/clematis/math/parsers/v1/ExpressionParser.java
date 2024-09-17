// Created: Jan 17, 2003 T 7:46:47 PM
package org.clematis.math.parsers.v1;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;

import org.clematis.math.AbstractConstant;
import org.clematis.math.AlgorithmException;
import org.clematis.math.Constant;
import org.clematis.math.IExpressionItem;
import org.clematis.math.IFunction;
import org.clematis.math.StringConstant;
import org.clematis.math.Variable;
import org.clematis.math.algorithm.DefaultParameterProvider;
import org.clematis.math.algorithm.IFunctionProvider;
import org.clematis.math.algorithm.ISimpleParameterProvider;
import org.clematis.math.algorithm.IVariableProvider;
import org.clematis.math.algorithm.Parameter;
import org.clematis.math.algorithm.ParameterReference;
import org.clematis.math.operations.Addition;
import org.clematis.math.operations.Multiplication;
import org.clematis.math.operations.Power;

import lombok.Setter;

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
    String expression;
    /**
     * Current operator
     */
    char operator;
    /**
     * Previous operator
     */
    char prevOperator = '\0';
    /**
     * Operand
     */
    String operand = null;
    /**
     * Parameter provider, always not null
     */
    ISimpleParameterProvider paramProvider = new DefaultParameterProvider();
    /**
     * Variable provider
     */
    IVariableProvider varProvider = null;
    /**
     * Function provider
     */
    IFunctionProvider functionProvider = null;
    /**
     * Buffer to pick up letters of operand
     */
    StringBuilder operandBuffer = new StringBuilder();
    /**
     * String iterator
     */
    StringCharacterIterator strIterator;
    /**
     *  Copy constant flag. If set to false, expression root is copied in evaluation of variable
     *  Sets parameter replacement mode
     *  Can be either
     *  <p/>
     *  MODE_COPY_PARAMETER_AS_CONSTANT = 1;
     *  MODE_COPY_PARAMETER_AS_TREE = 2;
     *  MODE_COPY_PARAMETER_AS_REFERENCE = 3;
     */
    @Setter
    private int mode = MODE_COPY_PARAMETER_AS_CONSTANT;

    /**
     * Constructor with only expression to be parsed
     * @param expression to parse
     */
    public ExpressionParser(String expression) {
        this.expression = expression;
        this.strIterator = new StringCharacterIterator(expression);
        this.operator = CharacterIterator.DONE;
    }

    /**
     * Constructor with parameter provider. Each parameter starts with dollar sign - $.
     * This constructor is made private to avoid implicit usage without function factory.
     *
     * @param expression to parse
     * @param paramProvider parameter provider
     */
    private ExpressionParser(String expression, ISimpleParameterProvider paramProvider) {
        this(expression);
        this.paramProvider = paramProvider;
    }

    /**
     * Constructor with parameter and variable providers.
     *
     * @param expression    expression to parse
     * @param paramProvider parameter provider
     * @param varProvider   variables provider
     */
    public ExpressionParser(String expression,
                            ISimpleParameterProvider paramProvider,
                            IVariableProvider varProvider) {
        this(expression, paramProvider);
        this.varProvider = varProvider;
    }

    /**
     * Constructor parameter, variable and function providers.
     *
     * @param expression    expression to parse
     * @param paramProvider parameter provider
     * @param varProvider   variables provider
     * @param functionProvider functions provider
     */
    public ExpressionParser(String expression,
                            ISimpleParameterProvider paramProvider,
                            IVariableProvider varProvider,
                            IFunctionProvider functionProvider) {
        this(expression, paramProvider, varProvider);
        this.functionProvider = functionProvider;
    }

    /**
     * Advances one step further in parsing string
     *
     * @return true if success
     */
    @SuppressWarnings({
        "checkstyle:NestedIfDepth",
        "checkstyle:CyclomaticComplexity",
        "checkstyle:ReturnCount"
    })
    private boolean getNextOperatorAndOperand() {
        /*
         * Take next char as operator
         */
        if (operator == strIterator.current()) {
            operator = strIterator.next();
        }
        /*
         * No operands, no operators
         */
        if (strIterator.current() == CharacterIterator.DONE) {
            operator = CharacterIterator.DONE;
            return false;
        }
        /*
         * Clean operand buffer
         */
        operandBuffer.delete(0, operandBuffer.length());
        /*
         * Remember previous char
         */
        char prevCh = strIterator.current();
        /*
         * Adds chars as simple text, without parsing,
         * if this flag is set to true
         */
        boolean text = false;

        for (char ch = strIterator.current(); ch != CharacterIterator.DONE; ch = strIterator.next()) {
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
                    prevOperator = operator;
                    operator = ch;
                    break;
                }
                /* parsing after exponent **/
                if (ch == 'e' || ch == 'E') {
                    /* only in case of for example ...8E */
                    if (prevCh >= '0' && prevCh <= '9') {
                        operandBuffer.append(ch);
                        /* Counter of chars after E */
                        int charCounter = 0;
                        /* Take next chars */
                        while (true) {
                            ch = strIterator.next();
                            if ((charCounter == 0 && (ch == '+' || ch == '-'))
                                || (ch >= '0' && ch <= '9')
                            ) {
                                charCounter++;
                                operandBuffer.append(ch);
                            } else {
                                /*
                                 * Return to previous
                                 */
                                ch = strIterator.previous();
                                break;
                            }
                        }
                        prevCh = ch;
                        continue;
                    }
                }
            }
            // append char to operator
            operandBuffer.append(ch);
            prevCh = ch;
        }
        /*
         * Finally got operand
         */
        operand = operandBuffer.toString();
        /*
         * Get last operand, no more operators
         */
        if (strIterator.current() == CharacterIterator.DONE) {
            operator = CharacterIterator.DONE;
            return true;
        }
        /*
         * Filter only valid operators
         */
        return isOperator(operator);
    }

    /**
     * This function returns true if char is operator
     *
     * @param ch to test
     * @return true if char is operator
     */
    private boolean isOperator(char ch) {
        return ch == '+'
            || ch == '-'
            || ch == '*' || ch == '/'
            || ch == '^' || ch == '('
            || ch == ')' || ch == ','
            || ch == CharacterIterator.DONE;
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
        return (ch < 'a' || ch > 'z')
            && (ch < 'A' || ch > 'Z')
            && (ch < '0' || ch > '9')
            && ch != '"' && ch != '$'
            && ch != '.' && ch != '{'
            && ch != '}' && ch != '_';
    }

    /**
     * This function returns true if string is a number
     *
     * @param str string to test
     * @return true if string is a number
     */
    private boolean isNumber(String str) {
        if (str != null && !str.trim().isEmpty()) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException ignored) {
            }
        }
        return false;
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
        Constant result = null;
        if ("pi".equalsIgnoreCase(str)) {
            result = new Constant(Math.PI);
        } else if ("e".equalsIgnoreCase(str)) {
            result = new Constant(Math.E);
        }
        return result;
    }

    public IExpressionItem parse() throws AlgorithmException {
        checkBrackets();

        /* array of items */
        List<IExpressionItem> items = new ArrayList<>();
        /* add first item */
        IExpressionItem item = evalExpression();
        if (item != null) {
            items.add(item);
        }
        /* iterate until all items divided by blanks are collected */
        final InfLoopChecker checker = new InfLoopChecker();
        while (strIterator.current() != CharacterIterator.DONE) {
            item = evalPlusMinus();
            if (item != null) {
                items.add(item);
            } else {
                break;
            }
            checker.check();
        }

        if (items.size() > 1) {
            if (functionProvider == null) {
                throw new AlgorithmException("Function provider is null for \""
                    + expression + '\"');
            }

            final IFunction and = functionProvider.getFunction("and");
            if (and != null) {
                for (final IExpressionItem itm : items) {
                    and.addArgument(itm);
                }
            }
            return and;
        } else if (items.size() == 1) {
            return items.get(0);
        } else {
            throw new AlgorithmException("Empty items array for \""
                + expression + '\"');
        }
    }

    private IExpressionItem evalExpression() throws AlgorithmException {
        if (!getNextOperatorAndOperand()) {
            return null;
        }
        return evalPlusMinus();
    }

    private IExpressionItem evalPlusMinus() throws AlgorithmException {
        IExpressionItem calculable = evalMultDiv();

        final InfLoopChecker checker = new InfLoopChecker();
        while (operator == '+' || operator == '-') {
            boolean sub = operator == '-';
            if (getNextOperatorAndOperand()) {
                IExpressionItem secondOperand = evalMultDiv();
                if (sub) {
                    secondOperand.setMultiplier(secondOperand.getMultiplier() * (-1));
                }
                calculable = new Addition(calculable, secondOperand);
            }
            checker.check();
        }
        return calculable;
    }

    private IExpressionItem evalMultDiv() throws AlgorithmException {
        IExpressionItem calculable = evalPower();

        final InfLoopChecker checker = new InfLoopChecker();
        while (operator == '*' || operator == '/') {
            boolean div = operator == '/';
            if (getNextOperatorAndOperand()) {
                IExpressionItem secondOperand;
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

    private IExpressionItem evalPower() throws AlgorithmException {
        IExpressionItem calculable = evalUnaryOperation();
        if (operator == '^') {
            if (getNextOperatorAndOperand()) {
                IExpressionItem secondOperand = evalPower();
                calculable = new Power(calculable, secondOperand);
            }
        }

        return calculable;
    }

    private IExpressionItem evalUnaryOperation() throws AlgorithmException {
        IExpressionItem calculable;
        if ((operator == '-' || operator == '+') && "".equals(operand)) {
            boolean negative = operator == '-';
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

    @SuppressWarnings("checkstyle:NestedIfDepth")
    private IExpressionItem evalBrackets() throws AlgorithmException {
        IExpressionItem calculable = null;
        if (operator == '(') {
            if (operand.isEmpty()) {
                //Operator '('
                calculable = evalExpression();
                //if (operator != ')') {
                    //ERROR: unclosed brackets
                //}
                getNextOperatorAndOperand();
                if (operator == '(') {
                    IExpressionItem secondOperand = evalExpression();
                    calculable = new Multiplication(calculable, secondOperand);
                    getNextOperatorAndOperand();
                } else if (operator == '^') {
                    getNextOperatorAndOperand();
                    IExpressionItem secondOperand;
                    if (operator == '(') {
                        secondOperand = evalBrackets();
                    } else {
                        secondOperand = evalVariable();
                    }
                    calculable = new Power(calculable, secondOperand);
                }
            } else {
                //Function
                if (functionProvider != null) {
                    IFunction function = functionProvider.getFunction(operand);
                    if (function == null) {
                        throw new AlgorithmException("Function factory cannot create function " + operand);
                    }
                    calculable = evalExpression();
                    function.addArgument(calculable);
                    final InfLoopChecker checker = new InfLoopChecker();
                    while (operator == ',') {
                        calculable = evalExpression();
                        function.addArgument(calculable);
                        checker.check();
                    }
                    if (operator == ')') {
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

    private IExpressionItem evalVariable() {
        IExpressionItem result = null;
        /* parameter */
        if (isParameter(operand)) {
            result = getParameter(operand);
            if (result == null) {
                // result = new Variable(m_operand);
                result = Variable.create(operand);
            }
        } else if (isNumber(operand)) {
            /* number */
            result = new Constant(operand);
        } else if (!operand.isEmpty() && operand.charAt(0) == '"') {
            /* string */
            result = new StringConstant(operand);
        }  else if (isMathConstant(operand) != null) {
            /* math constant */
            result = isMathConstant(operand);
        } else if (operand != null && !operand.trim().isEmpty()) {
            /* variable */
            result = Variable.create(varProvider, operand);
        }
        return result;
    }

    @SuppressWarnings("checkstyle:NestedIfDepth")
    private IExpressionItem getParameter(String name) {
        IExpressionItem result = null;

        if (paramProvider != null) {
            if (mode == MODE_COPY_PARAMETER_AS_CONSTANT) {
                AbstractConstant constant = paramProvider.getParameterConstant(name);
                if (constant != null) {
                    result = constant.copy();
                }
            } else if (mode == MODE_COPY_PARAMETER_AS_TREE) {
                Parameter param = paramProvider.getParameter(name);
                if (param != null) {
                    result = param.getExpressionRoot();
                }
            } else if (mode == MODE_COPY_PARAMETER_AS_REFERENCE) {
                Parameter param = paramProvider.getParameter(name);
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
        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);
            if (ch == '(') {
                nOpened++;
            } else if (ch == ')') {
                nClosed++;
            }
        }
        if (nOpened != nClosed) {
            throw new AlgorithmException("Number of '(' and ')' is different in: " + expression);
        }
    }



    private class InfLoopChecker {
        private int prev;

        InfLoopChecker() {
            prev = strIterator.getIndex();
        }

        public void check() throws AlgorithmException {
            if (prev == strIterator.getIndex()) {
                throw new AlgorithmException("Infinite loop detected for \""
                    + expression + "\" ("
                    + (prev + 1) + ": \""
                    + strIterator.current() + "\")");
            } else {
                prev = strIterator.getIndex();
            }
        }
    }

}
