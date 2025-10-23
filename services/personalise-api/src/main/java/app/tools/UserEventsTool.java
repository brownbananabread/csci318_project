package app.tools;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import dev.langchain4j.agent.tool.Tool;
import app.model.EventDto;
import app.context.UserContext;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserEventsTool {

    private final RestTemplate restTemplate;
    private static final String EVENT_API_BASE = "http://localhost:8082";

    public UserEventsTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Tool("Fetches all events that the user has created")
    public String getUserCreatedEvents() {
        try {
            String userId = UserContext.getUserId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<EventDto>> response = restTemplate.exchange(
                EVENT_API_BASE + "/api/v1/events/my-events",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<EventDto>>() {}
            );

            List<EventDto> events = response.getBody();
            if (events == null || events.isEmpty()) {
                return "User has not created any events";
            }

            return events.stream()
                .map(event -> String.format(
                    "Event ID: %s\nTitle: %s\nDescription: %s\nLocation: %s\nStart: %s\nEnd: %s\nCapacity: %d/%d\n",
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getCurrentParticipants(),
                    event.getMaxParticipants()
                ))
                .collect(Collectors.joining("\n---\n"));
        } catch (Exception e) {
            return "Failed to fetch user's created events: " + e.getMessage();
        }
    }

    @Tool("Fetches all events that the user is registered for (attending)")
    public String getUserRegisteredEvents() {
        try {
            String userId = UserContext.getUserId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<EventDto>> response = restTemplate.exchange(
                EVENT_API_BASE + "/api/v1/events/registered",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<EventDto>>() {}
            );

            List<EventDto> events = response.getBody();
            if (events == null || events.isEmpty()) {
                return "User is not registered for any events";
            }

            return events.stream()
                .map(event -> String.format(
                    "Event ID: %s\nTitle: %s\nDescription: %s\nLocation: %s\nStart: %s\nEnd: %s\nCapacity: %d/%d\n",
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getCurrentParticipants(),
                    event.getMaxParticipants()
                ))
                .collect(Collectors.joining("\n---\n"));
        } catch (Exception e) {
            return "Failed to fetch user's registered events: " + e.getMessage();
        }
    }
}
