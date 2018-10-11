# Reproducer for https://github.com/confluentinc/kafka-connect-elasticsearch/issues/99.

Do the following:

* export DEBEZIUM_VERSION=0.9
* cd unwrap-smt
* Start Kafka, Connect, MySQL and ES: `docker-compose -f docker-compose-es.yaml up`
* Upper-case a table name: `docker-compose -f docker-compose-es.yaml exec mysql bash -c 'mysql -u $MYSQL_USER  -p$MYSQL_PASSWORD inventory -e "rename table customers to Customers"'`
* Register source: `curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @source.json`
* Verify "Customers" topic exists": `docker-compose -f docker-compose-es.yaml exec kafka /kafka/bin/kafka-topics.sh --list --zookeeper zookeeper:2181`
* Register sink with regex transform which is ignored (THIS FAILS): `curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @es-sink.json`
* Shut down: `docker-compose -f docker-compose-es.yaml down`
