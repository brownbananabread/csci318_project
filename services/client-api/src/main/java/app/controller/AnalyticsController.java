package app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Client API Gateway controller for Stream Analytics endpoints.
 * Proxies requests to the Event API's Kafka stream processing queries.
 */
@RestController
@RequestMapping("/analytics")
@Tag(name = "Stream Analytics", description = "Real-time Kafka stream processing queries for event analytics")
public class AnalyticsController {

    private final WebClient eventApiClient;

    public AnalyticsController(WebClient.Builder webClientBuilder) {
        this.eventApiClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    /**
     * Get currently trending events based on 5-minute sliding window.
     */
    @Operation(
        summary = "Get trending events (Kafka Stream Query)",
        description = """
            Returns currently trending events based on real-time Kafka stream processing.

            **Stream Processing Logic:**
            - Consumes `user-registered-event` Kafka topic
            - Maintains 5-minute sliding window per event
            - Events with ≥3 registrations flagged as trending
            - Real-time computation (no database queries)

            **Use Case:** Display trending events badge/section on homepage
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved trending events",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "trendingEvents": {
                        "550e8400-e29b-41d4-a716-446655440000": 5,
                        "660e8400-e29b-41d4-a716-446655440001": 4
                      },
                      "windowMinutes": 5,
                      "threshold": 3,
                      "timestamp": 1698765432000,
                      "description": "Events with 3+ registrations in last 5 minutes"
                    }
                    """)))
    })
    @GetMapping("/trending-events")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getTrendingEvents() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/trending-events")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }

    /**
     * Get real-time capacity status for all events.
     */
    @Operation(
        summary = "Get capacity status for all events",
        description = """
            Returns real-time capacity utilization computed from Kafka streams.

            **Stream Processing:**
            - Tracks registrations per event in real-time
            - Computes capacity percentage: (registrations / maxAttendees) * 100
            - Provides threshold levels: 75% warning, 90% critical, 100% full

            **Use Case:** Dashboard monitoring, capacity planning
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved capacity status",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "capacityStatus": {
                        "550e8400-e29b-41d4-a716-446655440000": 85.5,
                        "660e8400-e29b-41d4-a716-446655440001": 45.0
                      },
                      "thresholds": {
                        "warning": 75.0,
                        "critical": 90.0,
                        "full": 100.0
                      },
                      "timestamp": 1698765432000,
                      "description": "Real-time capacity utilization for all events"
                    }
                    """)))
    })
    @GetMapping("/capacity-status")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getCapacityStatus() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/capacity-status")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }

    /**
     * Get events approaching capacity (≥75% full).
     */
    @Operation(
        summary = "Get capacity alerts for events at risk",
        description = """
            Returns events at or above 75% capacity utilization.

            **Alert Levels:**
            - Warning: 75-89% capacity
            - Critical: 90-99% capacity
            - Full: 100% capacity

            **Use Case:** Proactive notifications, capacity management
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved capacity alerts",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "alerts": {
                        "550e8400-e29b-41d4-a716-446655440000": 85.5,
                        "660e8400-e29b-41d4-a716-446655440001": 92.0
                      },
                      "alertCount": 2,
                      "timestamp": 1698765432000,
                      "description": "Events at or above 75% capacity"
                    }
                    """)))
    })
    @GetMapping("/capacity-alerts")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getCapacityAlerts() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/capacity-alerts")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }

    /**
     * Get detailed analytics for all events.
     */
    @Operation(
        summary = "Get per-event analytics",
        description = """
            Returns real-time computed statistics per event:
            - Total registrations
            - Registration velocity (registrations/minute)
            - Event status (fully booked or not)

            **Use Case:** Event performance dashboard, organizer insights
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved event analytics",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "eventStats": {
                        "550e8400-e29b-41d4-a716-446655440000": {
                          "totalRegistrations": 42,
                          "registrationVelocity": 2.5,
                          "isFullyBooked": false
                        }
                      },
                      "eventCount": 1,
                      "timestamp": 1698765432000,
                      "description": "Real-time analytics per event"
                    }
                    """)))
    })
    @GetMapping("/event-stats")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getEventAnalytics() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/event-stats")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }

    /**
     * Get platform-wide analytics summary.
     */
    @Operation(
        summary = "Get global platform statistics",
        description = """
            Returns platform-wide statistics computed in real-time:
            - Total events created
            - Total registrations across all events
            - Average registrations per event

            **Use Case:** Platform health monitoring, KPI tracking
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved global analytics",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "totalEvents": 150,
                      "totalRegistrations": 3240,
                      "averageRegistrationsPerEvent": 21.6,
                      "timestamp": 1698765432000,
                      "description": "Platform-wide real-time statistics"
                    }
                    """)))
    })
    @GetMapping("/global-stats")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getGlobalAnalytics() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/global-stats")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }

    /**
     * Get most popular events by registration velocity.
     */
    @Operation(
        summary = "Get popular events by registration rate",
        description = """
            Returns events sorted by registration velocity (registrations per minute).

            **Use Case:** Identify "hot" events with rapid registration activity
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved popular events",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "popularEvents": {
                        "550e8400-e29b-41d4-a716-446655440000": {
                          "registrationVelocity": 5.2,
                          "totalRegistrations": 156
                        }
                      },
                      "sortedBy": "registrationVelocity",
                      "timestamp": 1698765432000,
                      "description": "Events sorted by registration rate"
                    }
                    """)))
    })
    @GetMapping("/popular-events")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getPopularEvents() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/popular-events")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }

    /**
     * Health check for stream processors.
     */
    @Operation(
        summary = "Stream processing health check",
        description = """
            Returns status of all Kafka stream processors:
            - TrendingEventsProcessor
            - CapacityMonitoringProcessor
            - EventAnalyticsProcessor

            **Use Case:** System monitoring, health checks
            """
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved health status",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {
                      "status": "UP",
                      "processors": {
                        "trendingEventsProcessor": "ACTIVE",
                        "capacityMonitoringProcessor": "ACTIVE",
                        "eventAnalyticsProcessor": "ACTIVE"
                      },
                      "timestamp": 1698765432000
                    }
                    """)))
    })
    @GetMapping("/health")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Map<String, Object>> getStreamProcessingHealth() {
        Map<String, Object> response = eventApiClient.get()
                .uri("/api/v1/analytics/health")
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        return ResponseEntity.ok(response);
    }
}
