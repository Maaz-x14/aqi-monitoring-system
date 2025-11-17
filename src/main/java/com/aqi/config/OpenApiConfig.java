package com.aqi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // 1. Add the "Authorize" button (globally)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))

                // 2. Define the security scheme
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // Type is HTTP
                                .scheme("bearer")               // Scheme is "bearer"
                                .bearerFormat("JWT")            // Format is "JWT"
                        )
                )

                // 3. Add API info (optional, but good practice)
                .info(new Info()
                        .title("AQI Monitoring System API")
                        .version("v1.0")
                        .description("API documentation for the Intelligent Air Quality Index Monitoring System.")
                );
    }
}