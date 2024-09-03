// Created: Feb 15, 2003 T 4:54:17 PM
package org.clematis.math.v1;

import lombok.Getter;

/**
 * <code>AlgorithmException</code> is thrown when something wrong
 * when algorithm is processed.
 */
@Getter
public class AlgorithmException extends Exception {
    private int line = 0;

    /**
     * Constructor.
     *
     * @param msg the description of the exception reason.
     */
    public AlgorithmException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg the description of the exception reason.
     */
    public AlgorithmException(String msg, int line) {
        super(msg);
        this.line = line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String toString() {
        return super.toString() + " in line " + line;
    }
}
