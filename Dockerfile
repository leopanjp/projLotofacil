# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /build/target/lotofacil-sorteios-1.0.0.jar /app/lotofacil.jar

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/LOTERIA
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV CAIXA_LOTOFACIL_BASE_URL=https://servicebus2.caixa.gov.br/portaldeloterias/api/lotofacil

ENTRYPOINT ["java", "-jar", "/app/lotofacil.jar"]
