package com.bericotech.dropwizard.nagios;

import com.google.common.collect.ImmutableMultimap;

import java.util.Random;

/**
 * Demonstrates how to build a NagiosCheckTask.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public class ExampleNagiosCheckTask extends NagiosCheckTask {

    public static final int WARNING_TEMPERATURE = 90;

    public static final int CRITICAL_TEMPERATURE = 100;

    Random random = new Random();

    public ExampleNagiosCheckTask() {

        super("temperature-check");
    }


    private int getCurrentTemperature(){

        return random.nextInt(132);
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        int currentTemperature = getCurrentTemperature();

        Level level = Level.evaluate(currentTemperature, WARNING_TEMPERATURE, CRITICAL_TEMPERATURE);

        String message;

        switch (level){
            case OK: message = "Temperature is nice."; break;
            case WARNING: message = "Temperature is a little warm."; break;
            case CRITICAL: message = "Temperature is hot!"; break;
            default: message = "Something went wrong with my thermometer."; break;
        }

        return MessagePayload
                .builder()
                .withLevel(level)
                .withMessage(message)
                .withPerfData(
                    PerfDatum.builder("temp", currentTemperature)
                    .criteria(WARNING_TEMPERATURE, CRITICAL_TEMPERATURE)
                    .build()
                )
                .build();
    }
}
