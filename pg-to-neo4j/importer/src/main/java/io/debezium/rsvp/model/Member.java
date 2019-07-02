package io.debezium.rsvp.model;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Member {

    @Id
    @JsonbProperty("member_id")
    public long id;

    public String photo;

    @JsonbProperty("member_name")
    public String name;

    @ManyToMany
    public List<Group> groups = new ArrayList<>();

    @Override
    public String toString() {
        return "Member [id=" + id + ", photo=" + photo + ", name=" + name + "]";
    }
}
