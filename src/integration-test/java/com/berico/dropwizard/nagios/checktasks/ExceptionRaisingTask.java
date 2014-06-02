package com.berico.dropwizard.nagios.checktasks;

import com.bericotech.dropwizard.nagios.MessagePayload;
import com.bericotech.dropwizard.nagios.NagiosCheckTask;
import com.google.common.collect.ImmutableMultimap;

public class ExceptionRaisingTask extends NagiosCheckTask {

    public static final String TASKNAME = "exception-raising-task";
    public static final String MESSAGE = "Oops";

    public ExceptionRaisingTask() {
        super(TASKNAME);
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        throw new Exception(MESSAGE);
    }
}
