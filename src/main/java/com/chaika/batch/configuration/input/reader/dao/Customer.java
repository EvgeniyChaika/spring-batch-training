package com.chaika.batch.configuration.input.reader.dao;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import java.util.Date;

/**
 * Created by echaika on 27.12.2018
 */
public class Customer implements ResourceAware {

    private final Long id;

    private final String firstName;

    private final String lastName;

    private final Date birthdate;

    private Resource resource;

    public Customer(Long id, String firstName, String lastName, Date birthdate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Customer{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", birthDate=").append(birthdate);
        sb.append(", from ").append(resource.getDescription());
        sb.append('}');
        return sb.toString();
    }
}
