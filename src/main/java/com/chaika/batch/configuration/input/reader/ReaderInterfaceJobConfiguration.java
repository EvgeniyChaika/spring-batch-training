package com.chaika.batch.configuration.input.reader;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by echaika on 27.12.2018
 */
@Configuration
public class ReaderInterfaceJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ReaderInterfaceJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public StatelessItemReader statelessItemReader() {
        /*
        List "data" is immutable.
        */
        final List<String> data = Arrays.asList("one", "two", "three");
        return new StatelessItemReader(data);
    }

    @Bean
    public Step readerInterfaceStep() {
        return stepBuilderFactory.get("readerInterfaceStep")
                .<String, String>chunk(3)
                .reader(statelessItemReader())
                .writer(list -> list.forEach(System.out::println))
                .build();
    }

    @Bean
    public Job readerInterfaceJob() {
        return jobBuilderFactory.get("readerInterfaceJob")
                .start(readerInterfaceStep())
                .build();
    }
}
