# Incremental Snapshot Demo

## Set-Up

```
DEBEZIUM_VERSION=2.6 docker compose -f docker-compose-mysql.yaml up
```

## Register Connector, Examine Change events

```
# only with customers
kcctl apply -f register-mysql.json
```

```
docker run --tty --rm \
     --network tutorial_default \
     quay.io/debezium/tooling:1.2 \
     kcat -b kafka:9092 -C -o beginning -q \
     -t dbserver1.inventory.customers | jq .payload
```

```
export $(cat .env | xargs) && jbang DataGen.java
```

## Update Connector

```
# add products table
kcctl apply -f register-mysql.json
```

```
docker run --tty --rm -i \
  --network tutorial_default \
  quay.io/debezium/tooling:1.2 \
  bash -c 'mycli mysql://root:debezium@mysql:3306/inventory'

INSERT INTO products VALUES (default,"scooter","Small 2-wheel scooter",3.14);
```

```
docker run --tty --rm \
     --network tutorial_default \
     quay.io/debezium/tooling:1.2 \
     kcat -b kafka:9092 -C -o beginning -q \
     -t dbserver1.inventory.products | jq .payload
```

```
INSERT INTO inventory.debezium_signal (id, type, data) VALUES('ad-hoc-1', 'execute-snapshot', '{"data-collections": ["inventory.products"],"type":"incremental"}');
```

## Useful Commands

docker compose -f docker-compose-mysql.yaml exec kafka bash -c '/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --list'