// Created: 14.04.2005 T 14:56:28
package org.clematis.math.io;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractParameterFormatter {
    /**
     * Output text format settings
     */
    protected OutputFormatSettings formatSettings = new OutputFormatSettings();
}
