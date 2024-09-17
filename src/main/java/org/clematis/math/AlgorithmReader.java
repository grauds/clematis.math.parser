// $Id: AlgorithmReader.java,v 1.1 2009-04-27 07:11:09 anton Exp $
// Created: Jan 17, 2003 T 5:13:06 PM

package org.clematis.math;

import java.util.ArrayList;
import java.util.StringTokenizer;

import org.clematis.math.algorithm.Algorithm;
import org.clematis.math.algorithm.Parameter;
import org.clematis.math.functions.GenericFunction;
import org.clematis.math.parsers.v1.DefaultParameterFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * Reads algorithm lines and produces parameters which hold name and algorithmic code.
 * This class doesn't parse algorithmic code.
 */
@Getter
@Setter
public class AlgorithmReader {

    IParameterFactory parameterFactory = new DefaultParameterFactory();

    /**
     * Reads code and creates the list of parameters.
     *
     * @param code the algorithm code
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
     * Reads the code and returns the list with statements like:
     * $paramname=expression.
     *
     * @param code from the algorithm line
     * @return statements list
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

        int pos;
        String name;
        String code;
        Parameter param;

        if (statement.startsWith(XMLConstants.CONDITION_NAME)) {
            name = XMLConstants.CONDITION_NAME;
            code = statement.substring(XMLConstants.CONDITION_NAME.length() + 1);
            param = createParameter(name, code);
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
            code = statement.substring(pos + 1);
            param = createParameter(name, code);
            algorithm.addParameter(param);
        }
    }

    public Parameter createParameter(String name, String code) {
        return parameterFactory.createParameter(name, code);
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
