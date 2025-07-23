# Fase 1: Build com Maven
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

# Compilar sem rodar os testes
RUN mvn clean install -DskipTests

# Fase 2: Imagem final para rodar a aplicação
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar o JAR compilado
COPY --from=build /app/target/*.jar /app/app.jar

# ✅ Copiar o arquivo de credenciais do Firebase para o container
COPY producteve-65ffb-firebase-adminsdk-fbsvc-2469a53581.json /app/firebase.json

# ✅ Definir a variável de ambiente para o Firebase usar o arquivo JSON
ENV GOOGLE_APPLICATION_CREDENTIALS=/app/firebase.json

# Expor a porta padrão do Spring Boot
EXPOSE 8080

# Rodar o app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
