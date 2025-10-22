package app.publisher;

import app.events.UserCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher for user-related domain events.
 * Publishes events to Kafka topics for asynchronous processing.
 */
@Component
public class UserEventPublisher {
    private static final Logger logger = LoggerFactory.getLogger(UserEventPublisher.class);
    private static final String USER_CREATED_TOPIC = "user-created";

    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    public UserEventPublisher(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a UserCreatedEvent to Kafka.
     *
     * @param event The user created event to publish
     */
    public void publishUserCreated(UserCreatedEvent event) {
        try {
            logger.info("Publishing UserCreatedEvent for user: {}", event.getUserId());
            kafkaTemplate.send(USER_CREATED_TOPIC, event.getUserId(), event);
            logger.info("Successfully published UserCreatedEvent for user: {}", event.getUserId());
        } catch (Exception e) {
            logger.error("Failed to publish UserCreatedEvent for user: {}", event.getUserId(), e);
            // In a production system, you might want to implement retry logic or dead letter queue
        }
    }
}
