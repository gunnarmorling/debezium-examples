# Preparations

1. Strimzi operator:

curl -L https://github.com/strimzi/strimzi-kafka-operator/releases/download/0.21.1/strimzi-cluster-operator-0.21.1.yaml \
  | sed 's/namespace: .*/namespace: gmorling-test/' \
  | kubectl apply -f - -n gmorling-test

2. Kafka:

kubectl -n gmorling-test \
    apply -f https://raw.githubusercontent.com/strimzi/strimzi-kafka-operator/0.21.1/examples/kafka/kafka-persistent-single.yaml \
  && kubectl wait kafka/my-cluster --for=condition=Ready --timeout=300s -n gmorling-test

3. Database:

kubectl -n gmorling-test run --image=docker.io/gunnarmorling/saga-database \
    order-db \
    --port=5432 \
    --env="POSTGRES_USER=orderuser" \
    --env="POSTGRES_PASSWORD=orderpw" \
    --env="POSTGRES_DB=orderdb"

cat <<EOF | kubectl -n gmorling-test apply -f -
apiVersion: v1
kind: Service
metadata:
  name: orderdb
  labels:
    run: orderdb
spec:
  type: NodePort
  ports:
   - port: 5432
  selector:
   run: order-db
EOF

4. Enable tooling pod:

oc adm policy add-scc-to-user anyuid system:serviceaccount:gmorling-test:default

5. Jäger:

oc new-app jaegertracing/all-in-one:1
oc expose service/all-in-one --port=16686

# Demo


export PATH=$PATH:/Users/gmorling/Downloads/openshift-client-mac-4.7.0-0.okd-2021-04-24-103438

1. App

oc new-app docker.io/gunnarmorling/saga-order-service \
    --env="QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://orderdb:5432/orderdb" \
    --env="MP_MESSAGING_INCOMING_PAYMENTRESPONSE_BOOTSTRAP_SERVERS=my-cluster-kafka-bootstrap.gmorling-test:9092" \
    --env="MP_MESSAGING_INCOMING_CREDITRESPONSE_BOOTSTRAP_SERVERS=my-cluster-kafka-bootstrap.gmorling-test:9092" \
    --env="JAEGER_SERVICE_NAME=order-service" \
    --env="JAEGER_SAMPLER_TYPE=const" \
    --env="JAEGER_SAMPLER_PARAM=1" \
    --env="JAEGER_AGENT_HOST=all-in-one"

1. Kafka Connect

kubectl -n gmorling-test apply -f kafka-connect.yml

2. Order connector

kubectl -n gmorling-test apply -f order-connector.yml

3. Tooling Pod

kubectl run tooling -it --image=debezium/tooling --restart=Never

3. Create order

http POST http://saga-order-service:8080/orders itemId=123 quantity=2 customerId=456 paymentDue=4999 creditCardNo=xxxx-yyyy-dddd-aaaa

4. Examine database

pgcli postgresql://orderuser:orderpw@orderdb:5432/orderdb

5. Examine Kafka topics

kafkacat -b my-cluster-kafka-bootstrap.gmorling-test:9092 -C -o beginning -q -f "{\"key\":%k, \"headers\":\"%h\"}\n%s\n" -t credit-approval.request

6. Keep events in outbox table

kubectl set env deployment saga-order-service QUARKUS_DEBEZIUM_OUTBOX_REMOVE_AFTER_INSERT=false

# Clean-up between demo runs

oc delete service/saga-order-service
oc delete deployment/saga-order-service
oc delete KafkaConnector/order-connector
oc delete KafkaConnect/my-connect-cluster
oc delete pod tooling
oc delete pod tooling2