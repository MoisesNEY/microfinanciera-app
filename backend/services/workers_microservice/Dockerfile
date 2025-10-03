# ==== Build ====
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Compila SOLO el módulo del workers
RUN mvn -q -DskipTests -pl backend/services/workers_microservice -am clean package
# ==== Run ====
FROM eclipse-temurin:21-jre
WORKDIR /app
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0"
# Copia el jar desde la carpeta del módulo
COPY --from=build /app/backend/services/workers_microservice/target/*.jar app.jar
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
