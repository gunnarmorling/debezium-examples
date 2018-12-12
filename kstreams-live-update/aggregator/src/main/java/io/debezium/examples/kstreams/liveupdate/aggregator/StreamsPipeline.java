/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.kstreams.liveupdate.aggregator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.debezium.examples.kstreams.liveupdate.aggregator.model.Category;
import io.debezium.examples.kstreams.liveupdate.aggregator.model.Order;
import io.debezium.examples.kstreams.liveupdate.aggregator.model.Report;
import io.debezium.examples.kstreams.liveupdate.aggregator.model.Rule;
import io.debezium.examples.kstreams.liveupdate.aggregator.model.System;
import io.debezium.examples.kstreams.liveupdate.aggregator.serdes.ChangeEventAwareJsonSerde;

public class StreamsPipeline {

    private static final Logger LOG = LoggerFactory.getLogger(StreamsPipeline.class);

    public static KTable<Windowed<String>, String> salesPerCategory(StreamsBuilder builder) {
        Serde<Long> longKeySerde = new ChangeEventAwareJsonSerde<>(Long.class);
        longKeySerde.configure(Collections.emptyMap(), true);

        Serde<Order> orderSerde = new ChangeEventAwareJsonSerde<>(Order.class);
        orderSerde.configure(Collections.emptyMap(), false);

        Serde<Category> categorySerde = new ChangeEventAwareJsonSerde<>(Category.class);
        categorySerde.configure(Collections.emptyMap(), false);

        KTable<Long, Category> category = builder.table("dbserver1.inventory.categories", Consumed.with(longKeySerde, categorySerde));

        return builder.stream(
                "dbserver1.inventory.orders",
                Consumed.with(longKeySerde, orderSerde)
                )
                .selectKey((k, v) -> v.categoryId)
                .join(
                        category,
                        (value1, value2) -> {
                            value1.categoryName = value2.name;
                            return value1;
                        },
                        Joined.with(Serdes.Long(), orderSerde, null)
                )
                .selectKey((k, v) -> v.categoryName)
                .groupByKey(Serialized.with(Serdes.String(), orderSerde))
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5).toMillis()))
                .aggregate(
                        () -> 0L, /* initializer */
                        (aggKey, newValue, aggValue) -> {
                            aggValue += newValue.salesPrice;
                            return aggValue;
                        },
                        Materialized.with(Serdes.String(), Serdes.Long())
                )
                .mapValues(v -> BigDecimal.valueOf(v)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .mapValues(v -> String.valueOf(v));
    }

    public static KTable<Long, Report> reportAggregates(StreamsBuilder builder) {
        Serde<Long> longKeySerde = new ChangeEventAwareJsonSerde<>(Long.class);
        longKeySerde.configure(Collections.emptyMap(), true);

        Serde<Report> reportSerde = new ChangeEventAwareJsonSerde<>(Report.class);
        reportSerde.configure(Collections.emptyMap(), false);

        Serde<io.debezium.examples.kstreams.liveupdate.aggregator.model.System> systemSerde = new ChangeEventAwareJsonSerde<>(io.debezium.examples.kstreams.liveupdate.aggregator.model.System.class);
        systemSerde.configure(Collections.emptyMap(), false);

        Serde<Rule> ruleSerde = new ChangeEventAwareJsonSerde<>(Rule.class);
        ruleSerde.configure(Collections.emptyMap(), false);

        KTable<Long, io.debezium.examples.kstreams.liveupdate.aggregator.model.System> systems = builder.table("dbserver1.inventory.systems", Consumed.with(longKeySerde, systemSerde));
        KTable<Long, Rule> rules = builder.table("dbserver1.inventory.rules", Consumed.with(longKeySerde, ruleSerde));

        return builder.stream(
                "dbserver1.inventory.reports",
                Consumed.with(longKeySerde, reportSerde)
                )
                // obtain reports as ktable partitioned by system id
                .selectKey((key, report) -> report.systemId)
                .groupByKey(Serialized.with(longKeySerde, reportSerde))
                .reduce((v1, v2) -> v2)
                // join with system
                .join(systems, new ValueJoiner<Report, io.debezium.examples.kstreams.liveupdate.aggregator.model.System, Report>() {

                            @Override
                            public Report apply(Report value1, System value2) {
                                Report r = new Report();
                                r.id = value1.id;
                                r.ruleId = value1.ruleId;
                                r.systemId = value1.systemId;
                                r.systemName = value2.name;

                                return r;
                            }
                       },
                        Materialized.with(Serdes.Long(), reportSerde)
                 )
                // obtain reports (enriched with system info) as ktable partitioned by rule id
                .toStream()
                .selectKey((key, report) -> report.ruleId)
                .groupByKey(Serialized.with(longKeySerde, reportSerde))
                .reduce((v1, v2) -> v2)
                // join with rule
                .join(rules, new ValueJoiner<Report, Rule, Report>() {

                    @Override
                    public Report apply(Report value1, Rule value2) {
                        Report r = new Report();
                        r.id = value1.id;
                        r.ruleId = value1.ruleId;
                        r.ruleName = value2.description;
                        r.systemId = value1.systemId;
                        r.systemName = value1.systemName;

                        return r;
                    }
               },
                Materialized.with(Serdes.Long(), reportSerde)

                // todo: partition by report id again
         );
    }

    public static void waitForTopicsToBeCreated(String bootstrapServers) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(config)) {
            AtomicBoolean topicsCreated = new AtomicBoolean(false);

            while (topicsCreated.get() == false) {
                LOG.info("Waiting for topics to be created");

                ListTopicsResult topics = adminClient.listTopics();
                topics.names().whenComplete((t, e) -> {
                    if (e != null) {
                        throw new RuntimeException(e);
                    }
                    else if (t.contains("dbserver1.inventory.reports") && t.contains("dbserver1.inventory.systems") && t.contains("dbserver1.inventory.rules")) {
                        LOG.info("Found topics 'dbserver1.inventory.reports', 'dbserver1.inventory.systems' and 'dbserver1.inventory.rules'");
                        topicsCreated.set(true);
                    }
                });

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
