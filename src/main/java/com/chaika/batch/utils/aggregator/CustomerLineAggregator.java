package com.chaika.batch.utils.aggregator;

import com.chaika.batch.utils.dao.Customer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * Created by echaika on 11.01.2019
 */
public class CustomerLineAggregator implements LineAggregator<Customer> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String aggregate(Customer item) {
        try {
            return objectMapper.writeValueAsString(item);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to serialize Customer", e);
        }
    }
}
