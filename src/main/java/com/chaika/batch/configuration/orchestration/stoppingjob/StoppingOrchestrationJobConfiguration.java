package com.chaika.batch.configuration.orchestration.stoppingjob;

import org.springframework.batch.core.Job;
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
 * Created by echaika on 22.01.2019
 */
@Configuration
public class StoppingOrchestrationJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public StoppingOrchestrationJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public Tasklet stoppingOrchestrationJobTasklet(@Value("#{jobParameters['name']}") String name) {
        return (contribution, chunkContext) -> {
            System.out.println(String.format(">> %s is sleeping again...", name));
            Thread.sleep(1000);
            return RepeatStatus.CONTINUABLE;
        };
    }

    @Bean
    public Job stoppingOrchestrationJob() {
        return jobBuilderFactory.get("stoppingOrchestrationJob")
                .start(stepBuilderFactory.get("stoppingOrchestrationJobStep1")
                        .tasklet(stoppingOrchestrationJobTasklet(null))
                        .build())
                .build();
    }
}
