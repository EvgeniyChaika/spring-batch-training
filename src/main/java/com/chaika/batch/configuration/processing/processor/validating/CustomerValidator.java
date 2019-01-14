package com.chaika.batch.configuration.processing.processor.validating;

import com.chaika.batch.utils.dao.Customer;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

/**
 * Created by echaika on 14.01.2019
 */
public class CustomerValidator implements Validator<Customer> {

    @Override
    public void validate(Customer value) throws ValidationException {
        if (value.getFirstName().startsWith("A")) {
            throw new ValidationException("First name that begin with A are invalid: " + value);
        }
    }
}
