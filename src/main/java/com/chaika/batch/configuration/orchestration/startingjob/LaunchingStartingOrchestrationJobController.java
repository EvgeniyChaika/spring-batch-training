package com.chaika.batch.configuration.orchestration.startingjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by echaika on 22.01.2019
 */
@RestController
public class LaunchingStartingOrchestrationJobController {

    private final JobLauncher jobLauncher;

    @Qualifier("startingOrchestrationJob")
    private final Job startingOrchestrationJob;

    private final JobOperator jobOperator;

    @Autowired
    public LaunchingStartingOrchestrationJobController(JobLauncher jobLauncher, Job startingOrchestrationJob, JobOperator jobOperator) {
        this.jobLauncher = jobLauncher;
        this.startingOrchestrationJob = startingOrchestrationJob;
        this.jobOperator = jobOperator;
    }

//    @RequestMapping(value = "/", method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public void launch(@RequestParam("name") String name) throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("name", name)
//                .addDate("date", new Date())
//                .toJobParameters();
//
//        jobLauncher.run(startingOrchestrationJob, jobParameters);
//        jobOperator.start("startingOrchestrationJob", String.format("name=%s, date=%s", name, new Date()));
//    }
}
