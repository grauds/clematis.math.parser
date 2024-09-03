// Created: 14.04.2005 T 15:01:38
package org.clematis.math.v1.io;

import org.jdom2.Element;

/**
 * Parameter formatter
 */
public interface IParameterFormatter {
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

    /**
     * Output parameters to XML
     * @return xml representation of the algorithm
     */
    Element save();

    /**
     * Output parameters to XML
     * @return xml representation of the algorithm
     */
    Element toXML();
}
