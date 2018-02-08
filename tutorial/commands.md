# Prepare WF

- ./prepare.sh
- ./target/wildfly-10.1.0.Final/bin/standalone.sh
- mvn wildfly:deploy -DskipTests=true
- Go to http://localhost:8080/hibernate-ogm-hiking-demo-1.0-SNAPSHOT/hikes.html

# Prepare Kafka etc.

- docker ps -a
- docker-compose up

# Register source connector (Avro)

- cat register-hiking-connector.json | http POST http://localhost:8083/connectors/
- http localhost:8083/connectors/hiking-connector/status
- docker-compose exec schema-registry /usr/bin/kafka-avro-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --property schema.registry.url=http://schema-registry:8081 --topic dbserver1_inventory_Hike

# Register sink connector

- cat jdbc-sink.json | http POST http://localhost:8083/connectors/
- http localhost:8083/connectors/
- docker-compose exec postgres bash -c 'psql -U $POSTGRES_USER $POSTGRES_DB -c "select * from \"dbserver1_inventory_Hike\""'

# Register source connector (JSON)

- cat register-hiking-connector-json.json | http POST http://localhost:8084/connectors/
- docker-compose exec kafka /kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic dbserver1_inventory_Hike_json




# json

# Misc.

docker-compose exec kafka /kafka/bin/kafka-topics.sh --list --zookeeper zookeeper:2181
docker-compose exec mysql bash -c 'mysql -u $MYSQL_USER -p$MYSQL_PASSWORD inventory'
http http://localhost:8081/subjects/dbserver1_inventory_Hike-value/versions
