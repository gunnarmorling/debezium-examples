/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.outbox.order.event;

import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import io.debezium.examples.outbox.order.model.PurchaseOrder;
import io.debezium.examples.outbox.order.outbox.ExportedEvent;

public class InvoiceCreatedEvent implements ExportedEvent {

    private final long customerId;
    private final GenericContainer order;
    private final Long timestamp;

    private InvoiceCreatedEvent(long customerId, GenericContainer order) {
        this.customerId = customerId;
        this.order = order;
        this.timestamp = (new Date()).getTime();
    }

    public static InvoiceCreatedEvent of(PurchaseOrder order) {
        Schema schema = SchemaBuilder.record("InvoiceCreated")
                .namespace("io.debezium.examples.outbox.order")
                .fields()
                .requiredLong("orderId")
                .requiredString("invoiceDate")
                .requiredLong("invoiceValue")
                .endRecord();

        GenericRecord entry = new GenericData.Record(schema);
        entry.put("orderId", order.getId());
        entry.put("invoiceDate", order.getOrderDate().toString());
        entry.put("invoiceValue", order.getTotalValue().longValue());

        return new InvoiceCreatedEvent(order.getCustomerId(), entry);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(customerId);
    }

    @Override
    public String getAggregateType() {
        return "Customer";
    }

    @Override
    public String getType() {
        return "InvoiceCreated";
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
