FROM maven:3.9.16-eclipse-temurin-26-noble AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:26-jre-noble AS runtime
RUN groupadd --system appgroup && useradd --system --gid appgroup --home-dir /app appuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER appuser
ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]