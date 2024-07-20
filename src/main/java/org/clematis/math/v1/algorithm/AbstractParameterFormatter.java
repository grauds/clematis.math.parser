// Created: 14.04.2005 T 14:56:28
package org.clematis.math.v1.algorithm;

import org.clematis.math.v1.OutputFormatSettings;

/**
 *
 */
abstract class AbstractParameterFormatter implements iParameterProvider {
    /**
     * Output text format settings
     */
    protected OutputFormatSettings formatSettings = new OutputFormatSettings();

    /**
     * Get format settings from this algorithm
     *
     * @return format settings from this algorithm
     */
    public OutputFormatSettings getFormatSettings() {
        return formatSettings;
    }

    /**
     * Sets format settings for this algorithm
     */
    public void setFormatSettings(OutputFormatSettings fs) {
        this.formatSettings = new OutputFormatSettings(fs);
    }
}
