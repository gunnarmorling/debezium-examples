package io.debezium.rsvp.model;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Entity;
import javax.persistence.Id;

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

    @Override
    public String toString() {
        return "Event [id=" + id + ", time=" + time + ", name=" + name + ", url=" + url + "]";
    }
}
