package app.events;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * Domain event published when a new event is created.
 * Allows other services to react to new events asynchronously.
 */
public class EventCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String eventId;
    private String title;
    private String description;
    private String location;
    private String createdBy;
    private int maxParticipants;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private OffsetDateTime timestamp;

    public EventCreatedEvent() {
    }

    public EventCreatedEvent(String eventId, String title, String description, String location,
                             String createdBy, int maxParticipants, OffsetDateTime startTime,
                             OffsetDateTime endTime, OffsetDateTime timestamp) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.createdBy = createdBy;
        this.maxParticipants = maxParticipants;
        this.startTime = startTime;
        this.endTime = endTime;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "EventCreatedEvent{" +
                "eventId='" + eventId + '\'' +
                ", title='" + title + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
