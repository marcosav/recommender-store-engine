FROM openjdk:8-jdk-slim@sha256:b4f663e0f9ac8f89fb46c82687c10aa7618295b5c0a86516f6b0341feab556b6 AS build
COPY . /source
WORKDIR /source
RUN ./gradlew shadowJar

FROM openjdk:8-jre-slim@sha256:5563c7e505fa828bd868ae99f24c5a56bb0bd5488a10184f7175d10f167b0898
COPY --from=build /source/build/libs/*.jar /app/recommender-store-engine.jar
WORKDIR /app
CMD "java" "-jar" "recommender-store-engine.jar"