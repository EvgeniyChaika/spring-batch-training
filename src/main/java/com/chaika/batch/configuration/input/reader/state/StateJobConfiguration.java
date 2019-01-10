package com.chaika.batch.configuration.input.reader.state;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echaika on 10.01.2019
 */
@Configuration
public class StateJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public StateJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    @StepScope
    public StatefulItemReader stateJobItemReader() {
        List<String> items = new ArrayList<>(100);

        for (int i = 0; i < 100; i++) {
            items.add(String.valueOf(i));
        }

        return new StatefulItemReader(items);
    }

    @Bean
    public ItemWriter<String> stateJobItemWriter() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item : items) {
                    System.out.println(">> " + item);
                }
            }
        };
    }

    @Bean
    public Step stateJobStep1() {
        return stepBuilderFactory.get("stateJobStep1")
                .<String, String>chunk(10)
                .reader(stateJobItemReader())
                .writer(stateJobItemWriter())
                .stream(stateJobItemReader())
                .build();
    }

    @Bean
    public Job stateJob() {
        return jobBuilderFactory.get("stateJob")
                .start(stateJobStep1())
                .build();
    }
}
