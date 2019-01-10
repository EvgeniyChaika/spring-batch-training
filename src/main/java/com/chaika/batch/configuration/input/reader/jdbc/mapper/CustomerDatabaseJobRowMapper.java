package com.chaika.batch.configuration.input.reader.jdbc.mapper;

import com.chaika.batch.configuration.dao.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by echaika on 28.12.2018
 */
public class CustomerDatabaseJobRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getDate("birthdate"));
    }
}
