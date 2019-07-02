package io.debezium.rsvp.model;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;

public class Rsvp {

    @JsonbProperty("rsvp_id")
    public int id;

    public Venue venue;

    public String visibility;

    public String response;

    public int guests;

    public Member member;

    @JsonbTypeAdapter(TimestampAdapter.class)
    public Instant mtime;

    public Event event;

    public Group group;

    @Override
    public String toString() {
        return "Rsvp [venue=" + venue + ", visibility=" + visibility + ", response=" + response + ", guests=" + guests
                + ", member=" + member + ", id=" + id + ", mtime=" + mtime + ", event=" + event + ", group="
                + group + "]";
    }
}
