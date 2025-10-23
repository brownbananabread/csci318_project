package app.models;

import java.time.OffsetDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Event data transfer object")
public class EventDto {
    @Schema(description = "Event's unique identifier (auto-generated)", example = "550e8400-e29b-41d4-a716-446655440000", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "Event title", example = "Tech Meetup 2025", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "Event description", example = "Join us for an exciting tech discussion", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "Event location", example = "Community Center, Room 201", requiredMode = Schema.RequiredMode.REQUIRED)
    private String location;

    @Schema(description = "Event start time (ISO-8601 format)", example = "2025-11-15T18:00:00+11:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime startTime;

    @Schema(description = "Event end time (ISO-8601 format)", example = "2025-11-15T20:00:00+11:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime endTime;

    @Schema(description = "User ID of event creator", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private String createdBy;

    @Schema(description = "Maximum number of participants allowed", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    private int maxParticipants;

    @Schema(description = "Current number of registered participants", example = "0", accessMode = Schema.AccessMode.READ_ONLY)
    private int currentParticipants;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "List of registered user IDs", accessMode = Schema.AccessMode.READ_ONLY)
    private List<String> userIds;

    public EventDto() {}

    public EventDto(String id, String title, String description, String location,
                   OffsetDateTime startTime, OffsetDateTime endTime, String createdBy,
                   int maxParticipants, int currentParticipants) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
    }

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
}