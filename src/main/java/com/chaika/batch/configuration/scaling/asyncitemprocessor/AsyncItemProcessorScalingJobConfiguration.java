package com.chaika.batch.configuration.scaling.asyncitemprocessor;

import com.chaika.batch.utils.dao.Customer;
import com.chaika.batch.utils.mapper.jdbc.CustomerDatabaseJdbcJobRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by echaika on 17.01.2019
 */
@Configuration
public class AsyncItemProcessorScalingJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Autowired
    public AsyncItemProcessorScalingJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingAsyncItemProcessorScalingJobItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new CustomerDatabaseJdbcJobRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();

        queryProvider.setSelectClause("id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);
        reader.setSaveState(false);

        return reader;
    }

    @Bean
    public ItemProcessor<Customer, Customer> asyncItemProcessorScalingJobItemProcessor() {
        return new ItemProcessor<Customer, Customer>() {
            @Override
            public Customer process(Customer item) throws Exception {
                Thread.sleep(new Random().nextInt(10));
                return new Customer(item.getId(),
                        item.getFirstName().toUpperCase(),
                        item.getLastName().toUpperCase(),
                        item.getBirthdate());
            }
        };
    }

    @Bean
    public AsyncItemProcessor asyncItemProcessorScalingJobAsyncItemProcessor() throws Exception {
        AsyncItemProcessor<Customer, Customer> asyncItemProcessor = new AsyncItemProcessor<>();

        asyncItemProcessor.setDelegate(asyncItemProcessorScalingJobItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());
        asyncItemProcessor.afterPropertiesSet();

        return asyncItemProcessor;
    }

    @Bean
    public JdbcBatchItemWriter<Customer> asyncItemProcessorScalingJobItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO new_customer VALUES (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public AsyncItemWriter<Customer> asyncItemProcessorScalingJobAsyncItemWriter() throws Exception {
        AsyncItemWriter<Customer> asyncItemWriter = new AsyncItemWriter<>();

        asyncItemWriter.setDelegate(asyncItemProcessorScalingJobItemWriter());
        asyncItemWriter.afterPropertiesSet();

        return asyncItemWriter;
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Step asyncItemProcessorScalingJobStep1() throws Exception {
        return stepBuilderFactory.get("asyncItemProcessorScalingJobStep1")
                .chunk(1000)
                .reader(pagingAsyncItemProcessorScalingJobItemReader())
                .processor(asyncItemProcessorScalingJobAsyncItemProcessor())
                .writer(asyncItemProcessorScalingJobAsyncItemWriter())
                .build();
    }

    @Bean
    public Job asyncItemProcessorScalingJob() throws Exception {
        return jobBuilderFactory.get("asyncItemProcessorScalingJob")
                .start(asyncItemProcessorScalingJobStep1())
                .build();
    }
}
