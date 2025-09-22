package app.model;

public class ActivityDto {

    private Long id;
    private String userId;
    private String activityObject;

    public ActivityDto() {}

    public ActivityDto(String activityObject) {
        this.activityObject = activityObject;
    }

    public ActivityDto(ActivityEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.activityObject = entity.getActivityObject();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActivityObject() { return activityObject; }
    public void setActivityObject(String activityObject) { this.activityObject = activityObject; }
}