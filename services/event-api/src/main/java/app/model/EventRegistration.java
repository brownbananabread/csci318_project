package app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_registrations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "event_id"}))
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    public EventRegistration() {}

    public EventRegistration(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
