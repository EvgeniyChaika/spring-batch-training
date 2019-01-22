package com.chaika.batch.configuration.withintegration.messages.informational;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.integration.stream.CharacterStreamWritingMessageHandler;
import org.springframework.messaging.MessageChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by echaika on 22.01.2019
 */
@Configuration
public class InformationalMessagesJobConfiguration implements ApplicationContextAware {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private ApplicationContext applicationContext;

    @Autowired
    public InformationalMessagesJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ListItemReader<String> informationalMessagesJobItemReader() {
        List<String> items = new ArrayList<>(1000);

        for (int i = 1; i <= 1000; i++) {
            items.add(String.valueOf(i));
        }

        return new ListItemReader<>(items);
    }

    @Bean
    public ItemWriter<String> informationalMessagesJobItemWriter() {
        return items -> {
            for (String item : items) {
                System.out.println(">> " + item);
            }
        };
    }

    @Bean
    public Step informationalMessagesJobStep1() throws Exception {
        return stepBuilderFactory.get("informationalMessagesJobStep1")
                .<String, String>chunk(100)
                .reader(informationalMessagesJobItemReader())
                .writer(informationalMessagesJobItemWriter())
                .listener((ChunkListener) informationalMessagesJobChunkListener())
                .build();
    }

    @Bean
    public Job informationalMessagesJob() throws Exception {
        return jobBuilderFactory.get("informationalMessagesJob")
                .start(informationalMessagesJobStep1())
                .listener((JobExecutionListener) informationalMessagesJobExecutionListener())
                .build();
    }

    @Bean
    public Object informationalMessagesJobExecutionListener() throws Exception {
        GatewayProxyFactoryBean proxyFactoryBean = new GatewayProxyFactoryBean(JobExecutionListener.class);

        proxyFactoryBean.setDefaultRequestChannel(informationalMessagesJobEvents());
        proxyFactoryBean.setBeanFactory(applicationContext);

        return proxyFactoryBean.getObject();
    }

    @Bean
    public Object informationalMessagesJobChunkListener() throws Exception {
        GatewayProxyFactoryBean proxyFactoryBean = new GatewayProxyFactoryBean(ChunkListener.class);

        proxyFactoryBean.setDefaultRequestChannel(informationalMessagesJobEvents());
        proxyFactoryBean.setBeanFactory(applicationContext);

        return proxyFactoryBean.getObject();
    }

    @Bean
    public MessageChannel informationalMessagesJobEvents() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "informationalMessagesJobEvents")
    public CharacterStreamWritingMessageHandler informationalMessagesJobLogger() {
        return CharacterStreamWritingMessageHandler.stdout();
    }
}
