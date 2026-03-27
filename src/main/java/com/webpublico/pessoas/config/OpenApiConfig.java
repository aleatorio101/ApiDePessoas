package com.webpublico.pessoas.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pessoas API")
                        .description(
                                "API REST para gerenciamento de Pessoas Físicas e Jurídicas. " +
                                        "Implementada seguindo o **Nível 2 do Modelo de Maturidade de Richardson**: " +
                                        "recursos identificados por URI, verbos HTTP semânticos e " +
                                        "códigos de status precisos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Lucas Domingues")
                                .email("lukasdng@gmail.com"))
                        .license(new License()
                                .name("MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente local")
                ));
    }
}