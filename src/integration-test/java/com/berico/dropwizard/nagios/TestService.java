package com.berico.dropwizard.nagios;


import com.bericotech.dropwizard.nagios.HealthCheckWrapper;
import com.bericotech.dropwizard.nagios.NagiosCheckTask;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;
import com.yammer.metrics.core.HealthCheck;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

public class TestService extends Service<TestService.TestConfiguration> {


    @Override
    public void initialize(Bootstrap<TestConfiguration> testConfigurationBootstrap) {}

    @Override
    public void run(TestConfiguration configuration, Environment environment) throws Exception {

        for (NagiosCheckTask checkTask : configuration.getCheckTasks()){

            environment.addTask(checkTask);
        }

        for (HealthCheck healthCheck : configuration.getWrappedCheckTasks()){

            environment.addHealthCheck(healthCheck);
        }

        environment.addResource(FakeResource.class);
    }

    public static void main(String[] args) throws Exception {

        new TestService().run(new String[]{"server", Resources.getResource("server.yml").getPath()});
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

        public List<HealthCheck> getWrappedCheckTasks(){

            List<HealthCheck> tasks = Lists.newArrayList();

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
