FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
#COPY keys ./keys
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 --ingroup spring springboot

COPY --from=builder --chown=springboot:spring /app/target/*.jar app.jar
#COPY --from=builder --chown=springboot:spring /app/keys ./keys

USER springboot

EXPOSE 8080

#HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
