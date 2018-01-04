This is a fork of the original Dropwizard-Nagios project, updated for dropwizard 1.2.2 and deployed to maven central. It is available under the following coordinates:

```
<dependency>
    <groupId>at.yawk.dropwizard-nagios</groupId>
    <artifactId>dropwizard-nagios</artifactId>
    <version>1.0</version>
</dependency>
``` 

---

# Dropwizard-Nagios

Extends Dropwizard's `Task` component to return Nagios-formatted healthchecks.

*So, why not just repurpose Dropwizard's HealthCheck?*

The Dropwizard (Yammer/Codahale) `HealthCheck` facility does not provide the granularity we desire.  We really like (nay prefer) the Nagios reporting standard which not only includes four valid service states (OK, WARN, CRITICAL, UNKNOWN), but also allows the output of **Perf Data** (performance data), which are arbitrary metrics specific to a health check.  While we get some of the same capabilities in different ways with Dropwizard's Metrics, we really want the ability to have this all integrated into one solution.

## Release Notes

**1.1.0** Refactored the Nagios check script to be more idiomatic.  Fixed a couple of warts in the model that was representative of the old JSendNSCA project.

**1.0.1** Fixed a dependency issue that was causing the build to fail.

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
    <version>1.1.0</version>
  </dependency>
</dependencies>
```

Installing the Nagios Check Plugin:

1.  Install the Python dependencies: `pynag` and `requests`:

```
sudo pip install requests
sudo pip install pynag
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

        return MessagePayload
                .builder()
                .withLevel(Level.OK)
                .withMessage("Everything is OK!")
                .withPerfData(
                    PerfDatum.builder("sproketCount", 42).build()
                )
                .build();
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

Add the `check_url.py` to the Nagios/Icinga plugin directory.

To get all of the options to `check_url.py`, execute: `python check_url.py --help`.

A simple example is found in the integration tests: `PassingTask`:

`python check_url.py -u admin -p admin -H localhost -P 11112 -U tasks/passing-task?param1=value`

Which returns:

`OK - success`

Parameters should be added to the relative path parameter `-U`.  These parameters will be made available to the task in as entries in the `ImmutableMultimap<String, String>` passed to all `NagiosCheckTask` instances.

For example, in the integration tests, there is a task called `ParameterizedTask`.  This can be executed against a locally launched Dropwizard instance like so:

`python check_url.py -u admin -p admin -H localhost -P 11112 -U 'tasks/parameterized-task?p1=value1&p2=value2'`

Which returns:

`OK - success w/ p1=value1 and p2=value2`

Please take note of the use of *quotes* for the parameters.  If you do not, the text after the ampersand (&) will be interpreted by BASH as a separate command.

## Contributions and Legal Information

### Credits

Some of the code is borrowed from the JSENDNSCA project written by Raj.Patel (https://code.google.com/p/jsendnsca/).


### License

This project is licensed under the Apache Software Foundation's License 2.0.

> The `MessagePayload`, `MessagePayloadBuilder`, `Level` have been partially rewritten from the JSendNSCA library, which is also licensed Apache 2.0.
