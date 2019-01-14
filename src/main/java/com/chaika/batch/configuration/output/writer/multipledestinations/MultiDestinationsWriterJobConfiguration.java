package com.chaika.batch.configuration.output.writer.multipledestinations;

import com.chaika.batch.utils.aggregator.CustomerLineAggregator;
import com.chaika.batch.utils.classifier.CustomerClassifier;
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
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by echaika on 11.01.2019
 */
@Configuration
public class MultiDestinationsWriterJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final DataSource dataSource;

    @Autowired
    public MultiDestinationsWriterJobConfiguration(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcPagingItemReader<Customer> pagingMultiDestinationsWriterJobItemReader() {
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
    public FlatFileItemWriter<Customer> jsonMultiDestinationsWriterJobItemWriter() throws Exception {
        FlatFileItemWriter<Customer> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setLineAggregator(new CustomerLineAggregator());

        String customerOutputPath = File.createTempFile("customerOutput", ".out").getAbsolutePath();
        System.out.println(">> MultiDestinationsWriterJob output path: " + customerOutputPath);

        itemWriter.setResource(new FileSystemResource(customerOutputPath));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public StaxEventItemWriter<Customer> xmlMultiDestinationsWriterJobItemWriter() throws Exception {
        XStreamMarshaller marshaller = new XStreamMarshaller();

        Map<String, Class> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);

        marshaller.setAliases(aliases);

        StaxEventItemWriter<Customer> itemWriter = new StaxEventItemWriter<>();
        itemWriter.setRootTagName("customers");
        itemWriter.setMarshaller(marshaller);

        String customerOutputPath = File.createTempFile("customerOutput", ".xml").getAbsolutePath();
        System.out.println(">> MultiDestinationsWriterJob output path: " + customerOutputPath);

        itemWriter.setResource(new FileSystemResource(customerOutputPath));
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

//    @Bean
//    public CompositeItemWriter<Customer> multiDestinationsWriterJobItemWriter() throws Exception {
//        List<ItemWriter<? super Customer>> writers = new ArrayList<>(2);
//
//        writers.add(xmlMultiDestinationsWriterJobItemWriter());
//        writers.add(jsonMultiDestinationsWriterJobItemWriter());
//
//        CompositeItemWriter<Customer> itemWriter = new CompositeItemWriter<>();
//        itemWriter.setDelegates(writers);
//        itemWriter.afterPropertiesSet();
//
//        return itemWriter;
//    }

    @Bean
    public ClassifierCompositeItemWriter<Customer> multiDestinationsWriterJobItemWriter() throws Exception {
        ClassifierCompositeItemWriter<Customer> itemWriter = new ClassifierCompositeItemWriter<>();
        itemWriter.setClassifier(new CustomerClassifier(xmlMultiDestinationsWriterJobItemWriter(), jsonMultiDestinationsWriterJobItemWriter()));

        return itemWriter;
    }

    /**
     * Use .stream() only with {@link #multiDestinationsWriterJobItemWriter()} for classifier logic.
     */
    @Bean
    public Step multiDestinationsWriterJobStep1() throws Exception {
        return stepBuilderFactory.get("multiDestinationsWriterJobStep1")
                .<Customer, Customer>chunk(10)
                .reader(pagingMultiDestinationsWriterJobItemReader())
                .writer(multiDestinationsWriterJobItemWriter())
                .stream(xmlMultiDestinationsWriterJobItemWriter())
                .stream(jsonMultiDestinationsWriterJobItemWriter())
                .build();
    }

    @Bean
    public Job multiDestinationsWriterJob() throws Exception {
        return jobBuilderFactory.get("multiDestinationsWriterJob")
                .start(multiDestinationsWriterJobStep1())
                .build();
    }
}
