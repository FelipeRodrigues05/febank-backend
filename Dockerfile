# ─── Development stage with hot reload ───────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS dev

WORKDIR /app

# Copy Maven wrapper and pom.xml first to cache dependency resolution layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:resolve -q

# Source code is mounted at runtime via volume — no COPY src here
EXPOSE 8000

# Spring Boot DevTools monitors classpath changes and triggers restart automatically.
# The source volume must be mounted at /app/src when running.
CMD ["./mvnw", "spring-boot:run", \
     "-Dspring-boot.run.jvmArguments=-Dspring.devtools.restart.enabled=true -Dspring.devtools.livereload.enabled=true"]


# ─── Production build stage ───────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:resolve -q

COPY src ./src
RUN ./mvnw package -DskipTests -q


# ─── Production runtime stage ─────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy AS prod

WORKDIR /app

RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
USER appuser

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]
