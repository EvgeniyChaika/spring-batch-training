package com.chaika.batch.configuration.flow;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class FlowJobLastConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public FlowJobLastConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step flowJobLastStep1() {
        return stepBuilderFactory
                .get("flowJobLastStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> flowJobLast Step 1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job flowJobLast(@Qualifier("exampleFlow") Flow flow) {
        return jobBuilderFactory
                .get("flowJobLast")
                .start(flowJobLastStep1())
                .on(String.valueOf(BatchStatus.COMPLETED)).to(flow)
                .end()
                .build();
    }
}
