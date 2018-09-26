FROM debezium/connect:0.8

# Deploy PostgreSQL JDBC Driver
COPY postgresql-42.1.4.jar /kafka/libs

# Deploy Kafka Connect JDBC
ENV KAFKA_CONNECT_JDBC_DIR=$KAFKA_CONNECT_PLUGINS_DIR/kafka-connect-jdbc \
    KAFKA_CONNECT_ES_DIR=$KAFKA_CONNECT_PLUGINS_DIR/kafka-connect-elasticsearch

RUN mkdir $KAFKA_CONNECT_JDBC_DIR
COPY kafka-connect-jdbc-3.3.0.jar $KAFKA_CONNECT_JDBC_DIR

# Deploy Confluent Elasticsearch sink connector
RUN mkdir $KAFKA_CONNECT_ES_DIR
COPY kafka-connect-jdbc-3.3.0.jar $KAFKA_CONNECT_ES_DIR
COPY kafka-connect-elasticsearch-5.0.0.jar $KAFKA_CONNECT_ES_DIR
COPY jest-2.0.0.jar $KAFKA_CONNECT_ES_DIR
COPY httpcore-nio-4.4.4.jar $KAFKA_CONNECT_ES_DIR
COPY httpclient-4.5.1.jar $KAFKA_CONNECT_ES_DIR
COPY httpasyncclient-4.1.1.jar $KAFKA_CONNECT_ES_DIR
COPY httpcore-4.4.4.jar $KAFKA_CONNECT_ES_DIR
COPY commons-logging-1.2.jar $KAFKA_CONNECT_ES_DIR
COPY commons-codec-1.9.jar $KAFKA_CONNECT_ES_DIR
COPY httpcore-4.4.4.jar $KAFKA_CONNECT_ES_DIR
COPY jest-common-2.0.0.jar $KAFKA_CONNECT_ES_DIR
COPY gson-2.4.jar $KAFKA_CONNECT_ES_DIR
