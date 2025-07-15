FROM openjdk:21-jdk AS build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw dependency:go-offline
RUN ./mvnw clean package -D maven.test.skip=true
RUN mv target/*.jar /app.jar

FROM openjdk:21-jdk
COPY --from=build /app.jar .
ENTRYPOINT ["java", "-jar", "app.jar"]