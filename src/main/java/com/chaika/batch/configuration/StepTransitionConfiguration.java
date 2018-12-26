package com.chaika.batch.configuration;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class StepTransitionConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public StepTransitionConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step transitionStep1() {
        return stepBuilderFactory
                .get("transitionStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(">> transitionJobSimpleNext Step 1");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step transitionStep2() {
        return stepBuilderFactory
                .get("transitionStep2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> transitionJobSimpleNext Step 2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step transitionStep3() {
        return stepBuilderFactory
                .get("transitionStep3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> transitionJobSimpleNext Step 3");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job transitionJobSimpleNext() {
        return jobBuilderFactory
                .get("transitionJobSimpleNext")
                .start(transitionStep1()).on(String.valueOf(BatchStatus.COMPLETED)).to(transitionStep2())
                .from(transitionStep2()).on(String.valueOf(BatchStatus.COMPLETED)).to(transitionStep3())
                .from(transitionStep3()).end()
                .build();
    }
}
