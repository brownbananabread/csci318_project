package app.utils;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI clientApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Management Platform - Complete API Documentation")
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Client API Gateway (Development)")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("User ID")
                                .description("User ID obtained from signup or login")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
