package com.chaika.batch.configuration.input.reader.flatfiles;

import com.chaika.batch.configuration.dao.Customer;
import com.chaika.batch.configuration.input.reader.mapper.CustomerFlatFilesJobFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by echaika on 28.12.2018
 */
@Configuration
public class FlatFilesJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Autowired
    public FlatFilesJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public FlatFileItemReader<Customer> customerFlatFilesJobItemReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();

        reader.setLinesToSkip(1);
        reader.setResource(new ClassPathResource("data/customer.csv"));

        DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("id", "firstName", "lastName", "birthdate");

        customerLineMapper.setLineTokenizer(tokenizer);
        customerLineMapper.setFieldSetMapper(new CustomerFlatFilesJobFieldSetMapper());
        customerLineMapper.afterPropertiesSet();

        reader.setLineMapper(customerLineMapper);

        return reader;
    }

    @Bean
    public ItemWriter<Customer> customerFlatFilesJobItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Step flatFilesJobStep1() {
        return stepBuilderFactory.get("flatFilesJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(customerFlatFilesJobItemReader())
                .writer(customerFlatFilesJobItemWriter())
                .build();
    }

    @Bean
    public Job flatFilesJob() {
        return jobBuilderFactory.get("flatFilesJob")
                .start(flatFilesJobStep1())
                .build();
    }
}
