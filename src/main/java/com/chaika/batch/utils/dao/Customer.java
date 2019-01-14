package com.chaika.batch.utils.dao;

import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

import java.util.Date;
import java.util.Objects;

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

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getBirthdate() {
        return birthdate;
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
        if (Objects.nonNull(resource)) {
            sb.append(", from ").append(resource.getDescription());
        }
        sb.append('}');
        return sb.toString();
    }
}
