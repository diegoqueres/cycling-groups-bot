# Build Stage
FROM eclipse-temurin:17.0.7_7-jdk AS build

WORKDIR /app

COPY gradle/wrapper/gradle-wrapper.properties /app/gradle/wrapper/gradle-wrapper.properties
COPY gradlew /app/gradlew
RUN chmod +x /app/gradlew
COPY gradle/wrapper/gradle-wrapper.jar /app/gradle/wrapper/gradle-wrapper.jar

COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN ./gradlew build -x test

# Final Stage
FROM eclipse-temurin:17.0.7_7-jre AS final

WORKDIR /app

COPY --from=build /app/build/libs/cycling-groups-bot-0.0.1-SNAPSHOT.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE ""
ENV SPRING_DATA_MONGODB_URI ""
ENV SPRING_DATA_MONGODB_DATABASE ""
ENV SPRING_DATA_MONGODB_AUTO_INDEX ""
ENV TELEGRAMBOT_USERNAME ""
ENV TELEGRAMBOT_TOKEN ""
ENV TELEGRAMBOT_CREATORID ""
ENV APP_FINDGROUPS_RESULTS_DISTANCE ""

CMD ["java", "-jar", "app.jar"]