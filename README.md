# Dropwizard-Nagios

Extends Dropwizard's `Task` component to return Nagios-formatted healthchecks.

*So, why not just repurpose Dropwizard's HealthCheck?*

The Dropwizard (Yammer/Codahale) `HealthCheck` facility does not provide the granularity we desire.  We really like (nay prefer) the Nagios reporting standard which not only includes four valid service states (OK, WARN, CRITICAL, UNKNOWN), but also allows the output of **Perf Data** (performance data), which are arbitrary metrics specific to a health check.  While we get some of the same capabilities in different ways with Dropwizard's Metrics, we really want the ability to have this all integrated into one solution.

## Installation

Access via Maven:

```
<repositories>
  <repository>
    <id>nexus.bericotechnologies.com</id>
    <name>Berico Technologies Nexus</name>
    <url>http://nexus.bericotechnologies.com/content/groups/public</url>
    <releases><enabled>true</enabled></releases>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.bericotech</groupId>
    <artifactId>dropwizard-nagios</artifactId>
    <version>1.0.1</version>
  </dependency>
</dependencies>
```

## Usage

### Writing a Nagios HealthCheck in Java

To write a Nagios HealthCheck in Java, simply extend the `com.bericotech.dropwizard.nagios.NagiosCheckTask` class:

```
public class ExampleNagiosCheckTask extends NagiosCheckTask {

    public DemoCheckTask() {

        super("demo-check");
    }

    @Override
    public MessagePayload performCheck(ImmutableMultimap<String, String> requestParameters) throws Throwable {

        return new MessagePayloadBuilder()
                .withLevel(Level.OK)
                .withMessage("Everything is OK!")
                .withPerfData(
                    PerfDatum.builder("sproketCount", 42).build()
                )
                .create();
    }
}
```

There is a more detailed example here: `src/test/java/com/bericotech/dropwizard/nagios/ExampleNagiosCheckTask.java`.

### Adding Nagios HealthChecks to Dropwizard.

`NagiosCheckTask`s are Dropwizard Tasks.  Therefore, they are added the same way.  Simply all `environment.addTask(nagiosCheckTask);` to add it to the Dropwizard runtime.

For example, if we created a custom bundle:

```
public class ExampleNagiosBundle implements Bundle {

    @Override
    public void initialize(Bootstrap<?> bootstrap) {}

    @Override
    public void run(Environment environment) {

        environment.addTask(new ExampleNagiosCheckTask());
    }
}
```

This example can be found here: `src/test/java/com/bericotech/dropwizard/nagios/ExampleNagiosBundle.java`.

### Creating the Check Task in Nagios.

Add the `check_dropwizard_task.py` to the Nagios/Icinga plugin directory.

The `check_dropwizard_task.py` accepts the following command line argument signature:

`check_dropwizard_task.py <username> <password> <hostname> <port> <task> [<params>]`

A simple example is found in the integration tests: `PassingTask`:

`python check_dropwizard_task.py admin admin localhost 11112 passing-task`

Which returns:

`OK - success`

`params` is a query string encoded set of parameters (e.g. `p1=v1&p2=v2`).  These parameters will be made available to the task in as entries in the `ImmutableMultimap<String, String>` passed to all `NagiosCheckTask` instances.

For example, in the integration tests, there is a task called `ParameterizedTask`.  This can be executed against a locally launched Dropwizard instance like so:

`python check_dropwizard_task.py admin admin localhost 11112 parameterized-task "p1=value1&p2=value2"`

Which returns:

`OK - success w/ p1=value1 and p2=value2`

Please take note of the use of *quotes* for the parameters.  If you do not, the text after the ampersand (&) will be interpreted by BASH as a separate command.

## Contributions and Legal Information

### Credits

Some of the code is borrowed from the JSENDNSCA project written by Raj.Patel (https://code.google.com/p/jsendnsca/).


### License

This project is licensed under the Apache Software Foundation's License 2.0.

> The `MessagePayload`, `MessagePayloadBuilder`, `Level` have been partially rewritten from the JSendNSCA library, which is also licensed Apache 2.0.
