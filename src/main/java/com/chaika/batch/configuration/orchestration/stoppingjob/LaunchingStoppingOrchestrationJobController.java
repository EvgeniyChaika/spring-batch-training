package com.chaika.batch.configuration.orchestration.stoppingjob;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Created by echaika on 22.01.2019
 */
@RestController
public class LaunchingStoppingOrchestrationJobController {

    private final JobOperator jobOperator;

    @Autowired
    public LaunchingStoppingOrchestrationJobController(JobOperator jobOperator) {
        this.jobOperator = jobOperator;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public long launchStoppingOrchestrationJob(@RequestParam("name") String name) throws Exception {
        return jobOperator.start("stoppingOrchestrationJob", String.format("name=%s, date=%s", name, new Date()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void stopStoppingOrchestrationJob(@PathVariable("id") Long id) throws Exception {
        jobOperator.stop(id);
    }
}
