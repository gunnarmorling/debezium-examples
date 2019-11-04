/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.outbox.order.outbox;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@ApplicationScoped
public class EventSender {

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    @ConfigProperty(name="schema.registry.url", defaultValue="http://localhost:8081")
    private String schemRegistryUrl;

    private KafkaAvroSerializer serializer;

    @PostConstruct
    public void postConstruct() {
        serializer = new KafkaAvroSerializer();
        Map<String, String> configs = new HashMap<>();
        configs.put("schema.registry.url", schemRegistryUrl);
        serializer.configure(configs, false);
    }

    public void onExportedEvent(@Observes ExportedEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent(
                event.getAggregateType(),
                event.getAggregateId(),
                event.getType(),
                serializer.serialize(event.getAggregateType(), event.getPayload()),
                event.getTimestamp()
        );

        // This will produce an INSERT followed by a DELETE;
        // So the events table will always be empty, but still both events will be captured from
        // the log by Debezium (and the latter will be ignored)
        entityManager.persist(outboxEvent);
//        entityManager.remove(outboxEvent);
    }
}
