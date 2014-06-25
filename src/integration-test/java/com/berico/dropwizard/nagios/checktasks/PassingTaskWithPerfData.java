package com.berico.dropwizard.nagios.checktasks;

import com.bericotech.dropwizard.nagios.*;
import com.google.common.collect.ImmutableMultimap;

public class PassingTaskWithPerfData extends NagiosCheckTask {

    public static final String TASKNAME = "passing-task-with-perf";
    public static final String MESSAGE = "success | ttl=100 | ttl2=100s;110;120;54;108";

    public PassingTaskWithPerfData() {
        super(TASKNAME);
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        return new MessagePayloadBuilder()
                .withLevel(Level.OK)
                .withMessage("success")
                .withPerfData(
                        PerfDatum.builder("ttl", 100).build(),
                        PerfDatum.builder("ttl2", 100)
                                .uom(PerfDatum.UOM.Seconds)
                                .criteria(110, 120)
                                .minMax(54, 108)
                                .build()
                )
                .build();
    }
}