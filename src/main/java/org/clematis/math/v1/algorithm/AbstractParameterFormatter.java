// Created: 14.04.2005 T 14:56:28
package org.clematis.math.v1.algorithm;

import org.clematis.math.v1.OutputFormatSettings;

import lombok.Getter;
/**
 *
 */
@Getter
abstract class AbstractParameterFormatter implements iParameterProvider {

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
