package com.chaika.batch.configuration.flow.nestedjobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class ParentJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final Job childJob;

    private final JobLauncher jobLauncher;

    @Autowired
    public ParentJobConfiguration(JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  Job childJob,
                                  JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.childJob = childJob;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    public Step parentJobStep1() {
        return stepBuilderFactory.get("parentJobStep1")
                .tasklet((contribution, chunkContext) -> {
                    System.out.println(">> parentJob Step 1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Step childJobStep = new JobStepBuilder(new StepBuilder("childJobStep"))
                .job(childJob)
                .launcher(jobLauncher)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .build();

        return jobBuilderFactory.get("parentJob")
                .start(parentJobStep1())
                .next(childJobStep)
                .build();
    }
}
