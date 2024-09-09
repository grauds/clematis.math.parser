// Created: 31.05.2004 T 14:59:58
package org.clematis.math.io;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Format settings for parameter output
 */
@Getter
@Setter
public class OutputFormatSettings implements Serializable {
    /**
     * Output formatting flag
     */
    private boolean noExponent = false;
    /**
     * Comma separated thousands, "grouping" mode
     */
    private boolean grouping = false;
    /**
     * External sdEnable flag
     */
    private boolean sdEnabled = false;
    /**
     * Tolerance policy
     */
    private boolean toleranceEnabled = true;

    public OutputFormatSettings() {
    }

    public OutputFormatSettings(OutputFormatSettings formatSettings) {
        this.noExponent = formatSettings.isNoExponent();
        this.grouping = formatSettings.isGrouping();
        this.sdEnabled = formatSettings.isSdEnabled();
        this.toleranceEnabled = formatSettings.isToleranceEnabled();
    }
}