package io.debezium.rsvp;

import java.io.IOException;
import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.control.ActivateRequestContext;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import io.debezium.rsvp.handler.DatabaseHandler;
import io.debezium.rsvp.handler.FileHandler;
import io.debezium.rsvp.handler.RsvpHandler;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.client.WebSocketClient;
import io.undertow.websockets.core.AbstractReceiveListener;
import io.undertow.websockets.core.BufferedTextMessage;
import io.undertow.websockets.core.WebSocketChannel;

@ApplicationScoped
public class MeetupImporter {

    public enum HandlerType {
        FILE, DB;
    }

    @Inject
    EntityManagerFactory emf;

    private WebSocketChannel webSocketChannel;

    @ConfigProperty(name="io.debezium.rsvp.handler.type", defaultValue="db")
    private HandlerType handlerType;

    @ConfigProperty(name="io.debezium.rsvp.handler.dir", defaultValue="./target/rsvps")
    private String rsvpDir;

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

        RsvpHandler handler;
        if (handlerType == HandlerType.DB) {
            handler = new DatabaseHandler(emf);
        }
        else {
            handler = new FileHandler(rsvpDir);
        }


        webSocketChannel.getReceiveSetter().set(new AbstractReceiveListener() {
            @Override
            protected void onFullTextMessage(WebSocketChannel channel, BufferedTextMessage message) throws IOException {
                handler.handle(message.getData());
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
