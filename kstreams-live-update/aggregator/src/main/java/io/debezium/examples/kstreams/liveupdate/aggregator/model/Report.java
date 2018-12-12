/*
 * Copyright Debezium Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.examples.kstreams.liveupdate.aggregator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Report {

    public long id;

    @JsonProperty("system_id")
    public long systemId;

    @JsonProperty("system_name")
    public String systemName;

    @JsonProperty("rule_id")
    public long ruleId;

    @JsonProperty("rule_name")
    public String ruleName;

    public Report() {
    }

    @Override
    public String toString() {
        return "Report [id=" + id + ", systemId=" + systemId + ", systemName=" + systemName + ", ruleId=" + ruleId
                + ", ruleName=" + ruleName + "]";
    }
}
