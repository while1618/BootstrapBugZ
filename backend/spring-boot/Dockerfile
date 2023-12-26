FROM maven:3.9.6-amazoncorretto-21-debian AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean install -DskipTests

FROM openjdk:21-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar bootstrapbugz-api.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "bootstrapbugz-api.jar"]
