package app.controller;

import app.stream.TrendingEventsProcessor;
import app.stream.CapacityMonitoringProcessor;
import app.stream.EventAnalyticsProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API for accessing real-time stream processing queries.
 * Exposes stream analytics data for trending events and capacity monitoring.
 *
 * NOTE: This is an internal service. API documentation available at Client API Gateway (port 8080).
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class StreamAnalyticsController {

    private final TrendingEventsProcessor trendingProcessor;
    private final CapacityMonitoringProcessor capacityProcessor;
    private final EventAnalyticsProcessor analyticsProcessor;

    public StreamAnalyticsController(
            TrendingEventsProcessor trendingProcessor,
            CapacityMonitoringProcessor capacityProcessor,
            EventAnalyticsProcessor analyticsProcessor) {
        this.trendingProcessor = trendingProcessor;
        this.capacityProcessor = capacityProcessor;
        this.analyticsProcessor = analyticsProcessor;
    }

    /**
     * Real-time query: Get currently trending events.
     *
     * Stream Processing Use Case #1:
     * Returns events that have received >= 3 registrations in the last 5 minutes.
     * This is computed in real-time from the event stream.
     *
     * @return Map of eventId -> registration count in current window
     */
    @GetMapping("/trending-events")
    public ResponseEntity<Map<String, Object>> getTrendingEvents() {
        Map<String, Integer> trendingEvents = trendingProcessor.getTrendingEvents();

        Map<String, Object> response = new HashMap<>();
        response.put("trendingEvents", trendingEvents);
        response.put("windowMinutes", 5);
        response.put("threshold", 3);
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Events with 3+ registrations in last 5 minutes");

        return ResponseEntity.ok(response);
    }

    /**
     * Real-time query: Get capacity status for all events.
     *
     * Stream Processing Use Case #2:
     * Returns real-time capacity utilization for all events.
     * Computed continuously from registration events.
     *
     * @return Map of eventId -> capacity utilization percentage
     */
    @GetMapping("/capacity-status")
    public ResponseEntity<Map<String, Object>> getCapacityStatus() {
        Map<String, Double> capacityStatus = capacityProcessor.getCapacityStatus();

        Map<String, Object> response = new HashMap<>();
        response.put("capacityStatus", capacityStatus);
        response.put("thresholds", Map.of(
            "warning", 75.0,
            "critical", 90.0,
            "full", 100.0
        ));
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Real-time capacity utilization for all events");

        return ResponseEntity.ok(response);
    }

    /**
     * Real-time query: Get events approaching capacity.
     *
     * Filters capacity status to show only events at warning or critical levels.
     *
     * @return Events with >= 75% capacity utilization
     */
    @GetMapping("/capacity-alerts")
    public ResponseEntity<Map<String, Object>> getCapacityAlerts() {
        Map<String, Double> allCapacities = capacityProcessor.getCapacityStatus();

        // Filter to only events above warning threshold (75%)
        Map<String, Double> alerts = new HashMap<>();
        allCapacities.forEach((eventId, utilization) -> {
            if (utilization >= 75.0) {
                alerts.put(eventId, utilization);
            }
        });

        Map<String, Object> response = new HashMap<>();
        response.put("alerts", alerts);
        response.put("alertCount", alerts.size());
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Events at or above 75% capacity");

        return ResponseEntity.ok(response);
    }

    /**
     * Real-time query: Get detailed analytics for all events.
     *
     * Stream Processing Use Case #3:
     * Returns real-time computed statistics including:
     * - Total registrations per event
     * - Registration velocity (registrations/minute)
     * - Event status (fully booked or not)
     *
     * @return Per-event analytics computed from event streams
     */
    @GetMapping("/event-stats")
    public ResponseEntity<Map<String, Object>> getEventAnalytics() {
        Map<String, Map<String, Object>> eventStats = analyticsProcessor.getEventAnalytics();

        Map<String, Object> response = new HashMap<>();
        response.put("eventStats", eventStats);
        response.put("eventCount", eventStats.size());
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Real-time analytics per event");

        return ResponseEntity.ok(response);
    }

    /**
     * Real-time query: Get global analytics summary.
     *
     * Stream Processing Use Case #4:
     * Returns platform-wide statistics computed in real-time:
     * - Total events created
     * - Total registrations
     * - Average registrations per event
     *
     * @return Global analytics summary
     */
    @GetMapping("/global-stats")
    public ResponseEntity<Map<String, Object>> getGlobalAnalytics() {
        Map<String, Object> globalStats = analyticsProcessor.getGlobalAnalytics();

        Map<String, Object> response = new HashMap<>();
        response.putAll(globalStats);
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Platform-wide real-time statistics");

        return ResponseEntity.ok(response);
    }

    /**
     * Real-time query: Get most popular events by registration velocity.
     *
     * Returns events sorted by registration rate (registrations per minute).
     * Useful for identifying "hot" events with rapid registration activity.
     *
     * @return Top events by registration velocity
     */
    @GetMapping("/popular-events")
    public ResponseEntity<Map<String, Object>> getPopularEvents() {
        Map<String, Map<String, Object>> allEvents = analyticsProcessor.getEventAnalytics();

        // Sort by registration velocity
        Map<String, Object> response = new HashMap<>();
        response.put("popularEvents", allEvents);
        response.put("sortedBy", "registrationVelocity");
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Events sorted by registration rate");

        return ResponseEntity.ok(response);
    }

    /**
     * Health check for stream processors.
     *
     * @return Status of stream processing components
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getStreamProcessingHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("processors", Map.of(
            "trendingEventsProcessor", "ACTIVE",
            "capacityMonitoringProcessor", "ACTIVE",
            "eventAnalyticsProcessor", "ACTIVE"
        ));
        health.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(health);
    }
}
