package com.chaika.batch.configuration.flow;

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
public class FlowJobFirstConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public FlowJobFirstConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step flowJobFirstStep1() {
        return stepBuilderFactory
                .get("flowJobFirstStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> flowJobFirst Step 1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job flowJobFirst(@Qualifier("exampleFlow") Flow flow) {
        return jobBuilderFactory
                .get("flowJobFirst")
                .start(flow)
                .next(flowJobFirstStep1())
                .end()
                .build();
    }
}
