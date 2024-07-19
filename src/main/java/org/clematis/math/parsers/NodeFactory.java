// Created: 05.01.2007 T 18:02:03
package org.clematis.math.parsers;

import org.clematis.math.*;
import org.clematis.math.operations.*;
import org.clematis.math.parsers.string.StringMathParser;
import org.clematis.math.parsers.webtex.WebTexParser;

/**
 * Factory for simple nodes of different types.
 */
public class NodeFactory {
    /**
     * Parameter handling mode - as constant
     */
    public static final int MODE_COPY_PARAMETER_AS_CONSTANT = 0;
    /**
     * Parameter handling mode - as expression tree
     */
    public static final int MODE_COPY_PARAMETER_AS_TREE = 1;
    /**
     * Parameter handling mode - as simple reference to constant
     */
    public static final int MODE_COPY_PARAMETER_AS_REFERENCE = 2;
    /**
     * Pluggable string math parser
     */
    public static final int STRING_MATH_PARSER = 0;
    /**
     * Pluggable webtex math parser
     */
    public static final int WEBTEX_MATH_PARSER = 1;

    /**
     * Create a custom node for the string parser
     *
     * @param id from string math parser constant
     * @return node for the string parser
     */
    public static Node createNode(int id, Object parser) {
        if (parser instanceof StringMathParser) {
            return createNodeFromString(id, (StringMathParser) parser);
        } else if (parser instanceof WebTexParser) {
            return createNodeFromWebTex(id, (WebTexParser) parser);
        }
        return new SimpleNode(id);
    }

    private static Node createNodeFromString(int id, StringMathParser parser) {
        switch (id) {
            case StringMathParserTreeConstants.JJTVOID:
            case StringMathParserTreeConstants.JJTEXPR:
            case StringMathParserTreeConstants.JJTVECTOR_EXPR:
            case StringMathParserTreeConstants.JJTINDEX_EXPR:
            case StringMathParserTreeConstants.JJTEQUALS_EXPR:
                break;
            case StringMathParserTreeConstants.JJTPLUS_EXPR: {
                return new Addition(id);
            }
            case StringMathParserTreeConstants.JJTMINUS_EXPR: {
                return new Substraction(id);
            }
            case StringMathParserTreeConstants.JJTMULT_EXPR: {
                return new Multiplication(id);
            }
            case StringMathParserTreeConstants.JJTDIV_EXPR: {
                return new SimpleFraction(id);
            }
            case StringMathParserTreeConstants.JJTUNARY_MINUS: {
                Unary unary = new Unary();
                unary.setTokenKind(Unary.MINUS);
                return unary;
            }
            case StringMathParserTreeConstants.JJTUNARY_PLUS: {
                Unary unary = new Unary();
                unary.setTokenKind(Unary.PLUS);
                return unary;
            }
            case StringMathParserTreeConstants.JJTPOWER_EXPR: {
                return new Power(id);
            }
            case StringMathParserTreeConstants.JJTSTRING: {
                return new StringConstant(id);
            }
            case StringMathParserTreeConstants.JJTVAR: {
                return new SimpleParameter(id);
            }
            case StringMathParserTreeConstants.JJTNUMBER: {
                return new Constant();
            }
            case StringMathParserTreeConstants.JJTIDENTIFIER: {
                return new Variable(id);
            }
            case StringMathParserTreeConstants.JJTCONSTANT: {
                return new Constant(id);
            }
            case StringMathParserTreeConstants.JJTDELTA_EXPR: {
                return new StringConstant(id);
            }
            case StringMathParserTreeConstants.JJTHAT_EXPR: {
                return new StringConstant(id);
            }
            case StringMathParserTreeConstants.JJTFUNCTION: {
                return new FunctionReference(id, parser.getFunctionProvider());
            }
            default:

        }
        return new SimpleNode(id);
    }

    private static Node createNodeFromWebTex(int id, WebTexParser parser) {
        switch (id) {
            case WebTexParserTreeConstants.JJTVOID:
            case WebTexParserTreeConstants.JJTEXPR:
            case WebTexParserTreeConstants.JJTVECTOR_EXPR:
            case WebTexParserTreeConstants.JJTINDEX_EXPR:
            case WebTexParserTreeConstants.JJTEQUALS_EXPR:
                break;
            case WebTexParserTreeConstants.JJTPLUS_EXPR: {
                return new Addition(id);
            }
            case WebTexParserTreeConstants.JJTMINUS_EXPR: {
                return new Substraction(id);
            }
            case WebTexParserTreeConstants.JJTMULT_EXPR: {
                return new Multiplication(id);
            }
            case WebTexParserTreeConstants.JJTDIV_EXPR: {
                return new SimpleFraction(id);
            }
            case WebTexParserTreeConstants.JJTUNARY_MINUS: {
                Unary unary = new Unary();
                unary.setTokenKind(Unary.MINUS);
                return unary;
            }
            case WebTexParserTreeConstants.JJTUNARY_PLUS: {
                Unary unary = new Unary();
                unary.setTokenKind(Unary.PLUS);
                return unary;
            }
            case WebTexParserTreeConstants.JJTPOWER_EXPR: {
                return new Power(id);
            }
            case WebTexParserTreeConstants.JJTSTRING: {
                return new StringConstant(id);
            }
            case WebTexParserTreeConstants.JJTVAR: {
                return new SimpleParameter(id);
            }
            case WebTexParserTreeConstants.JJTNUMBER: {
                return new Constant();
            }
            case WebTexParserTreeConstants.JJTIDENTIFIER: {
                return new Variable(id);
            }
            case WebTexParserTreeConstants.JJTCONSTANT: {
                return new Constant(id);
            }
            case WebTexParserTreeConstants.JJTDELTA_EXPR: {
                return new StringConstant(id);
            }
            case WebTexParserTreeConstants.JJTHAT_EXPR: {
                return new StringConstant(id);
            }
            case WebTexParserTreeConstants.JJTFUNCTION: {
                return new FunctionReference(id, parser.getFunctionProvider());
            }
            default:

        }
        return new SimpleNode(id);
    }
}
