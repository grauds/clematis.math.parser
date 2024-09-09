// $Id: AlgorithmReader.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Jan 17, 2003 T 5:13:06 PM

package org.clematis.math.v1;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.clematis.math.AlgorithmException;
import org.clematis.math.v1.algorithm.Algorithm;
import org.clematis.math.v1.algorithm.Parameter;
import org.clematis.math.v1.functions.GenericFunction;
import org.clematis.math.v1.io.XMLConstants;

/**
 * Parses the question algrorithm used for dynamical changing of the
 * question parameters.
 * The algorithm code is value of "algorithm" field in question bank.
 */
public class AlgorithmReader {

    /**
     * Parses the code and creates the list of parameters.
     *
     * @param code the algorithm code from question bank.
     */
    public Algorithm createAlgorithm(String code)
        throws Exception {
        Algorithm algorithm = new Algorithm();
        ArrayList<String> statements = getStatements(code);
        for (int i = 0; i < statements.size(); i++) {
            try {
                parseStatement(statements.get(i), algorithm);
            } catch (AlgorithmException ex) {
                throw new AlgorithmException(ex.getMessage(), i + 1);
            }
        }
        return algorithm;
    }

    /**
     * Parses the code and returns the list with statements like:
     * $paramname=expression.
     *
     * @param code the parsed code.
     * @return statements list.
     */
    private ArrayList<String> getStatements(String code) {
        ArrayList<String> statements = new ArrayList<>();
        StringTokenizer st = new StringTokenizer(code, ";@");
        StringBuilder previousStatements = new StringBuilder();
        boolean opened = false;

        while (st.hasMoreTokens()) {
            String statement = st.nextToken().trim();
            /* if ; is inside of "quoted expression", do not treat it as end of line */
            for (int i = 0; i < statement.length(); i++) {
                char ch = statement.charAt(i);
                if (ch == '\"') {
                    opened = !opened;
                }
            }
            if (opened) {
                previousStatements.append(statement).append(";");
                continue;
            }
            if (!previousStatements.isEmpty()) {
                statement = previousStatements + statement;
                previousStatements = new StringBuilder();
            }
            if (!statement.startsWith("comment:")) {
                statements.add(statement);
            }
        }
        return statements;
    }

    /**
     * Parse statement like $a = sig ( $k * 7 )
     * to form <code>Parameter</code> and add it to
     * new algorithm.
     *
     * @param statement string statement
     * @param algorithm new algorithm
     * @throws AlgorithmException if parsing isn't successful
     */
    private void parseStatement(String statement, Algorithm algorithm) throws AlgorithmException {

        /*
         * Start position of parameter code
         */
        int pos;
        String name;
        String code;
        StringBuilder buffer = new StringBuilder();
        Parameter param;

        if (statement.startsWith(XMLConstants.CONDITION_NAME)) {
            name = XMLConstants.CONDITION_NAME;
            code = statement.substring(XMLConstants.CONDITION_NAME.length() + 1);

            param = new Parameter(name, code);
            param.setCondition(true);
            algorithm.addParameter(param);
        } else if (statement.startsWith(GenericFunction.GENERIC_FUNCTION_NAME)) {
            /* add generic function to algorithm */
            algorithm.addFunction(GenericFunction.create(statement));
        } else {
            pos = statement.indexOf('=');
            if (pos == -1) {
                throw new AlgorithmException("Operand '=' is missed: " + statement);
            }
            name = parseParameterName(statement, pos);
            param = new Parameter(name, buffer.toString());
            algorithm.addParameter(param);
            buffer.setLength(0);
        }
    }

    /**
     * Parse and validate parameter name. Such name should start with
     * $ sign and shouldn't be empty.
     *
     * @param statement string statement
     * @param delimPos  position of delimeter
     * @return valid parameter name
     * @throws AlgorithmException is thrown if parameter name is invalid.
     */
    private String parseParameterName(String statement, int delimPos)
        throws AlgorithmException {
        String name = statement.substring(0, delimPos).trim();
        if (!name.startsWith("$")) {
            throw new AlgorithmException("Parameter name should starts with '$': " + name);
        }
        if (name.length() == 1) {
            throw new AlgorithmException("Parameter name is empty");
        }
        return name;
    }

}
