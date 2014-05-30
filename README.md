# Dropwizard-Nagios

Extends Dropwizard's `Task` component to return Nagios-formatted healthchecks.

*So, why not just repurpose Dropwizard's HealthCheck?*

The Dropwizard (Yammer/Codahale) `HealthCheck` facility does not provide the granularity we desire.  We really like (nay prefer) the Nagios reporting standard which not only includes four valid service states (OK, WARN, CRITICAL, UNKNOWN), but also allows the output of **Perf Data** (performance data), which are arbitrary metrics specific to a health check.  While we get some of the same capabilities in different ways with Dropwizard's Metrics, we really want the ability to have this all integrated into one solution.

### Credits

Some of the code is borrowed from the JSENDNSCA project written by Raj.Patel (https://code.google.com/p/jsendnsca/).


### License

This project is licensed under the Apache Software Foundation's License 2.0.

> The `MessagePayload`, `MessagePayloadBuilder`, `Level` have been partially rewritten from the JSendNSCA library, which is also licensed Apache 2.0.
