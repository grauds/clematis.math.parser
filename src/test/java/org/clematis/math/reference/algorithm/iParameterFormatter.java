// Created: 14.04.2005 T 15:01:38
package org.clematis.math.reference.algorithm;

import org.clematis.math.reference.OutputFormatSettings;

/**
 * Parameter formatter
 */
public interface iParameterFormatter
{
    /**
     * Get format settings from this algorithm
     *
     * @return format settings from this algorithm
     */
    OutputFormatSettings getFormatSettings();
    /**
     * Sets format settings for this algorithm
     */
    void setFormatSettings(OutputFormatSettings fs);
}
