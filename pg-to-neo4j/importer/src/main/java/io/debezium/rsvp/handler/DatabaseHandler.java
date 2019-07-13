package io.debezium.rsvp.handler;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import io.debezium.rsvp.model.Event;
import io.debezium.rsvp.model.Group;
import io.debezium.rsvp.model.Member;
import io.debezium.rsvp.model.PersistentRsvp;
import io.debezium.rsvp.model.Rsvp;
import io.debezium.rsvp.model.Venue;

public class DatabaseHandler implements RsvpHandler {

    private final EntityManagerFactory emf;
    private final Jsonb jsonb;

    public DatabaseHandler(EntityManagerFactory emf) {
        this.emf = emf;
        this.jsonb = JsonbBuilder.create();
    }

    @Override
    public void handle(String data) {
        Rsvp rsvp = jsonb.fromJson(data, Rsvp.class);
        System.out.println(rsvp);

        Group group = rsvp.group;
        Venue venue = rsvp.venue;
        Member member = rsvp.member;
        Event event = rsvp.event;

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        if (venue != null) {
            if (em.find(Venue.class, venue.id) != null) {
                venue = em.merge(venue);
            }
            else {
                em.persist(venue);
            }
        }

        if (em.find(Member.class, member.id) != null) {
            member = em.merge(member);
            member.groups.add(group);
        }
        else {
            member.groups.add(group);
            em.persist(member);
        }

        if (em.find(Group.class, group.id) != null) {
            group = em.merge(group);
            group.members.add(member);
        }
        else {
            group.members.add(member);
            em.persist(group);
        }

        if (em.find(Event.class, event.id) != null) {
            event = em.merge(event);
            group.events.add(event);
            event.group = group;
            event.venue = venue;
        }
        else {
            group.events.add(event);
            event.group = group;
            event.venue = venue;
            em.persist(event);
        }

        PersistentRsvp persistentRsvp = new PersistentRsvp();
        persistentRsvp.id = rsvp.id;
        persistentRsvp.guests = rsvp.guests;
        persistentRsvp.mtime = rsvp.mtime;
        persistentRsvp.visibility = rsvp.visibility;
        persistentRsvp.response = rsvp.response;
        persistentRsvp.member = rsvp.member;
        persistentRsvp.event = rsvp.event;

        PersistentRsvp existing = em.find(PersistentRsvp.class, persistentRsvp.id);
        if (existing != null) {
            em.merge(persistentRsvp);
        }
        else {
            em.persist(persistentRsvp);
        }

        em.getTransaction().commit();
        em.close();
    }

}
