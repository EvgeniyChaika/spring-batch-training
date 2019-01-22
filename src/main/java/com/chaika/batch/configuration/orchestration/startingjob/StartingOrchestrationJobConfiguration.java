package com.chaika.batch.configuration.orchestration.startingjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by echaika on 22.01.2019
 */
@Configuration
public class StartingOrchestrationJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final JobExplorer jobExplorer;

    private final JobRepository jobRepository;

    private final JobRegistry jobRegistry;

    private final JobLauncher jobLauncher;

    private final ApplicationContext applicationContext;

    @Autowired
    public StartingOrchestrationJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobExplorer jobExplorer, JobRepository jobRepository, JobRegistry jobRegistry, JobLauncher jobLauncher, ApplicationContext applicationContext) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
        this.jobRegistry = jobRegistry;
        this.jobLauncher = jobLauncher;
        this.applicationContext = applicationContext;
    }

//    @Bean
//    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() throws Exception {
//        JobRegistryBeanPostProcessor registryBeanPostProcessor = new JobRegistryBeanPostProcessor();
//
//        registryBeanPostProcessor.setJobRegistry(jobRegistry);
//        registryBeanPostProcessor.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
//        registryBeanPostProcessor.afterPropertiesSet();
//
//        return registryBeanPostProcessor;
//    }
//
//    @Bean
//    public JobOperator jobOperator() throws Exception {
//        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
//
//        simpleJobOperator.setJobLauncher(jobLauncher);
//        simpleJobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
//        simpleJobOperator.setJobRepository(jobRepository);
//        simpleJobOperator.setJobExplorer(jobExplorer);
//        simpleJobOperator.setJobRegistry(jobRegistry);
//        simpleJobOperator.afterPropertiesSet();
//
//        return simpleJobOperator;
//    }

    @Bean
    @StepScope
    public Tasklet startingOrchestrationJobTasklet(@Value("#{jobParameters['name']}") String name) {
        return (contribution, chunkContext) -> {
            System.out.println(String.format("The job ran for %s", name));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job startingOrchestrationJob() {
        return jobBuilderFactory.get("startingOrchestrationJob")
                .start(stepBuilderFactory.get("startingOrchestrationJobStep1")
                        .tasklet(startingOrchestrationJobTasklet(null))
                        .build())
                .build();
    }
}
