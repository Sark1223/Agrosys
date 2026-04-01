# --- Etapa 1: Construcción (Cambiado a JDK 21) ---
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# 1. Copiamos los archivos del Maven Wrapper
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# 2. Descargamos dependencias (aprovechando el caché de capas)
RUN ./mvnw dependency:go-offline -B

# 3. Compilamos el proyecto
COPY src ./src
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Ejecución (Cambiado a JRE 21) ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el jar generado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Configuración de seguridad: Correr como usuario no root
RUN groupadd -r agrosys && useradd -r -g agrosys agrosys
USER agrosys

EXPOSE 8083

# Mantenemos ZGC, que funciona excelente en Java 21 para microservicios
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]