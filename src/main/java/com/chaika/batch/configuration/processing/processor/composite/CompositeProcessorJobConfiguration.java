package com.chaika.batch.configuration.processing.processor.composite;

import com.chaika.batch.configuration.processing.processor.example.UpperCaseItemProcessor;
import com.chaika.batch.configuration.processing.processor.filtering.FilteringItemProcessor;
import com.chaika.batch.utils.aggregator.CustomerLineAggregator;
import com.chaika.batch.utils.dao.Customer;
import com.chaika.batch.utils.mapper.jdbc.CustomerDatabaseJdbcJobRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echaika on 14.01.2019
 */
@Configuration
public class CompositeProcessorJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Autowired
    public CompositeProcessorJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingCompositeProcessorJobItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerDatabaseJdbcJobRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public CompositeItemProcessor<Customer, Customer> compositeProcessorJobItemProcessor() throws Exception {
        List<ItemProcessor<Customer, Customer>> delegates = new ArrayList<>(2);

        delegates.add(new FilteringItemProcessor());
        delegates.add(new UpperCaseItemProcessor());

        CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<>();

        compositeItemProcessor.setDelegates(delegates);
        compositeItemProcessor.afterPropertiesSet();

        return compositeItemProcessor;
    }

    @Bean
    public FlatFileItemWriter<Customer> compositeProcessorJobItemWriter() throws Exception {
        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new CustomerLineAggregator());

        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
        System.out.println(">> compositeProcessorJob output path: " + customerOutputPath);
        itemWriter.setResource(new FileSystemResource(customerOutputPath));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public Step compositeProcessorJobStep1() throws Exception {
        return stepBuilderFactory.get("compositeProcessorJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(pagingCompositeProcessorJobItemReader())
                .processor(compositeProcessorJobItemProcessor())
                .writer(compositeProcessorJobItemWriter())
                .build();
    }

    @Bean
    public Job compositeProcessorJob() throws Exception {
        return jobBuilderFactory.get("compositeProcessorJob")
                .start(compositeProcessorJobStep1())
                .build();
    }
}
