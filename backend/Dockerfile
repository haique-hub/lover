FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/lovers-backend-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
