package com.berico.dropwizard.nagios.checktasks;


import com.bericotech.dropwizard.nagios.Level;
import com.bericotech.dropwizard.nagios.MessagePayload;
import com.bericotech.dropwizard.nagios.MessagePayloadBuilder;
import com.bericotech.dropwizard.nagios.NagiosCheckTask;
import com.google.common.collect.ImmutableMultimap;

public class ParameterizedTask extends NagiosCheckTask {

    public static final String TASKNAME = "parameterized-task";
    public static final String MESSAGE_TEMPLATE = "success w/ p1=%s and p2=%s";

    public ParameterizedTask() {
        super(TASKNAME);
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        String p1 = getMandatoryParameter(requestParameters, "p1");
        String p2 = getMandatoryParameter(requestParameters, "p2");

        String message = String.format(MESSAGE_TEMPLATE, p1, p2);

        return new MessagePayloadBuilder().withLevel(Level.OK).withMessage(message).build();
    }
}