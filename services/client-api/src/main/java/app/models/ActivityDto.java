package app.models;

import java.time.OffsetDateTime;

public class ActivityDto {

    private Long id;
    private String userId;
    private String activityObject;
    private OffsetDateTime timestamp;

    public ActivityDto() {}

    public ActivityDto(String activityObject) {
        this.activityObject = activityObject;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActivityObject() { return activityObject; }
    public void setActivityObject(String activityObject) { this.activityObject = activityObject; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}