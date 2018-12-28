package com.chaika.batch.configuration.input.reader.database;

import com.chaika.batch.configuration.input.reader.database.dao.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by echaika on 28.12.2018
 */
class CustomerDatabaseJobRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getDate("birthdate"));
    }
}
