// Created: 14.04.2005 T 14:56:28
package org.clematis.math.v1.io;

import org.clematis.math.v1.algorithm.IParameterProvider;

import lombok.Getter;

@Getter
public abstract class AbstractParameterFormatter implements IParameterProvider {

    /**
     * Output text format settings
     */
    protected OutputFormatSettings formatSettings = new OutputFormatSettings();

    /**
     * Sets format settings for this algorithm
     */
    public void setFormatSettings(OutputFormatSettings fs) {
        this.formatSettings = new OutputFormatSettings(fs);
    }
}
