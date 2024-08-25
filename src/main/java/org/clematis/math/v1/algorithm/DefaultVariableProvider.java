// Created: 01.12.2003 T 12:40:09
package org.clematis.math.v1.algorithm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import org.clematis.math.v1.AbstractConstant;
import org.clematis.math.v1.Constant;

/**
 * Default parameter provider is used to provide generic
 * random variable values to expression tree.
 */
public class DefaultVariableProvider implements IVariableProvider {
    /**
     * Randomizer.
     */
    private static final Random RAND = new Random(System.currentTimeMillis());
    /**
     * Maximum power
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    private static int power = 15;
    /**
     * List of parameters.
     */
    protected ArrayList<Parameter> variables = new ArrayList<>();
    /**
     * Hashed values
     */
    protected Hashtable<String, Double> valuesCache = new Hashtable<>();
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
    double getRandomValue() {
        double value = RAND.nextDouble() * Math.pow(Math.E, RAND.nextInt(power));
        value = RAND.nextBoolean() ? -value : value;
        return value;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        DefaultVariableProvider.power = power;
    }

    /**
     * Return variable constant
     *
     * @param name parameter name
     * @return parameter value, name or double
     */
    public AbstractConstant getVariableConstant(String name) {

        AbstractConstant result = null;

        /*
         * Seek for previously added parameter
         */
        for (Parameter var : variables) {
            if (name.equals(var.getName())) {
                result = var.getCurrentResult();
                break;
            }
        }

        /*
         * Seek in hashtable for generic random value
         */
        if (result == null) {
            if (valuesCache.containsKey(name)) {
                Double number = valuesCache.get(name);
                result = new Constant(number);
            } else {
                /*
                 * If value not found, generate and save new one
                 */
                double number = getRandomValue();
                valuesCache.put(name, number);
                result = new Constant(number);
            }
        }

        return result;
    }
}
