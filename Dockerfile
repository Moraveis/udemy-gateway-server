FROM openjdk:17-slim

RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

COPY target/gateway-server-0.0.1-SNAPSHOT.jar gateway-server-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "gateway-server-0.0.1-SNAPSHOT.jar"]
