/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.outbox.order.event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.debezium.examples.outbox.order.model.PurchaseOrder;
import io.debezium.examples.outbox.order.outbox.ExportedEvent;

public class OrderCreatedEvent implements ExportedEvent {

    private final long id;
    private final GenericContainer order;
    private final Long timestamp;

    private OrderCreatedEvent(long id, GenericContainer order) {
        this.id = id;
        this.order = order;
        this.timestamp = (new Date()).getTime();
    }

    public static void main(String[] args) {
        PurchaseOrder order = new PurchaseOrder(1L, LocalDateTime.now(), Collections.emptyList());
        order.setId(2L);
        Schema schema = SchemaBuilder.record("PurchaseOrder")
                .namespace("io.debezium.examples.outbox.order")
                .fields()
                .requiredLong("id")
                .requiredLong("customerId")
                .requiredString("orderDate")
                .endRecord();

        GenericRecord entry = new GenericData.Record(schema);
        entry.put("id", order.getId());
        entry.put("customerId", order.getCustomerId());
        entry.put("orderDate", order.getOrderDate().toString());

        KafkaAvroSerializer serializer = new KafkaAvroSerializer();
        Map<String, String> configs = new HashMap<>();
        configs.put("schema.registry.url", "http://localhost:8081");
        serializer.configure(configs, false);

        byte[] s = serializer.serialize("", entry);
        System.out.println(s);
    }

    public static OrderCreatedEvent of(PurchaseOrder order) {
        Schema schema = SchemaBuilder.record("PurchaseOrder")
                .namespace("io.debezium.examples.outbox.order")
                .fields()
                .requiredLong("id")
                .requiredLong("customerId")
                .requiredString("orderDate")
                .endRecord();

        GenericRecord entry = new GenericData.Record(schema);
        entry.put("id", order.getId());
        entry.put("customerId", order.getCustomerId());
        entry.put("orderDate", order.getOrderDate().toString());

//        ObjectNode asJson = mapper.createObjectNode()
//                .put("id", order.getId())
//                .put("customerId", order.getCustomerId())
//                .put("orderDate", order.getOrderDate().toString());
//
//        ArrayNode items = asJson.putArray("lineItems");
//
//        for (OrderLine orderLine : order.getLineItems()) {
//        items.add(
//                mapper.createObjectNode()
//                .put("id", orderLine.getId())
//                .put("item", orderLine.getItem())
//                .put("quantity", orderLine.getQuantity())
//                .put("totalPrice", orderLine.getTotalPrice())
//                .put("status", orderLine.getStatus().name())
//            );
//        }

        return new OrderCreatedEvent(order.getId(), entry);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(id);
    }

    @Override
    public String getAggregateType() {
        return "Order";
    }

    @Override
    public String getType() {
        return "OrderCreated";
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public GenericContainer getPayload() {
        return order;
    }
}
