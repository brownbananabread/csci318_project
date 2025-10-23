package app;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class UserCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String name;
    private OffsetDateTime timestamp;

    // Default constructor for JSON deserialization
    public UserCreatedEvent() {
    }

    public UserCreatedEvent(String userId, String email, String name, OffsetDateTime timestamp) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
