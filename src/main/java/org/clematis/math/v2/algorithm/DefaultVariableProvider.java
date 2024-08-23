// Created: 01.12.2003 T 12:40:09
package org.clematis.math.v2.algorithm;

import org.clematis.math.v2.AbstractConstant;
import org.clematis.math.v2.Constant;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/**
 * Default variable provider is used to provide generic
 * random variable values to expression tree.
 */
public class DefaultVariableProvider implements iVariableProvider {
    /**
     * List of parameters.
     */
    protected ArrayList<Parameter> variables = new ArrayList<Parameter>();
    /**
     * Randomizer.
     */
    private static final Random rand = new Random(System.currentTimeMillis());
    /**
     * Maximum power
     */
    private int power = 15;
    /**
     * Hashed values
     */
    protected Hashtable<String, Double> hashed_parameter_values = new Hashtable<String, Double>();

    /**
     * Adds calculated parameter.
     *
     * @param var the parameter.
     */
    public void addVariable(Parameter var) {
        for (int i = 0; i < variables.size(); i++) {
            Parameter param = variables.get(i);
            if (var.getName().equals(param.getName())) {
                int index = variables.indexOf(param);
                variables.set(index, var);
                return;
            }
        }
        variables.add(var);
    }

    /**
     * Adds random value to variables
     */
    Double getRandomValue() {
        double value = rand.nextDouble() * Math.pow(Math.E, rand.nextInt(power));
        value = rand.nextBoolean() ? -value : value;
        return value;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Return variable constant
     *
     * @param in_varName parameter name
     * @return parameter value, string or double
     */
    public AbstractConstant getVariableConstant(String in_varName) {
        /**
         * Seek for previously added parameter
         */
        for (int i = 0; i < variables.size(); i++) {
            Parameter var = variables.get(i);
            if (in_varName.equals(var.getName())) {
                return var.getCurrentResult();
            }
        }
        /**
         * Seek in hashtable for generic random value
         */
        if (hashed_parameter_values.containsKey(in_varName)) {
            Double number = hashed_parameter_values.get(in_varName);
            return new Constant(number.doubleValue());
        }
        /**
         * Value not found, generate and save new one
         */
        else {
            Double number = getRandomValue();
            hashed_parameter_values.put(in_varName, number);
            return new Constant(number.doubleValue());
        }
    }

    /**
     * Checks whether the string is a parameter name.
     *
     * @param name checked string.
     * @return <code>true</code> if it is a parameter name or <code>false</code> in otherwise.
     */
    protected boolean isVarName(String name) {
        for (int i = 0; i < variables.size(); i++) {
            Parameter param = variables.get(i);
            if (name.equals(param.getName())) {
                return true;
            }
        }
        return false;
    }
}
