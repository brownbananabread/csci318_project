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

    public Map<String, Object> processMyEvents(String userId) throws ServiceException {
        try {
            // Set user context for tools to access
            UserContext.setUserId(userId);

            try {
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