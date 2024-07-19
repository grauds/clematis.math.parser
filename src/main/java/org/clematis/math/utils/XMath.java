// Created: Mar 25, 2003 T 4:00:23 PM
package org.clematis.math.utils;

/**
 * Extensions to <code>java.lang.Math</code> class
 */
public class XMath {
    /**
     * Secant function
     *
     * @param radians argument in radians
     * @return result
     */
    public static double sec(double radians) {
        return 1.0 / Math.cos(radians);
    }

    /**
     * Cosecant function
     *
     * @param radians argument in radians
     * @return result
     */
    public static double csc(double radians) {
        return 1.0 / Math.sin(radians);
    }

    /**
     * Cotangent function
     *
     * @param radians argument in radians
     * @return result
     */
    public static double ctn(double radians) {
        return 1.0 / Math.tan(radians);
    }

    /**
     * Arcsine function, returning result in degrees
     *
     * @param sine argument
     * @return result in degrees
     */
    public static double arcsine(double sine) {
        return Math.asin(sine) * 180.0 / Math.PI;
    }

    /**
     * Arccosine function
     *
     * @param cosine argument in radians
     * @return result in degrees
     */
    public static double arccosine(double cosine) {
        return Math.acos(cosine) * 180.0 / Math.PI;
    }

    /**
     * Arctangent function
     *
     * @param tangent argument in radians
     * @return result in degrees
     */
    public static double arctangent(double tangent) {
        return Math.atan(tangent) * 180.0 / Math.PI;
    }

    /**
     * Logarithm with logarithmic base of 10
     *
     * @param x argument
     * @return result
     */
    public static double log10(double x) {
        return Math.log(x) / Math.log(10.0);
    }

    /**
     * Logarithm with logarithmic base of 2
     *
     * @param x argument
     * @return result
     */
    public static double log2(double x) {
        return Math.log(x) / Math.log(2.0);
    }
}
