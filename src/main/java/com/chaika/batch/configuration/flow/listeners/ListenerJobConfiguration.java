package com.chaika.batch.configuration.flow.listeners;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.List;

/**
 * Created by echaika on 26.12.2018
 */
@Configuration
public class ListenerJobConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    public ListenerJobConfiguration(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public ItemReader<String> listenerReader() {
        return new ListItemReader<>(Arrays.asList("one", "two", "three"));
    }

    @Bean
    public ItemWriter<String> listenerWriter() {
        return new ItemWriter<String>() {
            @Override
            public void write(List<? extends String> items) throws Exception {
                for (String item : items) {
                    System.out.println("Writing item " + item);
                }
            }
        };
    }

    @Bean
    public Step listenerStep1() {
        return stepBuilderFactory.get("listenerStep1")
                .<String, String>chunk(2)
                .faultTolerant()
                .listener(new CustomChunkListener())
                .reader(listenerReader())
                .writer(listenerWriter())
                .build();
    }

    @Bean
    public Job listenerJob(JavaMailSender javaMailSender) {
        return jobBuilderFactory.get("listenerJob")
                .start(listenerStep1())
                .listener(new CustomJobListener(javaMailSender))
                .build();
    }
}
