package com.chaika.batch.configuration.flow.parameters;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 27.12.2018
 */
@Configuration
public class ParametersJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ParametersJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public Tasklet messageTasklet(@Value("#{jobParameters['message']}") final String message) {
        return (contribution, chunkContext) -> {
            System.out.println(message);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step parametersStep() {
        return stepBuilderFactory.get("parametersStep")
                .tasklet(messageTasklet(null))
                .build();
    }

    @Bean
    public Job parametersJob() {
        return jobBuilderFactory.get("parametersJob")
                .start(parametersStep())
                .build();
    }
}
