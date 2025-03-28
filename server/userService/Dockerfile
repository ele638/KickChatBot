FROM openjdk:20-jdk-slim
LABEL authors="ele63"
WORKDIR /app
COPY build/libs/userService-all.jar user-service.jar
EXPOSE 80
CMD ["java", "-jar", "user-service.jar"]