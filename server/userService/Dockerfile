FROM openjdk:17-jdk-slim
LABEL authors="ele63"
WORKDIR /app
COPY build/libs/user-service.jar user-service.jar
EXPOSE 8080
CMD ["java", "-jar", "user-service.jar"]