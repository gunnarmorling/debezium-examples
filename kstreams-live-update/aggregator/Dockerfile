# FROM fabric8/java-jboss-openjdk8-jdk
# ENV JAVA_OPTIONS='-Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=${PORT}'
# COPY target/lib/* /deployments/lib/
# COPY target/*-runner.jar /deployments/app.jar
# ENTRYPOINT [ "/deployments/run-java.sh" ]

# FROM registry.fedoraproject.org/fedora-minimal
FROM fedora:28
RUN yum -y install libstdc++ procps && yum clean all
WORKDIR /work/
COPY target/*-runner /work/application
# COPY librocksdbjni-linux64.so /work/rocksdblibs/
RUN chmod 775 /work
EXPOSE 8080
CMD ["./application", "-Dquarkus.http.host=0.0.0.0", "-Dquarkus.http.port=${PORT}", "-Xmx64m"]
