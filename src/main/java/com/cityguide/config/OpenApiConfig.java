package com.cityguide.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cityGuideOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("City Guide API")
                        .description("REST API путеводителя по городу — достопримечательности, оценки и отзывы")
                        .version("1.0.0")
                        .contact(new Contact().name("City Guide Team")));
    }
}
