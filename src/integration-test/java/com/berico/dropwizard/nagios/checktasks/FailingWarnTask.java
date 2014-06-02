package com.berico.dropwizard.nagios.checktasks;

import com.bericotech.dropwizard.nagios.Level;
import com.bericotech.dropwizard.nagios.MessagePayload;
import com.bericotech.dropwizard.nagios.MessagePayloadBuilder;
import com.bericotech.dropwizard.nagios.NagiosCheckTask;
import com.google.common.collect.ImmutableMultimap;

public class FailingWarnTask extends NagiosCheckTask {

    public static final String TASKNAME = "warning-task";
    public static final String MESSAGE = "warning";

    public FailingWarnTask() {
        super(TASKNAME);
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        return new MessagePayloadBuilder().withLevel(Level.WARNING).withMessage(MESSAGE).create();
    }
}
