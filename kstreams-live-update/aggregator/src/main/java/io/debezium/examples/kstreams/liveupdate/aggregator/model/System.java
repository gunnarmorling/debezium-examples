/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.kstreams.liveupdate.aggregator.model;

public class System {

    public long id;

    public String name;

    public System() {
    }

    public System(long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "System [id=" + id + ", name=" + name + "]";
    }
}
