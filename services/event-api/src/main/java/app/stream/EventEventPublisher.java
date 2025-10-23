package app.publisher;

import app.events.EventCreatedEvent;
import app.events.EventCapacityReachedEvent;
import app.events.UserRegisteredForEventEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(EventEventPublisher.class);

    private static final String EVENT_CREATED_TOPIC = "event-created";
    private static final String USER_REGISTERED_TOPIC = "user-registered-event";
    private static final String EVENT_CAPACITY_REACHED_TOPIC = "event-capacity-reached";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEventCreated(EventCreatedEvent event) {
        try {
            logger.info("Publishing EventCreatedEvent for event: {}", event.getEventId());
            kafkaTemplate.send(EVENT_CREATED_TOPIC, event.getEventId(), event);
            logger.info("Successfully published EventCreatedEvent for event: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish EventCreatedEvent for event: {}", event.getEventId(), e);
        }
    }

    public void publishUserRegisteredForEvent(UserRegisteredForEventEvent event) {
        try {
            logger.info("Publishing UserRegisteredForEventEvent - User: {}, Event: {}",
                event.getUserId(), event.getEventId());
            kafkaTemplate.send(USER_REGISTERED_TOPIC, event.getEventId(), event);
            logger.info("Successfully published UserRegisteredForEventEvent - User: {}, Event: {}",
                event.getUserId(), event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish UserRegisteredForEventEvent - User: {}, Event: {}",
                event.getUserId(), event.getEventId(), e);
        }
    }

    public void publishEventCapacityReached(EventCapacityReachedEvent event) {
        try {
            logger.info("Publishing EventCapacityReachedEvent for event: {}", event.getEventId());
            kafkaTemplate.send(EVENT_CAPACITY_REACHED_TOPIC, event.getEventId(), event);
            logger.info("Successfully published EventCapacityReachedEvent for event: {}", event.getEventId());
        } catch (Exception e) {
            logger.error("Failed to publish EventCapacityReachedEvent for event: {}", event.getEventId(), e);
        }
    }
}
