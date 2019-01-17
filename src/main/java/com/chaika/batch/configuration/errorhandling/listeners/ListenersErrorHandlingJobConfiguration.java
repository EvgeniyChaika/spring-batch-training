package com.chaika.batch.configuration.errorhandling.listeners;

import com.chaika.batch.utils.exception.CustomException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echaika on 17.01.2019
 */
@Configuration
public class ListenersErrorHandlingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ListenersErrorHandlingJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public ListItemReader<String> listenersErrorHandlingJobReader() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    @StepScope
    public ListenersErrorHandlingJobProcessor listenersErrorHandlingJobItemProcessor() {
        return new ListenersErrorHandlingJobProcessor();
    }

    @Bean
    @StepScope
    public ListenersErrorHandlingJobWriter listenersErrorHandlingJobItemWriter() {
        return new ListenersErrorHandlingJobWriter();
    }

    @Bean
    public Step listenersErrorHandlingJobStep1() {
        return stepBuilderFactory.get("listenersErrorHandlingJobStep1")
                .<String, String>chunk(10)
                .reader(listenersErrorHandlingJobReader())
                .processor(listenersErrorHandlingJobItemProcessor())
                .writer(listenersErrorHandlingJobItemWriter())
                .faultTolerant()
                .skip(CustomException.class)
                .skipLimit(15)
                .listener(new CustomListener())
                .build();
    }

    @Bean
    public Job listenersErrorHandlingJob() {
        return jobBuilderFactory.get("listenersErrorHandlingJob")
                .start(listenersErrorHandlingJobStep1())
                .build();
    }
}
