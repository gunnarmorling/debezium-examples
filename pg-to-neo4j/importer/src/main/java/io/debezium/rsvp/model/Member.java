package io.debezium.rsvp.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {

    @Id
    @JsonbProperty("member_id")
    public long id;

    public String photo;

    @JsonbProperty("member_name")
    public String name;

    @Override
    public String toString() {
        return "Member [id=" + id + ", photo=" + photo + ", name=" + name + "]";
    }
}
