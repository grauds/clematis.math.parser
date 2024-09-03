// Created: 14.07.2005 T 17:54:50
package org.clematis.math.v1;

import lombok.Getter;
import lombok.Setter;

import org.clematis.math.v1.io.OutputFormatSettings;

/**
 * Bean shell algorithm value
 */
@Getter
@Setter
public class SimpleValue implements IValue {

    protected String value = null;

    public SimpleValue() {
    }

    public SimpleValue(String value) {
        this.value = value;
    }

    public String getValue(OutputFormatSettings fs) {
        return value;
    }

}
