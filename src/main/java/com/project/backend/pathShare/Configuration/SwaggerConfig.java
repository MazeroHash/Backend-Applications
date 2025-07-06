package com.project.backend.pathShare.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI rideSharingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ride-Sharing Backend API")
                        .description("APIs for real-time ride assignment, pricing, and driver tracking")
                        .version("v1.0.0")
                        .contact(new Contact().name("Pankaj Yadav").email("pankajrazz228@gmail.com")));
    }
}
