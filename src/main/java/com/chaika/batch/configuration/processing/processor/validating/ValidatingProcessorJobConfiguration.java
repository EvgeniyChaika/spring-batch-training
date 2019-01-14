package com.chaika.batch.configuration.processing.processor.validating;

import com.chaika.batch.utils.aggregator.CustomerLineAggregator;
import com.chaika.batch.utils.dao.Customer;
import com.chaika.batch.utils.mapper.jdbc.CustomerDatabaseJdbcJobRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by echaika on 14.01.2019
 */
@Configuration
public class ValidatingProcessorJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Autowired
    public ValidatingProcessorJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingValidatingProcessorJobItemReader() {
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
    public ValidatingItemProcessor<Customer> validatingProcessorJobItemProcessor() throws Exception {
        ValidatingItemProcessor<Customer> customerValidatingItemProcessor = new ValidatingItemProcessor<>(new CustomerValidator());
        customerValidatingItemProcessor.setFilter(true);
        customerValidatingItemProcessor.afterPropertiesSet();

        return customerValidatingItemProcessor;
    }

    @Bean
    public FlatFileItemWriter<Customer> validatingProcessorJobItemWriter() throws Exception {
        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new CustomerLineAggregator());

        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
        System.out.println(">> validatingProcessorJob output path: " + customerOutputPath);
        itemWriter.setResource(new FileSystemResource(customerOutputPath));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public Step validatingProcessorJobStep1() throws Exception {
        return stepBuilderFactory.get("validatingProcessorJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(pagingValidatingProcessorJobItemReader())
                .processor(validatingProcessorJobItemProcessor())
                .writer(validatingProcessorJobItemWriter())
                .build();
    }

    @Bean
    public Job validatingProcessorJob() throws Exception {
        return jobBuilderFactory.get("validatingProcessorJob")
                .start(validatingProcessorJobStep1())
                .build();
    }
}
