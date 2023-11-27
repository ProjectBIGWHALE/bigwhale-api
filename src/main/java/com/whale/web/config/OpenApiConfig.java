package com.whale.web.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Big Whale API")
                        .description("API developed for manipulating images and documents.")
                        .contact(new Contact().name("Whalers team").email("mybigwhale@gmail.com"))
                        .version("1.0.0"));

    }

    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder().group("api-v1").pathsToMatch("/api/v1/**").build();
    }
}
