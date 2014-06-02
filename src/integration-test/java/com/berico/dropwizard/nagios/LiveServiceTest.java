package com.berico.dropwizard.nagios;

import com.berico.dropwizard.nagios.checktasks.*;
import com.bericotech.dropwizard.nagios.Level;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class LiveServiceTest {

    private String PLUGINDIR = System.getProperty("ls.test.pdir", "plugin");
    private String CHECKSCRIPT = System.getProperty("ls.test.checkscript", "check_dropwizard_task.py");

    private String HOSTNAME = System.getProperty("ls.test.hostname", "localhost");
    private int    PORT     = Integer.parseInt(System.getProperty("ls.test.port", "11112"));
    private String USERNAME = System.getProperty("ls.test.username", "admin");
    private String PASSWORD = System.getProperty("ls.test.password", "admin");

    // This is an thread we will launch the Dropwizard service within.
    private static TestServiceThread serverRootThread = null;

    @BeforeClass
    public static void setup() throws InterruptedException {

        // Instantiate our thread.
        serverRootThread = new TestServiceThread("server.yml");

        // Start it.
        serverRootThread.start();

        // Wait until the server launches -- literally finishes it's tasks
        // and breaks out of the Service.run() block.
        while (!serverRootThread.isLaunched()){}
    }

    @AfterClass
    public static void teardown(){

        // Yes, this is generally considered bad.  But that's ok.  We don't care about any of the
        // resources associated with this thread.
        serverRootThread.stop();
    }

    @Test
    public void check_task_where_task_is_expected_to_be_OK() throws Throwable {

        assertStatus(PassingTask.TASKNAME, Level.OK, PassingTask.MESSAGE);
    }

    @Test
    public void check_task_where_task_is_expected_to_be_WARNING() throws Throwable {

        assertStatus(FailingWarnTask.TASKNAME, Level.WARNING, FailingWarnTask.MESSAGE);
    }

    @Test
    public void check_task_where_task_is_expected_to_be_CRITICAL() throws Throwable {

        assertStatus(FailingCriticalTask.TASKNAME, Level.CRITICAL, FailingCriticalTask.MESSAGE);
    }

    @Test
    public void check_task_where_task_is_expected_to_be_UNKNOWN() throws Throwable {

        assertStatus(UnknownTask.TASKNAME, Level.UNKNOWN, UnknownTask.MESSAGE);
    }

    @Test
    public void check_task_where_task_is_expected_to_be_OK_with_PerfData() throws Throwable {

        assertStatus(PassingTaskWithPerfData.TASKNAME, Level.OK, PassingTaskWithPerfData.MESSAGE);
    }

    @Test
    public void check_task_where_task_is_expected_to_be_OK_with_parameterized_data() throws Throwable {

        String v1 = "true";
        String v2 = "42";

        Map<String, String> params = Maps.newHashMap();

        params.put("p1", v1);
        params.put("p2", v2);

        assertStatus(
                ParameterizedTask.TASKNAME,
                Level.OK,
                String.format(ParameterizedTask.MESSAGE_TEMPLATE, v1, v2),
                params);
    }

    @Test
    public void check_task_where_task_throws_an_exception() throws Throwable {

        assertStatus(ExceptionRaisingTask.TASKNAME, Level.CRITICAL, ExceptionRaisingTask.MESSAGE);
    }

    @Test
    public void checking_non_existent_task_returns_unknown() throws Throwable {

        assertStatus("Nonexistent", Level.UNKNOWN, "Error making request; status code: 404");
    }

    protected void assertStatus(String taskName, Level expectedLevel, String expectedMessage) throws Throwable {

        assertStatus(taskName, expectedLevel, expectedMessage, null);
    }

    protected void assertStatus(
            String taskName, Level expectedLevel, String expectedMessage, Map<String, String> params) throws Throwable {

        ProcessResult pr = runNagiosCheckTask(taskName, (params != null)? params : new HashMap<String, String>());

        assertEquals(pr.exitcode, expectedLevel.ordinal());

        String[] parts = pr.getStdOut().split("-", 2);

        assertEquals(2, parts.length);

        String status = parts[0].trim();

        assertEquals(expectedLevel, Level.tolevel(status));

        assertEquals(expectedMessage.trim(), parts[1].trim());
    }

    protected ProcessResult runNagiosCheckTask(String task, Map<String, String> params)
            throws IOException, InterruptedException {

        Path pdir = Paths.get(PLUGINDIR);

        Preconditions.checkState(Files.exists(pdir));

        String paramString = Joiner.on("&").withKeyValueSeparator("=").join(params);

        ProcessBuilder pb = new ProcessBuilder()
                .directory(pdir.toFile())
                .command(
                        "python",
                        CHECKSCRIPT,
                        USERNAME,
                        PASSWORD,
                        HOSTNAME,
                        Integer.toString(PORT),
                        task,
                        paramString);

        Process p = pb.start();

        p.waitFor();

        String stdout = fromInputStream(p.getInputStream());

        String stderr = fromInputStream(p.getErrorStream());

        return new ProcessResult(p.exitValue(), stdout, stderr);
    }

    protected static class ProcessResult {

        private String stdout;
        private String stderr;
        private int    exitcode;

        public ProcessResult(int exitcode, String stdout, String stderr) {

            this.exitcode = exitcode;
            this.stdout = stdout;
            this.stderr = stderr;
        }

        public String getStdOut() {
            return stdout;
        }

        public String getStdErr(){
            return stderr;
        }

        public int getExitCode() {
            return exitcode;
        }
    }

    protected static String fromInputStream(InputStream is) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

        return CharStreams.toString(br);
    }

}
