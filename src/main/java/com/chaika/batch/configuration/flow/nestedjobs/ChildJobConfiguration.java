package com.chaika.batch.configuration.flow.nestedjobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class ChildJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ChildJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step childJobStep1() {
        return stepBuilderFactory.get("childJobStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> childJob Step 1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job childJob() {
        return jobBuilderFactory.get("childJob")
                .start(childJobStep1())
                .build();
    }
}
