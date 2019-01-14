package com.chaika.batch.configuration.processing.processor.example;

import com.chaika.batch.utils.dao.Customer;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by echaika on 14.01.2019
 */
public class UpperCaseItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) throws Exception {
        return new Customer(item.getId(),
                item.getFirstName().toUpperCase(),
                item.getLastName().toUpperCase(),
                item.getBirthdate());
    }
}
