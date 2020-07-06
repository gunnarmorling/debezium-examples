package io.debezium.demos.txaggregates.members.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Address extends PanacheEntity {

    public String street;
    public String city;
    public String zip;
    public String country;

    @ManyToOne
    public Member member;
}
