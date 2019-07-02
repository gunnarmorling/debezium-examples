package io.debezium.rsvp.model;

import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Venue {

    @Id
    @JsonbProperty("venue_id")
    public String id;

    @JsonbProperty("venue_name")
    public String name;

    public double lon;

    public double lat;

    @Override
    public String toString() {
        return "Venue [id=" + id + ", name=" + name + ", lon=" + lon + ", lat=" + lat + "]";
    }
}
