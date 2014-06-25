package com.berico.dropwizard.nagios.checktasks;

import com.bericotech.dropwizard.nagios.Level;
import com.bericotech.dropwizard.nagios.MessagePayload;
import com.bericotech.dropwizard.nagios.MessagePayloadBuilder;
import com.bericotech.dropwizard.nagios.NagiosCheckTask;
import com.google.common.collect.ImmutableMultimap;

public class FailingCriticalTask extends NagiosCheckTask {

    public static final String TASKNAME = "critical-task";
    public static final String MESSAGE = "critical";

    public FailingCriticalTask() {
        super(TASKNAME);
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        return new MessagePayloadBuilder().withLevel(Level.CRITICAL).withMessage(MESSAGE).build();
    }
}