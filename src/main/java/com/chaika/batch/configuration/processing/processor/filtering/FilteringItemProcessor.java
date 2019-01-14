package com.chaika.batch.configuration.processing.processor.filtering;

import com.chaika.batch.utils.dao.Customer;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by echaika on 14.01.2019
 */
public class FilteringItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer item) throws Exception {
        if (item.getId() % 2 == 0) {
            return null;
        } else {
            return item;
        }
    }
}
