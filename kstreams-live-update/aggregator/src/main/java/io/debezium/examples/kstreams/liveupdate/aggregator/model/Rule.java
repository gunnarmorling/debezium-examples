/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.kstreams.liveupdate.aggregator.model;

public class Rule {

    public long id;

    public String description;

    public Rule() {
    }

    public Rule(long id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Rule [id=" + id + ", description=" + description + "]";
    }
}
