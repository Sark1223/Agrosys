# agrosys-web/Dockerfile
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Descargamos dependencias
RUN ./mvnw dependency:go-offline -B

# Compilamos el proyecto
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN groupadd -r agrosys && useradd -r -g agrosys agrosys
USER agrosys

EXPOSE 8081

ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]