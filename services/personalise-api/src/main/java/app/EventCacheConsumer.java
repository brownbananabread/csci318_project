package app.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventCacheConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventCacheConsumer.class);

    private final ObjectMapper objectMapper;

    public EventCacheConsumer() {
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "event-created", groupId = "personalise-service-group")
    public void consumeEventCreated(String message) {
        try {
            logger.info("Received EventCreatedEvent for cache update: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("title").asText();

            logger.info("Event cache update - New event: {} ({})", eventTitle, eventId);

        } catch (Exception e) {
            logger.error("Error processing EventCreatedEvent for cache: {}", message, e);
        }
    }

    @KafkaListener(topics = "user-registered-event", groupId = "personalise-service-group")
    public void consumeUserRegistered(String message) {
        try {
            logger.info("Received UserRegisteredForEventEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();
            int currentParticipants = event.get("currentParticipants").asInt();

            logger.info("Event registration tracking - Event: {} now has {} participants",
                eventTitle, currentParticipants);
        } catch (Exception e) {
            logger.error("Error processing UserRegisteredForEventEvent: {}", message, e);
        }
    }

    @KafkaListener(topics = "event-capacity-reached", groupId = "personalise-service-group")
    public void consumeEventCapacityReached(String message) {
        try {
            logger.info("Received EventCapacityReachedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();

            logger.info("Event capacity alert - Event: {} is now full", eventTitle);

        } catch (Exception e) {
            logger.error("Error processing EventCapacityReachedEvent: {}", message, e);
        }
    }

    @KafkaListener(topics = "user-created", groupId = "personalise-service-group")
    public void consumeUserCreated(String message) {
        try {
            logger.info("Received UserCreatedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String userId = event.get("userId").asText();
            String userName = event.get("name").asText();

            logger.info("New user profile - User: {} ({})", userName, userId);

        } catch (Exception e) {
            logger.error("Error processing UserCreatedEvent: {}", message, e);
        }
    }
}
