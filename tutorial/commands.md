# Prepare Kafka etc.

- export DOCKER_HOST_NAME=`ipconfig getifaddr en0`
- export DEBEZIUM_VERSION=0.7
- docker ps -a
- docker-compose up

# Prepare WF

- mvn clean install -DskipTests=true
- ./prepare.sh
- ./target/wildfly-10.1.0.Final/bin/standalone.sh
- mvn wildfly:deploy -DskipTests=true
- Go to http://localhost:8080/hibernate-ogm-hiking-demo-1.0-SNAPSHOT/hikes.html

# Register source connector (Avro)

- cat register-hiking-connector.json | http POST http://localhost:8083/connectors/
- http localhost:8083/connectors/hiking-connector/status
- docker-compose exec schema-registry /usr/bin/kafka-avro-console-consumer --bootstrap-server kafka:9092 --from-beginning --property print.key=true --property schema.registry.url=http://schema-registry:8081 --topic dbserver1_inventory_Hike
- http http://localhost:8081/subjects/dbserver1_inventory_Hike-value/versions/1 | jq '.schema | fromjson'

# Register sink connector

- Back to slides - mention SMT

- cat jdbc-sink.json | http POST http://localhost:8083/connectors/
- http localhost:8083/connectors/
- docker-compose exec postgres bash -c 'psql -U $POSTGRES_USER $POSTGRES_DB -c "select * from \"dbserver1_inventory_Hike\""'

# Stop Kafka Connect

- docker stop tutorial_connect_1
- Change some data
- docker-compose up -d
- Show PG again as it catches up

# Register source connector (JSON)

- cat register-hiking-connector-json.json | http POST http://localhost:8084/connectors/
- docker-compose exec kafka /kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic dbserver1_inventory_Hike_json

# Start Swarm app

- mvn wildfly-swarm:run -Dswarm.http.port=8079
- Open in other browser: http://localhost:8079/
# Misc.

- docker-compose exec kafka /kafka/bin/kafka-topics.sh --list --zookeeper zookeeper:2181
- docker-compose exec mysql bash -c 'mysql -u $MYSQL_USER -p$MYSQL_PASSWORD inventory'
