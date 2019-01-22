package com.chaika.batch.configuration.orchestration.schedulingjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by echaika on 22.01.2019
 */
@Configuration
public class SchedulingOrchestrationJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public SchedulingOrchestrationJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public Tasklet schedulingOrchestrationJobTasklet() {
        return (contribution, chunkContext) -> {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

            System.out.println(
                    String.format(">> I was run at %s",
                            formatter.format(new Date())));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job schedulingOrchestrationJob() {
        return jobBuilderFactory.get("schedulingOrchestrationJob")
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderFactory.get("schedulingOrchestrationJobStep1")
                        .tasklet(schedulingOrchestrationJobTasklet())
                        .build())
                .build();
    }
}
