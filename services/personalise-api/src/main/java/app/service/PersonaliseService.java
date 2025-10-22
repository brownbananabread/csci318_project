package app.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import app.exception.ServiceException;
import app.agent.EventAgent;
import app.context.UserContext;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.ollama.OllamaChatModel;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class PersonaliseService {

    private final RestTemplate restTemplate;
    private final OllamaChatModel chatModel;
    private final EventAgent eventAgent;

    private static final String CLIENT_API_BASE = "http://localhost:8080";

    public PersonaliseService(RestTemplate restTemplate, OllamaChatModel chatModel, EventAgent eventAgent) {
        this.restTemplate = restTemplate;
        this.chatModel = chatModel;
        this.eventAgent = eventAgent;
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

            // Set user context for tools to access
            UserContext.setUserId(userId);

            try {
                // Use the AI agent which will autonomously decide which tools to call
                // based on the user's question. For example:
                // - "How many events have I created?" -> calls getUserCreatedEvents
                // - "What events am I attending?" -> calls getUserRegisteredEvents
                // - "What events are available?" -> calls getAllEvents
                // - "Tell me about myself" -> calls getUserDetails
                // This demonstrates autonomous tool selection based on context

                String response = eventAgent.chat(message);

                return Map.of(
                    "message", message,
                    "response", response,
                    "agenticBehavior", "The AI agent autonomously selected which tools to call based on your question"
                );
            } finally {
                UserContext.clear();
            }

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

    public Map<String, Object> processMyEvents(String userId) throws ServiceException {
        try {
            // Set user context for tools to access
            UserContext.setUserId(userId);

            try {
                // Use the AI agent which will autonomously:
                // 1. Decide it needs user details, created events, and registered events
                // 2. Call the appropriate tools (getUserDetails, getUserCreatedEvents, getUserRegisteredEvents)
                // 3. Analyze the results
                // 4. Generate a personalized summary
                // This demonstrates true agentic behavior with autonomous tool selection and multi-step reasoning

                String summary = eventAgent.summarizeMyEvents(
                    "Please provide a comprehensive summary of my event activity."
                );

                return Map.of(
                    "summary", summary,
                    "agenticBehavior", "The AI agent autonomously decided which tools to call and synthesized the results"
                );
            } finally {
                UserContext.clear();
            }

        } catch (Exception e) {
            throw new ServiceException("Failed to process my-events request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> processRecommendedEvents(String userId) throws ServiceException {
        try {
            // Set user context for tools to access
            UserContext.setUserId(userId);

            try {
                // Use the AI agent which will autonomously:
                // 1. Fetch the user's registered events to understand their interests
                // 2. Fetch all available events
                // 3. Compare and analyze the events
                // 4. Select relevant events NOT already registered for
                // 5. Generate personalized recommendations with reasoning
                // This demonstrates multi-step reasoning and complex decision-making

                String recommendations = eventAgent.recommendEvents(
                    "Please recommend events I should attend based on my interests."
                );

                return Map.of(
                    "recommendations", recommendations,
                    "agenticBehavior", "The AI agent autonomously analyzed your interests and all available events to make personalized recommendations"
                );
            } finally {
                UserContext.clear();
            }

        } catch (Exception e) {
            throw new ServiceException("Failed to process recommended-events request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}