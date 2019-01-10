package com.chaika.batch.configuration.output.writer.example;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echaika on 10.01.2019
 */
@Configuration
public class WriterInterfaceJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public WriterInterfaceJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public ListItemReader<String> writerInterfaceJobItemReader() {
        List<String> items = new ArrayList<>(100);

        for (int i = 1; i <= 100; i++) {
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public PrintItemWriter writerInterfaceJobItemWriter() {
        return new PrintItemWriter();
    }

    @Bean
    public Step writerInterfaceStep1() {
        return stepBuilderFactory.get("writerInterfaceStep1")
                .<String, String>chunk(10)
                .reader(writerInterfaceJobItemReader())
                .writer(writerInterfaceJobItemWriter())
                .build();
    }

    @Bean
    public Job writerInterfaceJob() {
        return jobBuilderFactory.get("writerInterfaceJob")
                .start(writerInterfaceStep1())
                .build();
    }
}
