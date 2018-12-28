package com.chaika.batch.configuration.input.reader.jdbc.dao;

import java.util.Date;

/**
 * Created by echaika on 27.12.2018
 */
public class Customer {

    private final Long id;

    private final String firstName;

    private final String lastName;

    private final Date birthDate;

    public Customer(Long id, String firstName, String lastName, Date birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Customer{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", birthDate=").append(birthDate);
        sb.append('}');
        return sb.toString();
    }
}
