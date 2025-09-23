package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import app.exception.ServiceException;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class PersonaliseService {

    private final RestTemplate restTemplate;
    private final GoogleAiGeminiChatModel chatModel;

    private static final String CLIENT_API_BASE = "http://localhost:8080";

    public PersonaliseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

        // Initialize Gemini model - API key should be configured via environment variable
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY environment variable not set");
        }

        this.chatModel = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-1.5-flash")
                .build();
    }

    public Map<String, Object> processSummary(String userId) throws ServiceException {
        try {
            Map<String, Object> result = new HashMap<>();

            // Fetch user details via client API
            Map<String, Object> userDetails = fetchUserDetails(userId);
            result.put("userDetails", userDetails);

            // Fetch user's own events via client API
            List<Map<String, Object>> userEvents = fetchUserEvents(userId);
            result.put("userEvents", userEvents);

            // Fetch events user is registered for via client API
            List<Map<String, Object>> registeredEvents = fetchRegisteredEvents(userId);
            result.put("registeredEvents", registeredEvents);

            return result;
        } catch (Exception e) {
            throw new ServiceException("Failed to process summary request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> processChat(String userId, Map<String, Object> chatData) throws ServiceException {
        try {
            // Extract message from request body
            String message = (String) chatData.get("message");
            if (message == null || message.trim().isEmpty()) {
                throw new ServiceException("Message is required", HttpStatus.BAD_REQUEST);
            }

            // Fetch user details via client API
            Map<String, Object> userDetails = fetchUserDetails(userId);

            // Fetch user's own events via client API
            List<Map<String, Object>> userEvents = fetchUserEvents(userId);

            // Fetch events user is registered for via client API
            List<Map<String, Object>> registeredEvents = fetchRegisteredEvents(userId);

            // Build context for Gemini
            StringBuilder context = new StringBuilder();
            context.append("User Information:\n");
            if (userDetails.containsKey("name")) {
                context.append("Name: ").append(userDetails.get("name")).append("\n");
            }
            if (userDetails.containsKey("email")) {
                context.append("Email: ").append(userDetails.get("email")).append("\n");
            }
            context.append("\nUser's Events: ").append(userEvents.size()).append(" events\n");
            context.append("Registered Events: ").append(registeredEvents.size()).append(" events\n");
            context.append("\nUser's message: ").append(message).append("\n");
            context.append("\nPlease respond as a helpful assistant with knowledge of the user's event management system.");

            // Send to Gemini
            UserMessage userMessage = UserMessage.from(context.toString());
            AiMessage response = chatModel.generate(userMessage).content();

            return Map.of(
                "message", message,
                "response", response.text(),
                "userDetails", userDetails,
                "eventCount", userEvents.size(),
                "registeredEventCount", registeredEvents.size()
            );

        } catch (Exception e) {
            throw new ServiceException("Failed to process chat request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> fetchUserDetails(String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                CLIENT_API_BASE + "/users/account",
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                Map<String, Object> userData = (Map<String, Object>) responseBody.get("data");
                // Remove password for security
                if (userData != null) {
                    userData = new HashMap<>(userData);
                    userData.remove("password");
                }
                return userData;
            }
            return new HashMap<>();
        } catch (Exception e) {
            // Return empty map if client API is not available
            return new HashMap<>();
        }
    }

    private List<Map<String, Object>> fetchUserEvents(String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                CLIENT_API_BASE + "/events/my-events",
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                return (List<Map<String, Object>>) responseBody.get("data");
            }
            return List.of();
        } catch (Exception e) {
            // Return empty list if client API is not available
            return List.of();
        }
    }

    private List<Map<String, Object>> fetchRegisteredEvents(String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                CLIENT_API_BASE + "/events/registered",
                HttpMethod.GET,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                return (List<Map<String, Object>>) responseBody.get("data");
            }
            return List.of();
        } catch (Exception e) {
            // Return empty list if client API is not available
            return List.of();
        }
    }
}