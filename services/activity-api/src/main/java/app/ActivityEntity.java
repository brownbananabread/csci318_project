package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "activities")
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String activityObject;

    public ActivityEntity() {}

    public ActivityEntity(String userId, String activityObject) {
        this.userId = userId;
        this.activityObject = activityObject;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getActivityObject() { return activityObject; }
    public void setActivityObject(String activityObject) { this.activityObject = activityObject; }

    @Override
    public String toString() {
        return "ActivityEntity{id=" + id + ", userId='" + userId + "', activityObject='" + activityObject + "}";
    }
}