FROM fabric8/java-jboss-openjdk8-jdk

# COPY target/kstreams-live-update-aggregator-0.1-SNAPSHOT.jar /deployments
# COPY target/dependencies/* /deployments/
# COPY target/demo-thorntail.jar /deployments

RUN mkdir /deployments/aggregator
RUN mkdir /deployments/aggregator/classes
RUN mkdir /deployments/aggregator/demo-bin
COPY target/classes /deployments/aggregator/classes
COPY target/demo-bin /deployments/aggregator/demo-bin

