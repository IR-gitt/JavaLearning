FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/OnlyOneMethod-0.0.1-SNAPSHOT.jar /app/onlyonemethod.jar
CMD ["java", "-jar", "/app/onlyonemethod.jar"]
EXPOSE 8080
COPY file.xlsx /app/file.xlsx