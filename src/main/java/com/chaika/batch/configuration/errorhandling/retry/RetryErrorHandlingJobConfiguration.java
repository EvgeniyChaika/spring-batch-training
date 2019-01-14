package com.chaika.batch.configuration.errorhandling.retry;

import com.chaika.batch.utils.exception.CustomRetryableException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echaika on 14.01.2019
 */
@Configuration
public class RetryErrorHandlingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public RetryErrorHandlingJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public ListItemReader<String> retryErrorHandlingJobReader() {
        List<String> items = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    @StepScope
    public RetryItemProcessor retryErrorHandlingJobProcessor(@Value("#{jobParameters['retry']}") String retry) {
        RetryItemProcessor processor = new RetryItemProcessor();

        processor.setRetry(StringUtils.hasText(retry) && "processor".equalsIgnoreCase(retry));

        return processor;
    }

    @Bean
    @StepScope
    public RetryItemWriter retryErrorHandlingJobWriter(@Value("#{jobParameters['retry']}") String retry) {
        RetryItemWriter writer = new RetryItemWriter();

        writer.setRetry(StringUtils.hasText(retry) && "writer".equalsIgnoreCase(retry));

        return writer;
    }

    @Bean
    public Step retryErrorHandlingJobStep1() {
        return stepBuilderFactory.get("retryErrorHandlingJobStep1")
                .<String, String>chunk(10)
                .reader(retryErrorHandlingJobReader())
                .processor(retryErrorHandlingJobProcessor(null))
                .writer(retryErrorHandlingJobWriter(null))
                .faultTolerant()
                .retry(CustomRetryableException.class)
                .retryLimit(15)
                .build();
    }

    @Bean
    public Job retryErrorHandlingJob() {
        return jobBuilderFactory.get("retryErrorHandlingJob")
                .start(retryErrorHandlingJobStep1())
                .build();
    }
}
