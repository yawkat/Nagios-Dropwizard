package com.berico.dropwizard.nagios;

import com.google.common.io.Resources;

public class TestServiceThread extends Thread {

    TestService testService;

    String ymlConfig;

    volatile boolean isLaunched = false;

    public TestServiceThread(String ymlConfig){

        this.ymlConfig = ymlConfig;

        this.testService = new TestService();
    }

    @Override
    public void run() {

        try {

            testService.run(new String[]{ "server", Resources.getResource(ymlConfig).getPath() });

            isLaunched = true;

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    public boolean isLaunched(){

        return this.isLaunched;
    }
}
