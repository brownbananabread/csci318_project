package app.events;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Domain event published when a user registers for an event.
 * Enables event-driven notification and analytics.
 */
public class UserRegisteredForEventEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String eventId;
    private String eventTitle;
    private int currentParticipants;
    private int maxParticipants;
    private OffsetDateTime timestamp;

    public UserRegisteredForEventEvent() {
    }

    public UserRegisteredForEventEvent(String userId, String eventId, String eventTitle,
                                       int currentParticipants, int maxParticipants,
                                       OffsetDateTime timestamp) {
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.currentParticipants = currentParticipants;
        this.maxParticipants = maxParticipants;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserRegisteredForEventEvent{" +
                "userId='" + userId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                ", currentParticipants=" + currentParticipants +
                ", maxParticipants=" + maxParticipants +
                ", timestamp=" + timestamp +
                '}';
    }
}
