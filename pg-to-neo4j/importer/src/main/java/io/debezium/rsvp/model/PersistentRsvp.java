package io.debezium.rsvp.model;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Rsvp")
public class PersistentRsvp {

    @Id
    public int id;

    public String visibility;

    public String response;

    public int guests;

    @ManyToOne
    public Member member;

    public Instant mtime;

    @ManyToOne
    public Event event;

    @Override
    public String toString() {
        return "Rsvp [visibility=" + visibility + ", response=" + response + ", guests=" + guests
                + ", member=" + member + ", id=" + id + ", mtime=" + mtime + ", event=" + event + "]";
    }
}
