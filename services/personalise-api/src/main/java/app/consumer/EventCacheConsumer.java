package app.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer for Personalise API that listens to event-related domain events.
 * Keeps local caches and state synchronized with the Event API asynchronously.
 */
@Component
public class EventCacheConsumer {
    private static final Logger logger = LoggerFactory.getLogger(EventCacheConsumer.class);

    private final ObjectMapper objectMapper;

    public EventCacheConsumer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Consumes EventCreatedEvent to update local event cache.
     * This enables the AI agent to have faster access to event data without
     * making synchronous HTTP calls to other services.
     */
    @KafkaListener(topics = "event-created", groupId = "personalise-service-group")
    public void consumeEventCreated(String message) {
        try {
            logger.info("Received EventCreatedEvent for cache update: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("title").asText();

            // In a production system, you would update a local cache (Redis, in-memory, etc.)
            // For now, we just log that the event was received
            logger.info("Event cache update - New event: {} ({})", eventTitle, eventId);

            // Future enhancement: Store in Redis or local cache for faster AI agent queries
            // cacheService.put(eventId, event);
        } catch (Exception e) {
            logger.error("Error processing EventCreatedEvent for cache: {}", message, e);
        }
    }

    /**
     * Consumes UserRegisteredForEventEvent to track trending events.
     * This data can be used by the AI agent to recommend popular events.
     */
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

            // Future enhancement: Track registration patterns for better recommendations
            // trendingService.incrementEventPopularity(eventId);
        } catch (Exception e) {
            logger.error("Error processing UserRegisteredForEventEvent: {}", message, e);
        }
    }

    /**
     * Consumes EventCapacityReachedEvent to mark events as full.
     * Prevents AI agent from recommending fully booked events.
     */
    @KafkaListener(topics = "event-capacity-reached", groupId = "personalise-service-group")
    public void consumeEventCapacityReached(String message) {
        try {
            logger.info("Received EventCapacityReachedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String eventId = event.get("eventId").asText();
            String eventTitle = event.get("eventTitle").asText();

            logger.info("Event capacity alert - Event: {} is now full", eventTitle);

            // Future enhancement: Mark event as unavailable in cache
            // cacheService.markEventFull(eventId);
        } catch (Exception e) {
            logger.error("Error processing EventCapacityReachedEvent: {}", message, e);
        }
    }

    /**
     * Consumes UserCreatedEvent to build user profiles for better personalization.
     */
    @KafkaListener(topics = "user-created", groupId = "personalise-service-group")
    public void consumeUserCreated(String message) {
        try {
            logger.info("Received UserCreatedEvent: {}", message);
            JsonNode event = objectMapper.readTree(message);

            String userId = event.get("userId").asText();
            String userName = event.get("name").asText();

            logger.info("New user profile - User: {} ({})", userName, userId);

            // Future enhancement: Initialize user profile for personalization
            // userProfileService.createProfile(userId, userName);
        } catch (Exception e) {
            logger.error("Error processing UserCreatedEvent: {}", message, e);
        }
    }
}
