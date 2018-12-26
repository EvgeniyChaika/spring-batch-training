package com.chaika.batch.configuration.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 24.12.2018
 */
@Configuration
public class ExampleJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ExampleJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step exampleJobStep1() {
        return stepBuilderFactory
                .get("exampleJobStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> exampleJob Step 1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job exampleJob() {
        return jobBuilderFactory
                .get("exampleJob")
                .start(exampleJobStep1())
                .build();
    }
}
