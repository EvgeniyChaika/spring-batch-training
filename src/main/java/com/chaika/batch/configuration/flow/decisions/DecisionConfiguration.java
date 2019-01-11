package com.chaika.batch.configuration.flow.decisions;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class DecisionConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DecisionConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Step startStep() {
        return stepBuilderFactory.get("startStep")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> decisionJob startStep");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step evenStep() {
        return stepBuilderFactory.get("evenStep")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> decisionJob evenStep");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step oddStep() {
        return stepBuilderFactory.get("oddStep")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> decisionJob oddStep");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public JobExecutionDecider decider() {
        return new OddDecider();
    }

    @Bean
    public Job decisionJob() {
        return jobBuilderFactory.get("decisionJob")
                .start(startStep())
                .next(decider())
                .from(decider()).on(String.valueOf(Decision.ODD)).to(oddStep())
                .from(decider()).on(String.valueOf(Decision.EVEN)).to(evenStep())
                .from(oddStep()).on("*").to(decider())
                .end()
                .build();
    }

    public static class OddDecider implements JobExecutionDecider {

        private int count = 0;

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            count++;

            if (count % 2 == 0) {
                return new FlowExecutionStatus(String.valueOf(Decision.EVEN));
            } else {
                return new FlowExecutionStatus(String.valueOf(Decision.ODD));
            }
        }
    }
}
