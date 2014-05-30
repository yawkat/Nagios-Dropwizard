package com.bericotech.dropwizard.nagios;

import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

/**
 * Demonstrates how you might add Nagios HealthChecks
 * into your Dropwizard Environment.
 *
 * @author Richard Clayton (Berico Technologies)
 */
public class ExampleNagiosBundle implements Bundle {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {}

    @Override
    public void run(Environment environment) {

        environment.addTask(new ExampleNagiosCheckTask());
    }
}
