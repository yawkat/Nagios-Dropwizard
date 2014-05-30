package com.bericotech.dropwizard.nagios;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.yammer.dropwizard.tasks.Task;

import java.io.PrintWriter;

/**
 * Represents a HealthCheck that will be periodically called via HTTP by Nagios/Icinga.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public abstract class NagiosCheckTask extends Task {

    /**
     * If an error occurs during the execution of a task, this is the assumed
     * outcome of the check (by default, it's assumed to be an error, and therefore
     * a CRITICAL outcome).
     */
    public static Level DEFAULT_LEVEL_FOR_TASK_ERROR = Level.CRITICAL;

    /**
     * Instantiate with the desired name for the task.
     * @param name Name of the task.
     */
    public NagiosCheckTask(String name) { super(name); }

    /**
     * Given the HTTP request parameters, execute your health check.  Implementations are expected to
     * return a Nagios MessagePayload, which represents the response Nagios requires of checks.
     *
     * @param requestParameters Query or Form parameters submitted to the HTTP servlet.
     * @return the result of the check.
     * @throws Throwable any error that may occur during the check.
     */
    public abstract MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable;

    /**
     * This is called by the Dropwizard runtime; basically, it executes the performCheck method, catching any exceptions
     * that may arise, and printing the results to the output buffer.
     * @param requestParameters Request parameters.
     * @param pw Output buffer.
     * @throws Exception Should not be thrown unless the Dropwizard supplied PrintWriter errors.
     */
    @Override
    public void execute(
            ImmutableMultimap<String, String> requestParameters, PrintWriter pw) throws Exception {

        MessagePayload mp = null;

        try {

            mp = performCheck(requestParameters);

        } catch (Throwable t){

            mp = new MessagePayloadBuilder()
                    .withLevel(DEFAULT_LEVEL_FOR_TASK_ERROR).withMessage(t.getMessage()).create();
        }
        finally {

            pw.println(mp.getLevel() + " - " + mp.getMessage());
        }
    }

    /**
     * Helper function to extract a single-valued parameter value by key name from the
     * request context.
     * @param requestParameters Request parameters.
     * @param paramName Name of the parameter to get.
     * @return An optional parameter.
     */
    public static Optional<String> getParameter(
            ImmutableMultimap<String, String> requestParameters, String paramName) {

        if (requestParameters.containsKey(paramName)){

            return Optional.of(requestParameters.get(paramName).asList().get(0));
        }

        return Optional.absent();
    }

    /**
     * Return a parameter or throw an exception.
     * @param requestParameters Request parameters.
     * @param paramName Name of the parameter to get.
     * @return The value of the parameter that must exist.
     * @throws UnsatisfiedParameterException thrown if the parameter does not exist.
     */
    public static String getMandatoryParameter(
            ImmutableMultimap<String, String> requestParameters, String paramName)
                throws UnsatisfiedParameterException {

        Optional<String> parameter = getParameter(requestParameters, paramName);

        if (parameter.isPresent()) return parameter.get();

        throw new UnsatisfiedParameterException(paramName);
    }
}
