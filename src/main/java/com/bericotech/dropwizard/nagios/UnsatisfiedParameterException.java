package com.bericotech.dropwizard.nagios;

/**
 * Thrown if a mandatory parameter is not supplied to NagiosCheckTask.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public class UnsatisfiedParameterException extends Exception {

    private final String parameter;

    /**
     * Instantiate the exception with the name of the missing parameter.
     * @param parameter Name of the missing parameter.
     */
    public UnsatisfiedParameterException(String parameter) {

        super(String.format("Parameter '%s' is required, but not supplied.", parameter));

        this.parameter = parameter;
    }

    /**
     * Get the name of the mandatory parameter.
     * @return Name of the mandatory parameter.
     */
    public String getParameter() {
        return parameter;
    }
}
