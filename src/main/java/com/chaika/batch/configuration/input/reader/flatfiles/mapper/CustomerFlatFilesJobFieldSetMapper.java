package com.chaika.batch.configuration.input.reader.flatfiles.mapper;

import com.chaika.batch.configuration.dao.Customer;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

/**
 * Created by echaika on 28.12.2018
 */
public class CustomerFlatFilesJobFieldSetMapper implements FieldSetMapper<Customer> {

    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Customer(
                fieldSet.readLong("id"),
                fieldSet.readString("firstName"),
                fieldSet.readString("lastName"),
                fieldSet.readDate("birthdate", "yyyy-MM-dd HH:mm:ss")
        );
    }
}
