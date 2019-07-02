package io.debezium.rsvp.model;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Event {

    @Id
    @JsonbProperty("event_id")
    public String id;

    @JsonbTypeAdapter(TimestampAdapter.class)
    public Instant time;

    @JsonbProperty("event_name")
    public String name;

    @JsonbProperty("event_url")
    public String url;

    @ManyToOne(optional=false)
    public Group group;

    @ManyToOne
    public Venue venue;

    @Override
    public String toString() {
        return "Event [id=" + id + ", time=" + time + ", name=" + name + ", url=" + url + ", group=" + group
                + ", venue=" + venue + "]";
    }
}
