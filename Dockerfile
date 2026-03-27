# =============================================================
# Dockerfile com build multi-estágio
# Estágio 1 (builder): compila o projeto com Maven
# Estágio 2 (runtime): imagem mínima apenas com o JAR final
# =============================================================

FROM maven:3.8.6-eclipse-temurin-8 AS builder

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B


FROM eclipse-temurin:8-jre-alpine

LABEL maintainer="Lucas Domingues <lukasdng@gmail.com>"
LABEL description="API REST para gerenciamento de Pessoas Físicas e Jurídicas"

# Usuário não-root por segurança
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /build/target/pessoas-api-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", "app.jar"]
