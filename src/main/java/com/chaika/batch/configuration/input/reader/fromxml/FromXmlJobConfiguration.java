package com.chaika.batch.configuration.input.reader.fromxml;

import com.chaika.batch.configuration.utils.dao.Customer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by echaika on 03.01.2019
 */
@Configuration
public class FromXmlJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private static final String CUSTOMER = "customer";

    @Autowired
    public FromXmlJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public StaxEventItemReader<Customer> cutomerFromXmlJobItemReader() {
        XStreamMarshaller unmarshaller = new XStreamMarshaller();

        Map<String, Class> aliases = new HashMap<>();
        aliases.put(CUSTOMER, Customer.class);

        unmarshaller.setAliases(aliases);

        StaxEventItemReader<Customer> reader = new StaxEventItemReader<>();
        reader.setResource(new ClassPathResource("/data/customers.xml"));
        reader.setFragmentRootElementName(CUSTOMER);
        reader.setUnmarshaller(unmarshaller);
        return reader;
    }

    @Bean
    public ItemWriter<Customer> customerFromXmlJobItemWriter() {
        return items -> {
            for (Customer item : items) {
                System.out.println(item.toString());
            }
        };
    }

    @Bean
    public Step fromXmlJobStep1() {
        return stepBuilderFactory.get("fromXmlJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(cutomerFromXmlJobItemReader())
                .writer(customerFromXmlJobItemWriter())
                .build();
    }

    @Bean
    public Job fromXmlJob() {
        return jobBuilderFactory.get("fromXmlJob")
                .start(fromXmlJobStep1())
                .build();
    }
}
