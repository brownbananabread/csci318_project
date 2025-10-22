package app.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Real-time event analytics stream processor.
 * Computes aggregated statistics from event streams for analytics dashboards.
 *
 * Stream Processing Use Case #3:
 * - Total registrations per event (all-time)
 * - Registration velocity (registrations per minute)
 * - Most active events by registration rate
 */
@Component
public class EventAnalyticsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EventAnalyticsProcessor.class);

    private final ObjectMapper objectMapper;

    // Real-time analytics state
    private final Map<String, EventAnalytics> eventAnalytics = new ConcurrentHashMap<>();
    private final AtomicInteger totalRegistrations = new AtomicInteger(0);
    private final AtomicInteger totalEventsCreated = new AtomicInteger(0);

    public EventAnalyticsProcessor() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Processes event creation events to track total events created.
     */
    @KafkaListener(topics = "event-created", groupId = "analytics-processor-group")
    public void processEventCreated(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("title").asText();
            String location = event.get("location").asText();

            // Initialize analytics for this event
            EventAnalytics analytics = new EventAnalytics(eventId, eventTitle, location);
            eventAnalytics.put(eventId, analytics);

            totalEventsCreated.incrementAndGet();

            logger.info("Analytics: Event created - {} (Total events: {})",
                eventTitle, totalEventsCreated.get());

        } catch (Exception e) {
            logger.error("Error processing event creation for analytics: {}", message, e);
        }
    }

    /**
     * Processes registration events to compute real-time statistics.
     */
    @KafkaListener(topics = "user-registered-event", groupId = "analytics-processor-group")
    public void processRegistration(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();
            OffsetDateTime timestamp = OffsetDateTime.now();

            // Update event-specific analytics
            EventAnalytics analytics = eventAnalytics.computeIfAbsent(
                eventId,
                id -> new EventAnalytics(id, eventTitle, "Unknown")
            );

            analytics.addRegistration(timestamp);
            totalRegistrations.incrementAndGet();

            double velocity = analytics.getRegistrationVelocity();

            logger.info("Analytics: Registration for '{}' - Total: {}, Velocity: {}/min",
                eventTitle, analytics.getTotalRegistrations(), String.format("%.2f", velocity));

        } catch (Exception e) {
            logger.error("Error processing registration for analytics: {}", message, e);
        }
    }

    /**
     * Processes capacity reached events.
     */
    @KafkaListener(topics = "event-capacity-reached", groupId = "analytics-processor-group")
    public void processCapacityReached(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();

            EventAnalytics analytics = eventAnalytics.get(eventId);
            if (analytics != null) {
                analytics.setFullyBooked(true);
                logger.info("Analytics: Event '{}' is now fully booked", eventTitle);
            }

        } catch (Exception e) {
            logger.error("Error processing capacity reached for analytics: {}", message, e);
        }
    }

    /**
     * Real-time analytics for a single event.
     */
    private static class EventAnalytics {
        private final String eventId;
        private final String eventTitle;
        private final String location;
        private final AtomicInteger totalRegistrations = new AtomicInteger(0);
        private final ConcurrentHashMap<Long, Integer> registrationsByMinute = new ConcurrentHashMap<>();
        private OffsetDateTime firstRegistration;
        private OffsetDateTime lastRegistration;
        private boolean fullyBooked = false;

        public EventAnalytics(String eventId, String eventTitle, String location) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
            this.location = location;
        }

        public void addRegistration(OffsetDateTime timestamp) {
            if (firstRegistration == null) {
                firstRegistration = timestamp;
            }
            lastRegistration = timestamp;

            totalRegistrations.incrementAndGet();

            // Track registrations per minute
            long minuteKey = timestamp.toEpochSecond() / 60;
            registrationsByMinute.merge(minuteKey, 1, Integer::sum);
        }

        public int getTotalRegistrations() {
            return totalRegistrations.get();
        }

        public double getRegistrationVelocity() {
            if (firstRegistration == null || lastRegistration == null) {
                return 0.0;
            }

            long elapsedSeconds = java.time.Duration.between(firstRegistration, lastRegistration).getSeconds();
            if (elapsedSeconds == 0) {
                return totalRegistrations.get();
            }

            double minutes = elapsedSeconds / 60.0;
            return totalRegistrations.get() / Math.max(minutes, 1.0);
        }

        public String getEventTitle() {
            return eventTitle;
        }

        public String getLocation() {
            return location;
        }

        public boolean isFullyBooked() {
            return fullyBooked;
        }

        public void setFullyBooked(boolean fullyBooked) {
            this.fullyBooked = fullyBooked;
        }
    }

    /**
     * Returns real-time analytics for all events.
     */
    public Map<String, Map<String, Object>> getEventAnalytics() {
        Map<String, Map<String, Object>> result = new ConcurrentHashMap<>();

        eventAnalytics.forEach((eventId, analytics) -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("eventTitle", analytics.getEventTitle());
            stats.put("location", analytics.getLocation());
            stats.put("totalRegistrations", analytics.getTotalRegistrations());
            stats.put("registrationVelocity", analytics.getRegistrationVelocity());
            stats.put("fullyBooked", analytics.isFullyBooked());

            result.put(eventId, stats);
        });

        return result;
    }

    /**
     * Returns global analytics summary.
     */
    public Map<String, Object> getGlobalAnalytics() {
        Map<String, Object> summary = new ConcurrentHashMap<>();
        summary.put("totalEventsCreated", totalEventsCreated.get());
        summary.put("totalRegistrations", totalRegistrations.get());
        summary.put("activeEvents", eventAnalytics.size());

        // Calculate average registrations per event
        double avgRegistrations = eventAnalytics.isEmpty() ? 0.0 :
            totalRegistrations.get() / (double) eventAnalytics.size();
        summary.put("avgRegistrationsPerEvent", avgRegistrations);

        return summary;
    }
}
