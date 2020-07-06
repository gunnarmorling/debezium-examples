package io.debezium.demos.txaggregates.members.model;

import java.time.LocalDate;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Member extends PanacheEntity {

    public String name;

    @JsonbDateFormat(value = "yyyy-MM-dd")
    public LocalDate birthday;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    public List<Address> addresses;
}
