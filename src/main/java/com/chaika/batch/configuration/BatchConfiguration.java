package com.chaika.batch.configuration;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

/**
 * Created by echaika on 22.01.2019
 */
@Configuration
public class BatchConfiguration extends DefaultBatchConfigurer implements ApplicationContextAware {

    private final JobExplorer jobExplorer;
    private final JobRepository jobRepository;
    private final JobRegistry jobRegistry;
    private final JobLauncher jobLauncher;
    private ApplicationContext applicationContext;

    @Autowired
    public BatchConfiguration(JobExplorer jobExplorer, JobRepository jobRepository, JobRegistry jobRegistry, JobLauncher jobLauncher) {
        this.jobExplorer = jobExplorer;
        this.jobRepository = jobRepository;
        this.jobRegistry = jobRegistry;
        this.jobLauncher = jobLauncher;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() throws Exception {
        JobRegistryBeanPostProcessor registryBeanPostProcessor = new JobRegistryBeanPostProcessor();

        registryBeanPostProcessor.setJobRegistry(jobRegistry);
        registryBeanPostProcessor.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
        registryBeanPostProcessor.afterPropertiesSet();

        return registryBeanPostProcessor;
    }

    @Bean
    public JobOperator jobOperator() throws Exception {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();

        simpleJobOperator.setJobLauncher(jobLauncher);
        simpleJobOperator.setJobParametersConverter(new DefaultJobParametersConverter());
        simpleJobOperator.setJobRepository(jobRepository);
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobRegistry(jobRegistry);
        simpleJobOperator.afterPropertiesSet();

        return simpleJobOperator;
    }

    @Override
    public JobLauncher getJobLauncher() {
        SimpleJobLauncher jobLauncher = null;
        try {
            jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(jobRepository);
            jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
            jobLauncher.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobLauncher;
    }
}
