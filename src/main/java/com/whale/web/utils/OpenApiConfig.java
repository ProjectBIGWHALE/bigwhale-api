package com.whale.web.utils;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Big Whale API")
                        .description("API developed for manipulating images and documents for free.")
                        .contact(new Contact().name("Whalers team").email("mybigwhale@gmail.com").url("https://mybigwhale.com/"))
                        .version("1.0.0"))
                .externalDocs(new ExternalDocumentation().description("Documentation")
                        .url("https://pastoral-thorn-cf3.notion.site/WHALE-0227a8c241084a1998fa7bb8bc01e35b?pvs=4"));
    }

    @Bean
    public GroupedOpenApi testApi() {
        return GroupedOpenApi.builder().group("api-v1").pathsToMatch("/api/v1/**").build();
    }
}
