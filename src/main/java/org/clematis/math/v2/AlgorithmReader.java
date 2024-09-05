// Created: Jan 17, 2003 T 5:13:06 PM
package org.clematis.math.v2;

import org.clematis.math.v1.io.XMLConstants;
import org.clematis.math.v2.algorithm.Algorithm;
import org.clematis.math.v2.algorithm.Parameter;
import org.clematis.math.v2.functions.generic;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Parses algorithm in following format per line: [parameter]=[code];
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
        ArrayList<String> statements = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(code, ";@");
        StringBuilder previousStatements = new StringBuilder();
        boolean opened = false;

        while (st.hasMoreTokens()) {
            String statement = st.nextToken().trim();
            /** if ; is inside of "quoted expression", do not treat it as end of line */
            for (int i = 0; i < statement.length(); i++) {
                char ch = statement.charAt(i);
                if (ch == '\"') {
                    opened = !opened;
                }
            }
            if (opened) {
                previousStatements.append(statement + ";");
                continue;
            }
            if (previousStatements.length() > 0) {
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
     * @throws AlgorithmException
     */
    private void parseStatement(String statement, Algorithm algorithm)
        throws AlgorithmException {
        String name = null;
        String code = null;
        StringBuilder buffer = new StringBuilder(1024);
        /**
         * Start position of parameter code
         */
        int pos = 0;

        Parameter param = null;

        if (statement.startsWith(XMLConstants.CONDITION_NAME)) {
            name = XMLConstants.CONDITION_NAME;
            code = statement.substring(XMLConstants.CONDITION_NAME.length() + 1);

            param = new Parameter(name, code);
            param.setCondition(true);
            algorithm.addParameter(param);
        } else if (statement.startsWith(generic.GENERIC_FUNCTION_NAME)) {
            /** add generic function to algorithm */
            algorithm.addFunction(generic.create(statement));
        } else {
            pos = statement.indexOf('=');
            if (pos == -1) {
                throw new AlgorithmException("Operand '=' is missed: " + statement);
            }
            name = parseParameterName(statement, pos);
            code = statement.substring(pos + 1);
            param = new Parameter(name, code);
            algorithm.addParameter(param);
            buffer.setLength(0);
        }
    }

    /**
     * Parse and validate parameter token. Such token should start with
     * $ sign and shouldn't be empty.
     *
     * @param statement string statement
     * @param delimPos  position of delimeter
     * @return valid parameter token
     * @throws AlgorithmException is thrown if parameter token is invalid.
     */
    private String parseParameterName(String statement, int delimPos)
        throws AlgorithmException {
        String name = statement.substring(0, delimPos).trim();
        if (!name.startsWith("$")) {
            throw new AlgorithmException("Parameter token should starts with '$': " + name);
        }
        if (name.length() <= 1) {
            throw new AlgorithmException("Parameter token is empty");
        }
        return name;
    }

}
