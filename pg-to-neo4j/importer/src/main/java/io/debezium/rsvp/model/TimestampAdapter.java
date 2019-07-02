package io.debezium.rsvp.model;

import java.time.Instant;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonValue;
import javax.json.bind.adapter.JsonbAdapter;

public class TimestampAdapter implements JsonbAdapter<Instant, JsonValue> {

  @Override
  public JsonValue adaptToJson(Instant timestamp) {
      return Json.createValue(timestamp.toEpochMilli());
  }

  @Override
  public Instant adaptFromJson(JsonValue json) {
      return Instant.ofEpochMilli(((JsonNumber)json).longValue());
  }
}