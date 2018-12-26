package com.chaika.batch.configuration.flow;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class FlowConfiguration {

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public FlowConfiguration(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step flowStep1() {
        return stepBuilderFactory
                .get("flowStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> flow Step 1 from inside exampleFlow");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step flowStep2() {
        return stepBuilderFactory
                .get("flowStep2")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> flow Step 2 from inside exampleFlow");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Flow exampleFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("exampleFlow");

        flowBuilder
                .start(flowStep1())
                .next(flowStep2())
                .end();

        return flowBuilder.build();
    }
}
