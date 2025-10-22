package app.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import app.model.ActivityEntity;
import app.repository.ActivityRepository;

import java.time.OffsetDateTime;

/**
 * Kafka consumer that listens to domain events and logs them as activities.
 * This replaces synchronous activity logging with event-driven architecture.
 */
@Component
public class EventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    private final ActivityRepository activityRepository;
    private final ObjectMapper objectMapper;

    public EventConsumer(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Consumes UserCreatedEvent and logs user creation activity.
     */
    @KafkaListener(topics = "user-created", groupId = "activity-service-group")
    public void consumeUserCreated(String message) {
        try {
            logger.info("Received UserCreatedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String userId = event.get("userId").asText();
            String userEmail = event.get("email").asText();

            String activityJson = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("type", "USER_CREATED");
                put("description", "User " + userEmail + " was created");
                put("path", "/user");
                put("timestamp", OffsetDateTime.now().toString());
            }});

            ActivityEntity activity = new ActivityEntity(userId, activityJson);
            activityRepository.save(activity);
            logger.info("Logged USER_CREATED activity for user: {}", userId);
        } catch (Exception e) {
            logger.error("Error processing UserCreatedEvent: {}", message, e);
        }
    }

    /**
     * Consumes EventCreatedEvent and logs event creation activity.
     */
    @KafkaListener(topics = "event-created", groupId = "activity-service-group")
    public void consumeEventCreated(String message) {
        try {
            logger.info("Received EventCreatedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("title").asText();
            String createdBy = event.get("createdBy").asText();

            String activityJson = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("type", "EVENT_CREATED");
                put("description", "Event '" + eventTitle + "' was created");
                put("path", "/events/" + eventId);
                put("timestamp", OffsetDateTime.now().toString());
            }});

            ActivityEntity activity = new ActivityEntity(createdBy, activityJson);
            activityRepository.save(activity);
            logger.info("Logged EVENT_CREATED activity for event: {}", eventId);
        } catch (Exception e) {
            logger.error("Error processing EventCreatedEvent: {}", message, e);
        }
    }

    /**
     * Consumes UserRegisteredForEventEvent and logs registration activity.
     */
    @KafkaListener(topics = "user-registered-event", groupId = "activity-service-group")
    public void consumeUserRegistered(String message) {
        try {
            logger.info("Received UserRegisteredForEventEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String userId = event.get("userId").asText();
            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();

            String activityJson = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("type", "USER_REGISTERED");
                put("description", "User registered for event '" + eventTitle + "'");
                put("path", "/events/" + eventId + "/register");
                put("timestamp", OffsetDateTime.now().toString());
            }});

            ActivityEntity activity = new ActivityEntity(userId, activityJson);
            activityRepository.save(activity);
            logger.info("Logged USER_REGISTERED activity for user: {} on event: {}", userId, eventId);
        } catch (Exception e) {
            logger.error("Error processing UserRegisteredForEventEvent: {}", message, e);
        }
    }

    /**
     * Consumes EventCapacityReachedEvent and logs capacity alerts.
     */
    @KafkaListener(topics = "event-capacity-reached", groupId = "activity-service-group")
    public void consumeEventCapacityReached(String message) {
        try {
            logger.info("Received EventCapacityReachedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();
            int maxParticipants = event.get("maxParticipants").asInt();

            String activityJson = objectMapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("type", "EVENT_CAPACITY_REACHED");
                put("description", "Event '" + eventTitle + "' reached maximum capacity of " + maxParticipants);
                put("path", "/events/" + eventId);
                put("timestamp", OffsetDateTime.now().toString());
            }});

            ActivityEntity activity = new ActivityEntity("SYSTEM", activityJson);
            activityRepository.save(activity);
            logger.info("Logged EVENT_CAPACITY_REACHED activity for event: {}", eventId);
        } catch (Exception e) {
            logger.error("Error processing EventCapacityReachedEvent: {}", message, e);
        }
    }
}
