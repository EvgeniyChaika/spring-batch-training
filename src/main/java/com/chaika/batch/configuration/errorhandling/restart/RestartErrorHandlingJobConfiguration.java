package com.chaika.batch.configuration.errorhandling.restart;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by echaika on 14.01.2019
 */
@Configuration
public class RestartErrorHandlingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public RestartErrorHandlingJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public Tasklet restartErrorHandlingJobTasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                Map<String, Object> stepExecutionContext = chunkContext.getStepContext().getStepExecutionContext();
                if (stepExecutionContext.containsKey("run")) {
                    System.out.println("This time we'll let it go.");
                    return RepeatStatus.FINISHED;
                } else {
                    System.out.println("I don't think so...");
                    chunkContext.getStepContext().getStepExecution().getExecutionContext().put("run", true);
                    throw new RuntimeException("Not this time. ..");
                }
            }
        };
    }

    @Bean
    public Step restartErrorHandlingJobStep1() {
        return stepBuilderFactory.get("restartErrorHandlingJobStep1")
                .tasklet(restartErrorHandlingJobTasklet())
                .build();
    }

    @Bean
    public Step restartErrorHandlingJobStep2() {
        return stepBuilderFactory.get("restartErrorHandlingJobStep2")
                .tasklet(restartErrorHandlingJobTasklet())
                .build();
    }

    @Bean
    public Job restartErrorHandlingJob() {
        return jobBuilderFactory.get("restartErrorHandlingJob")
                .start(restartErrorHandlingJobStep1())
                .next(restartErrorHandlingJobStep2())
                .build();
    }
}
