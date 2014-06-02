package com.bericotech.dropwizard.nagios;

import com.google.common.collect.ImmutableMultimap;
import com.yammer.metrics.core.HealthCheck;

public class HealthCheckWrapper extends HealthCheck {

    NagiosCheckTask checkTask;
    ImmutableMultimap<String, String> params = ImmutableMultimap.of();


    public HealthCheckWrapper(NagiosCheckTask checkTask) {

        super(checkTask.getName());

        this.checkTask = checkTask;
    }

    public HealthCheckWrapper(NagiosCheckTask checkTask, ImmutableMultimap<String, String> params) {

        super(checkTask.getName());

        this.checkTask = checkTask;
        this.params = params;
    }

    @Override
    protected Result check() throws Exception {

        try {

            MessagePayload mp = checkTask.performCheck(params);

            boolean isOk = mp.getLevel() == Level.OK;

            if (isOk){

                return Result.healthy(mp.getMessage());
            }
            else {

                return Result.unhealthy(mp.getMessage());
            }

        } catch (Throwable throwable) {

            return Result.unhealthy(throwable);
        }
    }
}
