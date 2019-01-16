package com.chaika.batch.configuration.errorhandling.skip;

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
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echaika on 16.01.2019
 */
@Configuration
public class SkipErrorHandlingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public SkipErrorHandlingJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public ListItemReader<String> skipErrorHandlingJobReader() {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SkipItemProcessor skipErrorHandlingJobProcessor(@Value("#{jobParameters['skip']}") String skip) {
        SkipItemProcessor processor = new SkipItemProcessor();

        processor.setSkip(StringUtils.hasText(skip) && "processor".equalsIgnoreCase(skip));

        return processor;
    }

    @Bean
    @StepScope
    public SkipItemWriter skipErrorHandlingJobWriter(@Value("#{jobParameters['skip']}") String skip) {
        SkipItemWriter writer = new SkipItemWriter();

        writer.setSkip(StringUtils.hasText(skip) && "writer".equalsIgnoreCase(skip));

        return writer;
    }

    @Bean
    public Step skipErrorHandlingJobStep1() {
        return stepBuilderFactory.get("skipErrorHandlingJobStep1")
                .<String, String>chunk(10)
                .reader(skipErrorHandlingJobReader())
                .processor(skipErrorHandlingJobProcessor(null))
                .writer(skipErrorHandlingJobWriter(null))
                .faultTolerant()
                .skip(CustomRetryableException.class)
                .skipLimit(15)
                .build();
    }

    @Bean
    public Job skipErrorHandlingJob() {
        return jobBuilderFactory.get("skipErrorHandlingJob")
                .start(skipErrorHandlingJobStep1())
                .build();
    }
}
