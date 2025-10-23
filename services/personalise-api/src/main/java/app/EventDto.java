package app.model;

import java.time.OffsetDateTime;
import java.util.List;

public class EventDto {
    private String id;
    private String title;
    private String description;
    private String location;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private String createdBy;
    private int maxParticipants;
    private int currentParticipants;
    private List<String> userIds;

    public EventDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }

    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getCurrentParticipants() { return currentParticipants; }
    public void setCurrentParticipants(int currentParticipants) { this.currentParticipants = currentParticipants; }

    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }

    @Override
    public String toString() {
        return "Event{id='" + id + "', title='" + title + "', description='" + description +
               "', location='" + location + "', startTime=" + startTime + ", endTime=" + endTime +
               ", createdBy='" + createdBy + "', maxParticipants=" + maxParticipants +
               ", currentParticipants=" + currentParticipants + "}";
    }
}
