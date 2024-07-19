// Created: 31.05.2004 T 14:59:58
package org.clematis.math.reference;

import java.io.Serializable;

/**
 * Format settings for parameter output
 */
public class OutputFormatSettings implements Serializable
{
    /**
     * Output formatting flag
     */
    private boolean noExponent = false;
    /**
     * Comma separated thousands,
     * "grouping" mode
     */
    private boolean grouping = false;
    /**
     * External sdEnable flag
     */
    private boolean sdEnable = false;
    /**
     * Tolerance policy - take policy from question or
     * from assignment
     */
    private boolean qTolerance = true;

    public OutputFormatSettings() {};

    public OutputFormatSettings(OutputFormatSettings formatSettings)
    {
        this.noExponent = formatSettings.isNoExponent();
        this.grouping = formatSettings.isGrouping();
        this.sdEnable = formatSettings.getSdEnable();
        this.qTolerance = formatSettings.isqTolerance();
    }

    public boolean isNoExponent()
    {
        return noExponent;
    }

    public void setNoExponent(boolean noExponent)
    {
        this.noExponent = noExponent;
    }

    public boolean isGrouping()
    {
        return grouping;
    }

    public void setGrouping(boolean grouping)
    {
        this.grouping = grouping;
    }

    public boolean getSdEnable()
    {
        return sdEnable;
    }

    public void setSdEnable(boolean sdEnable)
    {
        this.sdEnable = sdEnable;
    }
    /**
     * Returns question tolerance mode flag
     *
     * @return question tolerance mode flag
     */
    public boolean isqTolerance()
    {
        return qTolerance;
    }
    /**
     * Sets question tolerance mode
     *
     * @param qTolerance question tolerance mode flag
     */
    public void setqTolerance(boolean qTolerance)
    {
        this.qTolerance = qTolerance;
    }
}