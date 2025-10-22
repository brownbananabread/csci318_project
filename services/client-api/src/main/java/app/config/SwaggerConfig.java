package app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for Client API Gateway.
 * Provides interactive API documentation at /swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI clientApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Management Platform - Complete API Documentation")
                        .description("""
                                ## Event Management Microservices Platform
                                ### Centralized API Documentation (All Services on Port 8080)

                                **This is the unified API documentation for the entire platform.**
                                All microservice endpoints are accessible through this gateway on port 8080.

                                **Total Endpoints: 24** across 5 functional areas (Users, Events, Analytics, Activity, Personalise)

                                ### Features
                                - **User Management** (Registration, Authentication, Profile)
                                - **Event Management** (CRUD, Registration, Discovery)
                                - **Stream Analytics** (Real-time Kafka queries for trending events, capacity monitoring)
                                - **Activity Logging** (Audit Trail)
                                - **AI Personalization** (Event Summaries, Recommendations, Chat, My Events)

                                ### Microservices Architecture
                                This gateway consolidates and proxies requests to 4 backend microservices:
                                - **User API** (Internal Port 8081) - User authentication and account management
                                - **Event API** (Internal Port 8082) - Event CRUD operations + Kafka Stream Analytics
                                - **Activity API** (Internal Port 8083) - Activity logging and audit trail
                                - **Personalise API** (Internal Port 8084) - AI-powered personalization with LangChain4j

                                **All endpoints below are accessible on Port 8080** - You don't need to access the internal services directly.

                                ### Event-Driven Architecture & Stream Analytics
                                The platform uses Apache Kafka for asynchronous messaging with:
                                - 4 domain events (user-created, event-created, user-registered-event, event-capacity-reached)
                                - 3 stream processors (trending events, capacity monitoring, event analytics)
                                - 7 real-time query endpoints (accessible via `/analytics/*` endpoints)

                                ### API Endpoint Groups
                                **Users** - `/users/*`
                                - User signup, login, account management
                                - Authentication endpoints

                                **Events** - `/events/*`
                                - Event creation, discovery, registration
                                - CRUD operations for event management

                                **Stream Analytics** - `/analytics/*`
                                - Real-time Kafka stream processing queries
                                - Trending events, capacity monitoring, platform statistics

                                **Activity** - `/activity/*`
                                - User activity logs and audit trail

                                **Personalise** - `/personalise/*`
                                - AI-powered event summaries and recommendations
                                - Interactive chat assistant
                                - Personalized event discovery

                                ### Authentication
                                Use the user ID returned from `/users/signup` or `/users/login` as the Authorization header value.

                                **Example:** `Authorization: 1`

                                ### Agentic AI Features (via /personalise endpoints)
                                The platform includes LangChain4j-powered AI agents with:
                                - `/personalise/summary` - AI-generated event summaries
                                - `/personalise/chat` - Interactive AI assistant
                                - `/personalise/my-events` - User's registered events
                                - `/personalise/recommended-events` - Personalized event recommendations

                                Features autonomous tool selection, multi-step reasoning, and context-aware recommendations.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CSCI318 Group Project")
                                .email("csci318@university.edu"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
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
