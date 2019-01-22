package com.chaika.batch.configuration.withintegration.messages.launchingjob;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;

/**
 * Created by echaika on 22.01.2019
 */
@Configuration
public class LaunchingViaMessagesJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;

    @Autowired
    public LaunchingViaMessagesJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, JobLauncher jobLauncher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobLauncher = jobLauncher;
    }

    @Bean
    @ServiceActivator(inputChannel = "requestsLaunchingViaMessagesJob", outputChannel = "repliesLaunchingViaMessagesJob")
    public JobLaunchingMessageHandler jobLaunchingMessageHandler() {
        return new JobLaunchingMessageHandler(jobLauncher);
    }

    @Bean
    public DirectChannel requestsLaunchingViaMessagesJob() {
        return new DirectChannel();
    }

    @Bean
    public DirectChannel repliesLaunchingViaMessagesJob() {
        return new DirectChannel();
    }

    @Bean
    @StepScope
    public Tasklet launchingViaMessagesJobTasklet(@Value("#{jobParameters['name']}") String name) {
        return (contribution, chunkContext) -> {
            System.out.println(String.format("The job ran for %s", name));
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job launchingViaMessagesJob() {
        return jobBuilderFactory.get("launchingViaMessagesJob")
                .start(stepBuilderFactory.get("launchingViaMessagesJobStep1")
                        .tasklet(launchingViaMessagesJobTasklet(null))
                        .build())
                .build();
    }
}
