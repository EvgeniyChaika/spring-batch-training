package com.chaika.batch.configuration.input.reader.jdbc;

import com.chaika.batch.configuration.input.reader.dao.Customer;
import com.chaika.batch.configuration.input.reader.jdbc.mapper.CustomerDatabaseJobRowMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by echaika on 28.12.2018
 */
@Configuration
public class DatabaseJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Autowired
    public DatabaseJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

//    @Bean
//    public JdbcCursorItemReader<Customer> cursorDatabaseJobItemReader() {
//        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();
//
//        reader.setSql("select * from customer order by lastName, firstName");
//        reader.setDataSource(this.dataSource);
//        reader.setRowMapper(new CustomerDatabaseJobRowMapper());
//
//        return reader;
//    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingDatabaseJobItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(10);
        reader.setRowMapper(new CustomerDatabaseJobRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();

        queryProvider.setSelectClause("id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(2);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public ItemWriter<Customer> customerDatabaseJobItemWriter() {
        return new ItemWriter<Customer>() {
            @Override
            public void write(List<? extends Customer> items) throws Exception {
                for (Customer item : items) {
                    System.out.println(item.toString());
                }
            }
        };
    }

    @Bean
    public Step databaseJobStep1() {
        return stepBuilderFactory.get("databaseJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(pagingDatabaseJobItemReader())
                .writer(customerDatabaseJobItemWriter())
                .build();
    }

    @Bean
    public Job databaseJob() {
        return jobBuilderFactory.get("databaseJob")
                .start(databaseJobStep1())
                .build();
    }
}
