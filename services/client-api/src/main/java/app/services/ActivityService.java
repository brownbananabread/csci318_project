package app.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpHeaders;
import org.springframework.core.ParameterizedTypeReference;
import app.utils.Fetch;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.OffsetDateTime;

@Service
public class ActivityService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String ACTIVITY_API_URL = "http://localhost:8083";

    public ActivityService() {
        this.webClient = WebClient.builder()
                .baseUrl(ACTIVITY_API_URL)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void logActivity(String bearerToken, String title, String description, String path) {
        try {
            // Create the activity object in the exact format you want
            Map<String, Object> activityData = Map.of(
                "title", title,
                "description", description,
                "path", path,
                "timestamp", OffsetDateTime.now()
            );

            // Post the activity data directly - activity-api will get user_id from bearer token
            webClient.post()
                    .uri("/api/v1/activities")
                    .header(HttpHeaders.AUTHORIZATION, bearerToken)
                    .bodyValue(activityData)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            System.err.println("Failed to log activity: " + Fetch.extractErrorMessage(e));
        } catch (Exception e) {
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getUserActivities(String userId) throws Exception {
        try {
            List<Map<String, Object>> allActivities = webClient.get()
                    .uri("/api/v1/activities")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                    .block();

            if (allActivities == null) {
                return new ArrayList<>();
            }

            // Filter activities for the specific user and parse the activity object
            String cleanUserId = Fetch.extractBearerToken(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> activity : allActivities) {
                if (cleanUserId.equals(activity.get("userId"))) {
                    try {
                        String activityObjectJson = (String) activity.get("activityObject");
                        Map<String, Object> parsedActivity = objectMapper.readValue(activityObjectJson, Map.class);
                        result.add(parsedActivity);
                    } catch (Exception e) {
                        System.err.println("Failed to parse activity object: " + e.getMessage());
                        Map<String, Object> defaultActivity = Map.of(
                            "title", "Unknown",
                            "description", "Failed to parse activity",
                            "path", "/api/v1/unknown",
                            "timestamp", java.time.OffsetDateTime.now()
                        );
                        result.add(defaultActivity);
                    }
                }
            }
            return result;
        } catch (WebClientResponseException e) {
            throw new Exception(Fetch.extractErrorMessage(e));
        } catch (Exception e) {
            throw new Exception("Activity service unavailable: " + e.getMessage());
        }
    }

}