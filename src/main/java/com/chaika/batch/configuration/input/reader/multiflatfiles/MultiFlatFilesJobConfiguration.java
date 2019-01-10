package com.chaika.batch.configuration.input.reader.multiflatfiles;

import com.chaika.batch.configuration.dao.Customer;
import com.chaika.batch.configuration.input.reader.mapper.CustomerFlatFilesJobFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Created by echaika on 06.01.2019
 */
@Configuration
public class MultiFlatFilesJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    @Value("classpath*:/data/multi/customer*.csv")
    private Resource[] inputFiles;

    @Autowired
    public MultiFlatFilesJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public MultiResourceItemReader<Customer> multiResourceMultiFlatFilesJobItemReader() {
        MultiResourceItemReader<Customer> reader = new MultiResourceItemReader<>();

        reader.setDelegate(customerMultiFlatFilesJobItemReader());
        reader.setResources(inputFiles);

        return reader;
    }

    @Bean
    public FlatFileItemReader<Customer> customerMultiFlatFilesJobItemReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();

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
    public ItemWriter<Customer> customerMultiFlatFilesJobItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Step multiFlatFilesJobStep1() {
        return stepBuilderFactory.get("multiFlatFilesJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(multiResourceMultiFlatFilesJobItemReader())
                .writer(customerMultiFlatFilesJobItemWriter())
                .build();
    }

    @Bean
    public Job multiFlatFilesJob() {
        return jobBuilderFactory.get("multiFlatFilesJob")
                .start(multiFlatFilesJobStep1())
                .build();

    }
}
