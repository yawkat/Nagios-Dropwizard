package com.bericotech.dropwizard.nagios;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Utilities for converting between different units of measurement; useful for PerfDatum.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public class Conversion {

    public static double NS_TO_SECONDS = 0.000000001;
    public static double SECONDS_TO_MS = 1000000000;

    /**
     * Given a duration in nanoseconds, convert it to seconds.
     * @param ns Duration in nanoseconds.
     * @return Duration in seconds.
     */
    public static double nsToS(long ns){

        BigDecimal bd = new BigDecimal(NS_TO_SECONDS * ns);

        return bd.round(new MathContext(3, RoundingMode.CEILING)).doubleValue();
    }

    /**
     * Given a duration in seconds, convert it to nanoseconds.
     * @param s Duration in seconds.
     * @return Duration in nanoseconds.
     */
    public static long sToNs(double s){

        return (long)(SECONDS_TO_MS * s);
    }
}
