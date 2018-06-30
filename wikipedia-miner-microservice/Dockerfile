FROM openjdk:8u171-jdk-alpine3.7

COPY ./target/wikipedia-miner-microservice.jar ./wikipedia-miner-microservice.jar
ENTRYPOINT ["java", "-jar", "-Xms512m", "-Xmx8192m", "wikipedia-miner-microservice.jar"]