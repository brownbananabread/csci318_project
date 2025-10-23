package app.controller;

import app.stream.TrendingEventsProcessor;
import app.stream.CapacityMonitoringProcessor;
import app.stream.EventAnalyticsProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/global-stats")
    public ResponseEntity<Map<String, Object>> getGlobalAnalytics() {
        Map<String, Object> globalStats = analyticsProcessor.getGlobalAnalytics();

        Map<String, Object> response = new HashMap<>();
        response.putAll(globalStats);
        response.put("timestamp", System.currentTimeMillis());
        response.put("description", "Platform-wide real-time statistics");

        return ResponseEntity.ok(response);
    }

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
