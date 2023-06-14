FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bootstrapbugz-api.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "bootstrapbugz-api.jar"]
