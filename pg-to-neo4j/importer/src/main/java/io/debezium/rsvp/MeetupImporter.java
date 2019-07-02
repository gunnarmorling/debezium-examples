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

                EntityManager em = emf.createEntityManager();
                em.getTransaction().begin();

                if (rsvp.venue != null) {
                    if (em.find(Venue.class, rsvp.venue.id) != null) {
                        em.merge(rsvp.venue);
                    }
                    else {
                        em.persist(rsvp.venue);
                    }
                }

                if (rsvp.member != null) {
                    if (em.find(Member.class, rsvp.member.id) != null) {
                        em.merge(rsvp.member);
                    }
                    else {
                        em.persist(rsvp.member);
                    }
                }

                if (rsvp.event != null) {
                    if (em.find(Event.class, rsvp.event.id) != null) {
                        em.merge(rsvp.event);
                    }
                    else {
                        em.persist(rsvp.event);
                    }
                }

                if (rsvp.group != null) {
                    if (em.find(Group.class, rsvp.group.id) != null) {
                        em.merge(rsvp.group);
                    }
                    else {
                        em.persist(rsvp.group);
                    }
                }

                Rsvp existing = em.find(Rsvp.class, rsvp.rsvpId);
                if (existing != null) {
                    em.merge(rsvp);
                }
                else {
                    em.persist(rsvp);
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
