package com.example.bankcards.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(@org.springframework.beans.factory.annotation.Value("classpath:static/openapi.yml") Resource openapiResource) throws Exception {
        OpenAPIV3Parser parser = new OpenAPIV3Parser();
        return parser.read(openapiResource.getURL().toString());
    }

    @Bean
    public GroupedOpenApi customApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .build();
    }
}