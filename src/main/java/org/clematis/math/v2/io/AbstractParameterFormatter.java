// Created: 14.04.2005 T 14:56:28
package org.clematis.math.v2.io;

import org.clematis.math.v2.algorithm.IParameterProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractParameterFormatter implements IParameterProvider {
    /**
     * Output text format settings
     */
    protected OutputFormatSettings formatSettings = new OutputFormatSettings();
}
