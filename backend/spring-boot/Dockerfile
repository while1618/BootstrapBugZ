FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} api.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "api.jar"]
