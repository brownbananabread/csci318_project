package app.tools;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import dev.langchain4j.agent.tool.Tool;
import app.model.EventDto;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AllEventsTool {

    private final RestTemplate restTemplate;
    private static final String EVENT_API_BASE = "http://localhost:8082";

    public AllEventsTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Tool("Fetches all events available in the system, including their title, description, location, time, and capacity")
    public String getAllEvents() {
        try {
            ResponseEntity<List<EventDto>> response = restTemplate.exchange(
                EVENT_API_BASE + "/api/v1/events",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<EventDto>>() {}
            );

            List<EventDto> events = response.getBody();
            if (events == null || events.isEmpty()) {
                return "No events found";
            }

            return events.stream()
                .map(event -> String.format(
                    "Event ID: %s\nTitle: %s\nDescription: %s\nLocation: %s\nStart: %s\nEnd: %s\nCapacity: %d/%d\nCreated by: %s\n",
                    event.getId(),
                    event.getTitle(),
                    event.getDescription(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getCurrentParticipants(),
                    event.getMaxParticipants(),
                    event.getCreatedBy()
                ))
                .collect(Collectors.joining("\n---\n"));
        } catch (Exception e) {
            return "Failed to fetch events: " + e.getMessage();
        }
    }
}
