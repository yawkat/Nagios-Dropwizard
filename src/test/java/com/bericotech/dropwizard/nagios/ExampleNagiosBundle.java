package com.bericotech.dropwizard.nagios;

import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

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

        environment.admin().addTask(new ExampleNagiosCheckTask());
    }
}
