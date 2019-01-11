package com.chaika.batch.configuration.flow.transitions;

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
public class TransitionsJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public TransitionsJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step transitionsJobStep1() {
        return stepBuilderFactory
                .get("transitionsJobStep1")
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println(">> transitionsJobSimpleNext Step 1");
                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    public Step transitionsJobStep2() {
        return stepBuilderFactory
                .get("transitionsJobStep2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> transitionsJobSimpleNext Step 2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step transitionsJobStep3() {
        return stepBuilderFactory
                .get("transitionsJobStep3")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> transitionsJobSimpleNext Step 3");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job transitionsJobSimpleNext() {
        return jobBuilderFactory
                .get("transitionsJobSimpleNext")
                .start(transitionsJobStep1()).on(String.valueOf(BatchStatus.COMPLETED)).to(transitionsJobStep2())
                .from(transitionsJobStep2()).on(String.valueOf(BatchStatus.COMPLETED)).to(transitionsJobStep3())
                .from(transitionsJobStep3()).end()
                .build();
    }
}
