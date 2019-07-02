# Postgres to Neo4j via Debezium

Launch Kafka, ZooKeeper, Kafka Connect and Postgres:

```
export DEBEZIUM_VERSION=0.10
docker-compose up
```

Create "rsvp" schema in Postgres:

```
docker-compose exec postgres env PGOPTIONS="--search_path=rsvp" bash -c 'psql -U $POSTGRES_USER rsvpdb -c "CREATE SCHEMA rsvp"'
```

Launch importer:

```
mvn compile quarkus:dev
```

Register Debezium Postgres connector:

```
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @register-postgres.json
```

Examine topic for "rsvp" table in Kafka:

```
docker-compose exec kafka /kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server kafka:9092 \
    --from-beginning \
    --property print.key=true \
    --topic dbserver1.rsvp.rsvp
```
