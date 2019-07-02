package io.debezium.rsvp;

import java.io.IOException;
import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import io.debezium.rsvp.model.Event;
import io.debezium.rsvp.model.Group;
import io.debezium.rsvp.model.Member;
import io.debezium.rsvp.model.PersistentRsvp;
import io.debezium.rsvp.model.Rsvp;
import io.debezium.rsvp.model.Venue;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;

@ApplicationScoped
public class MeetupImporter {

    @Inject
    EntityManagerFactory emf;

    private WebSocketChannel webSocketChannel;
    private Jsonb jsonb = JsonbBuilder.create();

    @ActivateRequestContext
    public void start(@Observes StartupEvent se) throws Exception {
        System.out.println("##### Startup");

        Xnio xnio = Xnio.getInstance();
        XnioWorker worker = xnio.createWorker(OptionMap.builder()
                .set(Options.WORKER_IO_THREADS, 2)
                .set(Options.CONNECTION_HIGH_WATER, 1000000)
                .set(Options.CONNECTION_LOW_WATER, 1000000)
                .set(Options.WORKER_TASK_CORE_THREADS, 30)
                .set(Options.WORKER_TASK_MAX_THREADS, 30)
                .set(Options.TCP_NODELAY, true)
                .set(Options.CORK, true)
                .getMap());

        webSocketChannel = WebSocketClient.connectionBuilder(
                worker,
                new DefaultByteBufferPool(true, 1024 * 16 - 20, 1000, 10, 100),
                new URI("http://stream.meetup.com/2/rsvps")
        ).connect().get();

        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                String data = message.getData();
//                System.out.println("#### Data: " + data);

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

System.out.println("#### event: " + event);
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

            @Override
            protected void onError(WebSocketChannel channel, Throwable error) {
                super.onError(channel, error);
                error.printStackTrace();
            }
        });
        webSocketChannel.resumeReceives();
    }

    public void onShutDown(@Observes ShutdownEvent se) throws Exception {
        System.out.println("##### Stopping");
        webSocketChannel.sendClose();
    }
}
