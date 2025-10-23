package app.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CapacityMonitoringProcessor {
    private static final Logger logger = LoggerFactory.getLogger(CapacityMonitoringProcessor.class);

    private static final double WARNING_THRESHOLD = 0.75;  // 75% capacity
    private static final double CRITICAL_THRESHOLD = 0.90; // 90% capacity

    private final ObjectMapper objectMapper;

    // In-memory store for capacity tracking (in production, use Kafka Streams state stores)
    private final Map<String, EventCapacity> eventCapacities = new ConcurrentHashMap<>();

    public CapacityMonitoringProcessor() {
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "user-registered-event", groupId = "capacity-monitor-group")
    public void monitorCapacity(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();
            int currentParticipants = event.get("currentParticipants").asInt();
            int maxParticipants = event.get("maxParticipants").asInt();

            // Update capacity tracking
            EventCapacity capacity = eventCapacities.computeIfAbsent(
                eventId,
                id -> new EventCapacity(id, eventTitle, maxParticipants)
            );

            capacity.updateCurrentParticipants(currentParticipants);

            double utilizationPercent = capacity.getUtilizationPercent();

            logger.info("Capacity Monitor - Event: '{}' at {}/{} capacity ({}%)",
                eventTitle, currentParticipants, maxParticipants, String.format("%.1f", utilizationPercent));

            // Check thresholds and issue alerts
            if (utilizationPercent >= 100.0 && !capacity.isFullAlertSent()) {
                logger.error("CAPACITY ALERT [FULL]: Event '{}' has reached MAXIMUM capacity!",
                    eventTitle);
                capacity.setFullAlertSent(true);

            } else if (utilizationPercent >= CRITICAL_THRESHOLD * 100
                    && !capacity.isCriticalAlertSent()) {
                logger.warn("CAPACITY ALERT [CRITICAL]: Event '{}' at {}% capacity (threshold: {}%)",
                    eventTitle, String.format("%.1f", utilizationPercent), CRITICAL_THRESHOLD * 100);
                capacity.setCriticalAlertSent(true);

            } else if (utilizationPercent >= WARNING_THRESHOLD * 100
                    && !capacity.isWarningAlertSent()) {
                logger.info("CAPACITY ALERT [WARNING]: Event '{}' at {}% capacity (threshold: {}%)",
                    eventTitle, String.format("%.1f", utilizationPercent), WARNING_THRESHOLD * 100);
                capacity.setWarningAlertSent(true);
            }

        } catch (Exception e) {
            logger.error("Error monitoring capacity: {}", message, e);
        }
    }

    @KafkaListener(topics = "event-capacity-reached", groupId = "capacity-monitor-group")
    public void handleCapacityReached(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();

            logger.error("Event '{}' has reached maximum capacity - Registration closed", eventTitle);

        } catch (Exception e) {
            logger.error("Error handling capacity reached event: {}", message, e);
        }
    }

    private static class EventCapacity {
        private final String eventId;
        private final String eventTitle;
        private final int maxParticipants;
        private int currentParticipants;

        private boolean warningAlertSent = false;
        private boolean criticalAlertSent = false;
        private boolean fullAlertSent = false;

        public EventCapacity(String eventId, String eventTitle, int maxParticipants) {
            this.eventId = eventId;
            this.eventTitle = eventTitle;
            this.maxParticipants = maxParticipants;
            this.currentParticipants = 0;
        }

        public void updateCurrentParticipants(int current) {
            this.currentParticipants = current;
        }

        public double getUtilizationPercent() {
            if (maxParticipants == 0) return 0.0;
            return (currentParticipants * 100.0) / maxParticipants;
        }

        public boolean isWarningAlertSent() {
            return warningAlertSent;
        }

        public void setWarningAlertSent(boolean sent) {
            this.warningAlertSent = sent;
        }

        public boolean isCriticalAlertSent() {
            return criticalAlertSent;
        }

        public void setCriticalAlertSent(boolean sent) {
            this.criticalAlertSent = sent;
        }

        public boolean isFullAlertSent() {
            return fullAlertSent;
        }

        public void setFullAlertSent(boolean sent) {
            this.fullAlertSent = sent;
        }
    }

    public Map<String, Double> getCapacityStatus() {
        Map<String, Double> status = new ConcurrentHashMap<>();
        eventCapacities.forEach((eventId, capacity) -> {
            status.put(eventId, capacity.getUtilizationPercent());
        });
        return status;
    }
}
