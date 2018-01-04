package com.berico.dropwizard.nagios;

import com.bericotech.dropwizard.nagios.HealthCheckWrapper;
import com.bericotech.dropwizard.nagios.NagiosCheckTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.hibernate.validator.constraints.NotEmpty;

public class TestService extends Application<TestService.TestConfiguration> {

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception {

        for (NagiosCheckTask checkTask : configuration.getCheckTasks()){

            environment.admin().addTask(checkTask);
        }

        for (HealthCheckWrapper healthCheck : configuration.getWrappedCheckTasks()) {

            environment.healthChecks().register(healthCheck.getName(), healthCheck);
        }

        environment.jersey().register(FakeResource.class);
    }

    public static void main(String[] args) throws Exception {

        new TestService().run("server", Resources.getResource("server.yml").getPath());
    }

    /**
     * Instantiate test NagiosCheckTask handlers by specifying the class type.
     */
    public static class TestConfiguration extends Configuration {

        @NotEmpty
        @JsonProperty
        private List<String> checkTasks;

        @NotEmpty
        @JsonProperty
        private List<String> wrappedCheckTasks;

        public List<NagiosCheckTask> getCheckTasks(){

            List<NagiosCheckTask> tasks = Lists.newArrayList();

            for (String clazz : checkTasks){

                try {

                    NagiosCheckTask task = (NagiosCheckTask) Class.forName(clazz).newInstance();

                    tasks.add(task);

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            return tasks;
        }

        public List<HealthCheckWrapper> getWrappedCheckTasks() {

            List<HealthCheckWrapper> tasks = Lists.newArrayList();

            for (String clazz : wrappedCheckTasks){

                try {

                    NagiosCheckTask task = (NagiosCheckTask) Class.forName(clazz).newInstance();

                    tasks.add(new HealthCheckWrapper(task));

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            return tasks;
        }
    }

    // This is a dumb resource to keep Dropwizard from complaining.
    @Path("/blah")
    public static class FakeResource {

        @GET
        @Produces("application/json")
        public String getBlah(){

            return "blah";
        }

    }
}
