package com.chaika.batch.utils.mapper.jdbc;

import com.chaika.batch.utils.dao.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by echaika on 28.12.2018
 */
public class CustomerDatabaseJdbcJobRowMapper implements RowMapper<Customer> {

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Customer(
                rs.getLong("id"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getDate("birthdate"));
    }
}
