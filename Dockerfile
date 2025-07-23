# Usar uma imagem Maven para o build (já tem o JDK e Maven configurados)
FROM maven:3.9.5-eclipse-temurin-21 AS build

# Definir o diretório de trabalho
WORKDIR /app

# Copiar os arquivos da aplicação para o contêiner
COPY . .

# Rodar o Maven para compilar a aplicação
RUN mvn clean install -DskipTests
# Usar uma imagem base do OpenJDK para rodar a aplicação
FROM openjdk:17-jdk-slim

# Definir o diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado do estágio de build para o contêiner
COPY --from=build /app/target/*.jar /app/app.jar

# Expor a porta onde a aplicação Spring Boot vai rodar
EXPOSE 8080

# Rodar o arquivo JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
