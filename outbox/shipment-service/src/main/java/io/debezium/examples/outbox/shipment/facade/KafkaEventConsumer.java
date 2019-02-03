package io.debezium.examples.outbox.shipment.facade;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.aerogear.kafka.cdi.annotation.Consumer;
import org.aerogear.kafka.cdi.annotation.KafkaConfig;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@KafkaConfig(bootstrapServers = "#{KAFKA_SERVICE_HOST}:#{KAFKA_SERVICE_PORT}")
public class KafkaEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventConsumer.class);

    @Inject
    private OrderEventHandler orderEventHandler;

    @Consumer(topics = "#{ORDER_TOPIC_NAME}", groupId = "ShipmentService")
    public void orderArrived(String key, String value, Headers headers) {
        UUID eventId = UUID.fromString(new String(headers.lastHeader("eventId").value()));
        LOGGER.info("Received event {}", eventId);

        orderEventHandler.onOrderEvent(
                eventId,
                key,
                value
        );
    }
}
