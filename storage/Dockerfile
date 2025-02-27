FROM gradle:8.5 AS builder

WORKDIR /app

COPY . /app

RUN chmod +x ./gradlew

RUN /app/gradlew clean build

FROM openjdk:20

COPY --from=builder /app/build/libs/storage-0.0.1-SNAPSHOT.jar /app/storage.jar

CMD ["java", "-jar", "/app/storage.jar"]