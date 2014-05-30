package com.bericotech.dropwizard.nagios;

import com.google.common.base.Preconditions;

/**
 * Represents a metric recorded during a Nagios check.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public class PerfDatum {

    /**
     * Unit of Measurement (UOM).
     */
    public enum UOM {
        /**
         * No standard unit applies.
         */
        Unspecified (""),
        Seconds ("s"),
        Percentage ("%"),
        Bytes ("B"),
        Kilobytes ("KB"),
        Megabytes ("MB"),
        Terabytes ("TB"),
        Counter ("c");

        private final String symbol;

        UOM(String symbol){
            this.symbol = symbol;
        }

        public String symbol(){
            return this.symbol;
        }
    }

    /**
     * Oh no!  You better provide parameters!
     */
    private PerfDatum(){}

    /**
     * Initialized with all parameters except a Unit of Measurement (Unspecified).
     *
     * Note:  Instead of providing the permutation of valid overloads for this constructor, please
     * use the builder instead:  PerfDatum.builder("label", 42).build();
     *
     * @param label Name of the metric.
     * @param value Value of the metric.
     * @param unitOfMeasurement  Units of Measurement the value represents.
     * @param warn Warning criteria for the metric.
     * @param critical Critical criteria for the metric.
     * @param min Minimum observed value before/during the check.
     * @param max Maximum observed value before/during the check.
     */
    public PerfDatum(String label, Object value, UOM unitOfMeasurement, Object warn, Object critical, Object min, Object max) {
        this.label = label;
        this.value = value;
        this.unitOfMeasurement = unitOfMeasurement;
        this.warn = warn;
        this.critical = critical;
        this.min = min;
        this.max = max;
    }

    private String label;

    private Object value;

    private Object warn;

    private Object critical;

    private Object min;

    private Object max;

    private UOM unitOfMeasurement = UOM.Unspecified;

    /**
     * Get the name of the metric.
     * @return name of the metric.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Get the value of the metric.
     * @return value of the metric.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Warning criteria for the metric.
     * @return warning criteria value.
     */
    public Object getWarn() {
        return warn;
    }

    /**
     * Critical criteria for the metric.
     * @return critical criteria value.
     */
    public Object getCritical() {
        return critical;
    }

    /**
     * Minimum observed value before/during the check.
     * @return min observed value.
     */
    public Object getMin() {
        return min;
    }

    /**
     * Maximum observed value before/during the check.
     * @return max observed value.
     */
    public Object getMax() {
        return max;
    }

    /**
     * Units of Measurement the value represents.
     * @return Unit of Measurement.
     */
    public UOM getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    /**
     * Get a builder for the PerfDatum object, supplying the two mandatory parameters for
     * PerfDatum (label and value).
     * @param label Name of the metric.
     * @param value Value of the metric.
     * @return Builder.
     */
    public static PerfDatumBuilder builder(String label, Object value){

        return new PerfDatumBuilder(label, value);
    }

    /**
     * Prints a properly formatted Nagios PerfDatum string.
     * @return Formatted PerfDatum string.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(label).append("=").append(value).append(unitOfMeasurement.symbol());

        if (warn != null && critical != null && min != null && max != null) {
            sb.append(";").append(warn).append(";").append(critical).append(";").append(min).append(";").append(max);
        }
        else if (warn != null && critical != null && min == null && max == null){
            sb.append(";").append(warn).append(";").append(critical);
        }

        return sb.toString();
    }

    /**
     * Helper for the ridiculous amount of fields on PerfDatum.
     *
     * @author Richard Clayton (Berico Technologies)
     */
    public static class PerfDatumBuilder {

        PerfDatum pd = new PerfDatum();

        /**
         * Initialize the builder with the only two mandatory parameters.
         * @param label Name of the metric.
         * @param value Value of the metric.
         */
        public PerfDatumBuilder(String label, Object value){

            Preconditions.checkNotNull(label);
            Preconditions.checkNotNull(value);

            pd.label = label;
            pd.value = value;
        }

        /**
         * Unit of Measurement
         * @param uom Unit of Measurement.
         * @return this.
         */
        public PerfDatumBuilder uom(UOM uom){

            pd.unitOfMeasurement = uom;

            return this;
        }

        /**
         * Warning and Critical criteria for the metric.
         * @param warnValue Warning criteria.
         * @return this.
         */
        public PerfDatumBuilder criteria(Object warnValue, Object criticalValue){

            pd.warn = warnValue;

            pd.critical = criticalValue;

            return this;
        }

        /**
         * Minimum and maximum observed value before/during the check.
         * @param minValue min observed value.
         * @return this.
         */
        public PerfDatumBuilder minMax(Object minValue, Object maxValue){

            pd.min = minValue;

            pd.max = maxValue;

            return this;
        }

        /**
         * Return the PerfDatum.
         * @return Built PerfDatum.
         */
        public PerfDatum build(){

            return pd;
        }
    }
}