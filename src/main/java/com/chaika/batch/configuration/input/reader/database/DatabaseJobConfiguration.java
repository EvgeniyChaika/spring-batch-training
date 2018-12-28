package com.chaika.batch.configuration.input.reader.database;

import com.chaika.batch.configuration.input.reader.database.dao.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.List;

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

    @Bean
    public JdbcCursorItemReader<Customer> customerDatabaseJobItemReader() {
        JdbcCursorItemReader<Customer> reader = new JdbcCursorItemReader<>();

        reader.setSql("select * from customer order by lastName, firstName");
        reader.setDataSource(this.dataSource);
        reader.setRowMapper(new CustomerDatabaseJobRowMapper());

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
                .reader(customerDatabaseJobItemReader())
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
