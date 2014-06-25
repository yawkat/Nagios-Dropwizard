package com.bericotech.dropwizard.nagios;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Higher-level implementation of the NagiosCheckTask that will record
 * the 'tte' (time-to-execute) of the wrapped task and provide the 'tte'
 * as PerfDatum.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public abstract class NagiosTimedCheckTask extends NagiosCheckTask {

    /**
     * Provide the name of the task.
     * @param name Name of the task.
     */
    public NagiosTimedCheckTask(String name) {
        super(name);
    }

    /**
     * Get the criteria considered "critical" (unacceptible) for
     * the task's performance.
     * @return Critical criteria in nanoseconds.
     */
    public abstract long getCriticalThresholdNs();

    /**
     * Get the criteria considered "warning" (suboptimal) for
     * the task's performance.
     * @return Warning criteria in nanoseconds.
     */
    public abstract long getWarningThresholdInNs();

    /**
     * Execute the task that should be timed.
     * @param requestParameters Request parameters.
     * @return Optionally, any extra performance data you would like to provide.
     * @throws Throwable Thrown if an error occurred during the execution of the health check.
     */
    public abstract Optional<Collection<PerfDatum>>
        doTimedCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable;

    /**
     * Executes the timed check.
     * @param requestParameters Query or Form parameters submitted to the HTTP servlet.
     * @return MessagePayload representing the timed check.
     * @throws Throwable Thrown if an error occurred during the execution of the health check.
     */
    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        long start = System.nanoTime();

        Optional<Collection<PerfDatum>> extraPerfData = doTimedCheck(requestParameters);

        long total = System.nanoTime() - start;

        double timeInSeconds = Conversion.nsToS(total);

        ArrayList<PerfDatum> perfData = Lists.newArrayList();

        perfData.add(
                PerfDatum
                    .builder("tte", timeInSeconds)
                        .criteria(
                            Conversion.nsToS(getWarningThresholdInNs()),
                            Conversion.nsToS(getCriticalThresholdNs()))
                        .build());


        if (extraPerfData.isPresent()){

            perfData.addAll(extraPerfData.get());
        }

        return new MessagePayloadBuilder()
                .withLevel(Level.evaluate(total, getWarningThresholdInNs(), getCriticalThresholdNs()))
                .withMessage(String.format("%s took %ss;", this.getName(), Conversion.nsToS(total)))
                .withPerfData(perfData)
                .build();
    }

    /**
     * Helper for constructing the Optional Collection of PerfData.
     * @param perfData Variable set of performance data.
     * @return Collection of PerfData wrapped in an Optional.
     */
    public static Optional<Collection<PerfDatum>> withPerfData(PerfDatum... perfData){

        Collection<PerfDatum> perfDataList = Arrays.asList(perfData);

        return Optional.of(perfDataList);
    }

    /**
     * Helper for constructing an absent collection of PerfData.
     * @return Absent collection of PerfData.
     */
    public static Optional<Collection<PerfDatum>> noPerfData(){

        return Optional.absent();
    }
}
