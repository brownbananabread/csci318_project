package app.tools;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import dev.langchain4j.agent.tool.Tool;
import app.model.UserDto;
import app.context.UserContext;

import java.util.Map;
import java.util.HashMap;

@Component
public class UserDetailsTool {

    private final RestTemplate restTemplate;
    private static final String USER_API_BASE = "http://localhost:8081";

    public UserDetailsTool(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Tool("Fetches the details of the current user including their name and email")
    public String getUserDetails() {
        try {
            String userId = UserContext.getUserId();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UserDto> response = restTemplate.exchange(
                USER_API_BASE + "/user",
                HttpMethod.GET,
                entity,
                UserDto.class
            );

            UserDto user = response.getBody();
            if (user != null) {
                return String.format("User ID: %s, Name: %s, Email: %s",
                    user.getId(), user.getName(), user.getEmail());
            }
            return "User not found";
        } catch (Exception e) {
            return "Failed to fetch user details: " + e.getMessage();
        }
    }
}
