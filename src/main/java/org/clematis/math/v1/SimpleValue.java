// Created: 14.07.2005 T 17:54:50
package org.clematis.math.v1;

/**
 * Bean shell algorithm value
 */
public class SimpleValue implements iValue {
    protected String value = null;

    public SimpleValue() {
    }

    public SimpleValue(String value) {
        this.value = value;
    }

    public String getValue(OutputFormatSettings fs) {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
