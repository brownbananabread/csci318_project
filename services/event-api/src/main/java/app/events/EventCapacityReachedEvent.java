package app.events;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class EventCapacityReachedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String eventTitle;
    private int maxParticipants;
    private OffsetDateTime timestamp;

    public EventCapacityReachedEvent() {
    }

    public EventCapacityReachedEvent(String eventId, String eventTitle, int maxParticipants, OffsetDateTime timestamp) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.maxParticipants = maxParticipants;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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
        return "EventCapacityReachedEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventTitle='" + eventTitle + '\'' +
                ", maxParticipants=" + maxParticipants +
                ", timestamp=" + timestamp +
                '}';
    }
}
