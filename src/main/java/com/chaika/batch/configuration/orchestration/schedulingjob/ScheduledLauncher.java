package com.chaika.batch.configuration.orchestration.schedulingjob;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by echaika on 22.01.2019
 */
@Component
public class ScheduledLauncher {

    private final JobOperator jobOperator;

    @Autowired
    public ScheduledLauncher(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @Scheduled(fixedDelay = 5000L)
    public void runSchedulingOrchestrationJob() throws Exception {
        jobOperator.startNextInstance("schedulingOrchestrationJob");
    }
}
