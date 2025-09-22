package app.model;

public class EventRegistrationDto {
    private String userId;
    private String eventId;

    public EventRegistrationDto() {}

    public EventRegistrationDto(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}