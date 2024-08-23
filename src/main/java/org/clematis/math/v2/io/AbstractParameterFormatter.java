// Created: 14.04.2005 T 14:56:28
package org.clematis.math.v2.io;

import org.clematis.math.v2.algorithm.iParameterProvider;

/**
 * This class is a format provider and also formats output
 */
public abstract class AbstractParameterFormatter implements iParameterProvider {
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
