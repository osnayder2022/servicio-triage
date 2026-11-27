# Etapa de compilacion: el contexto de construccion es la raiz del proyecto.
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -DskipTests dependency:go-offline

COPY src/ src/
RUN ./mvnw -B -DskipTests package

# Etapa de ejecucion: imagen pequena sin herramientas de compilacion.
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S triage && adduser -S triage -G triage
COPY --from=build /workspace/target/*.jar app.jar
RUN chown triage:triage app.jar

USER triage
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
