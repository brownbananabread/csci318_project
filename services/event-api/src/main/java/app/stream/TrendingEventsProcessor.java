package app.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Stream processor that tracks trending events in real-time.
 * Analyzes event registration patterns to identify popular events.
 *
 * This demonstrates stream processing capabilities for event-driven architecture:
 * - Real-time windowed aggregation (5-minute windows)
 * - Event popularity scoring
 * - Trending event detection
 */
@Component
public class TrendingEventsProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TrendingEventsProcessor.class);
    private static final int TRENDING_THRESHOLD = 3; // registrations in window
    private static final int WINDOW_MINUTES = 5;

    private final ObjectMapper objectMapper;

    // In-memory store for windowed counts (in production, use Kafka Streams with state stores)
    private final Map<String, EventWindow> eventWindows = new ConcurrentHashMap<>();

    public TrendingEventsProcessor() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Processes user registration events to detect trending events.
     * Uses a 5-minute sliding window to count registrations per event.
     */
    @KafkaListener(topics = "user-registered-event", groupId = "trending-processor-group")
    public void processRegistration(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();
            OffsetDateTime timestamp = OffsetDateTime.now();

            // Get or create event window
            EventWindow window = eventWindows.computeIfAbsent(
                eventId,
                id -> new EventWindow(id, eventTitle)
            );

            // Clean old registrations outside window
            window.removeOldRegistrations(WINDOW_MINUTES);

            // Add new registration
            window.addRegistration(timestamp);

            int count = window.getCount();
            logger.info("Trending Analysis - Event: {} has {} registrations in last {} minutes",
                eventTitle, count, WINDOW_MINUTES);

            // Detect trending events
            if (count >= TRENDING_THRESHOLD && !window.isTrending()) {
                window.setTrending(true);
                logger.warn("TRENDING EVENT DETECTED: '{}' with {} registrations in {} minutes!",
                    eventTitle, count, WINDOW_MINUTES);

                // In production: publish TrendingEventDetectedEvent to Kafka
                // kafkaTemplate.send("trending-events", eventId, new TrendingEventDetectedEvent(...));
            } else if (count < TRENDING_THRESHOLD && window.isTrending()) {
                window.setTrending(false);
                logger.info("Event '{}' is no longer trending", eventTitle);
            }

        } catch (Exception e) {
            logger.error("Error processing registration for trending analysis: {}", message, e);
        }
    }

    /**
     * Represents a time window for tracking event registrations.
     */
    private static class EventWindow {
        private final String eventId;
        private final String eventTitle;
        private final ConcurrentHashMap<String, OffsetDateTime> registrations = new ConcurrentHashMap<>();
        private final AtomicInteger count = new AtomicInteger(0);
        private volatile boolean trending = false;

        public EventWindow(String eventId, String eventTitle) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
        }

        public void addRegistration(OffsetDateTime timestamp) {
            String key = timestamp.toString();
            registrations.put(key, timestamp);
            count.incrementAndGet();
        }

        public void removeOldRegistrations(int windowMinutes) {
            OffsetDateTime cutoff = OffsetDateTime.now().minus(windowMinutes, ChronoUnit.MINUTES);
            registrations.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
            count.set(registrations.size());
        }

        public int getCount() {
            return count.get();
        }

        public boolean isTrending() {
            return trending;
        }

        public void setTrending(boolean trending) {
            this.trending = trending;
        }
    }

    /**
     * Returns current trending events (for API endpoint exposure).
     */
    public Map<String, Integer> getTrendingEvents() {
        Map<String, Integer> trending = new ConcurrentHashMap<>();
        eventWindows.forEach((eventId, window) -> {
            if (window.isTrending()) {
                trending.put(eventId, window.getCount());
            }
        });
        return trending;
    }
}
