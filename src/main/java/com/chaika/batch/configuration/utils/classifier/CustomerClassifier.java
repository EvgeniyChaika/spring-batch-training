package com.chaika.batch.configuration.utils.classifier;

import com.chaika.batch.configuration.utils.dao.Customer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

/**
 * Created by echaika on 11.01.2019
 */
public class CustomerClassifier implements Classifier<Customer, ItemWriter<? super Customer>> {

    private ItemWriter<Customer> evenItemWriter;
    private ItemWriter<Customer> oddItemWriter;

    public CustomerClassifier(ItemWriter<Customer> evenItemWriter, ItemWriter<Customer> oddItemWriter) {
        this.evenItemWriter = evenItemWriter;
        this.oddItemWriter = oddItemWriter;
    }

    @Override
    public ItemWriter<? super Customer> classify(Customer customer) {
        return customer.getId() % 2 == 0 ? evenItemWriter : oddItemWriter;
    }
}
