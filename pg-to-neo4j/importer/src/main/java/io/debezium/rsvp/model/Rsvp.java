package io.debezium.rsvp.model;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Rsvp {

    @ManyToOne
    public Venue venue;

    public String visibility;

    public String response;

    public int guests;

    @ManyToOne
    public Member member;

    @Id
    @JsonbProperty("rsvp_id")
    public int rsvpId;

    @JsonbTypeAdapter(TimestampAdapter.class)
    public Instant mtime;

    @ManyToOne
    public Event event;

    @ManyToOne
    public Group group;

    @Override
    public String toString() {
        return "Rsvp [venue=" + venue + ", visibility=" + visibility + ", response=" + response + ", guests=" + guests
                + ", member=" + member + ", rsvpId=" + rsvpId + ", mtime=" + mtime + ", event=" + event + ", group="
                + group + "]";
    }
}
