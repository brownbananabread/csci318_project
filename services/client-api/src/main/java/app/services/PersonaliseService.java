package app.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import app.utils.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import java.util.Map;
import java.util.HashMap;

@Service
public class PersonaliseService {

    private final RestTemplate restTemplate;

    private static final String PERSONALISE_API_BASE = "http://localhost:8084";

    public PersonaliseService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> processChat(String userId, Map<String, Object> chatData) throws ServiceException {
        try {
            // Call the personalise service chat endpoint
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            headers.set("Content-Type", "application/json");
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(chatData, headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.postForObject(
                PERSONALISE_API_BASE + "/api/v1/chat",
                entity,
                Map.class
            );

            if (response != null) {
                return response;
            }

            return new HashMap<>();
        } catch (Exception e) {
            throw new ServiceException("Failed to process chat request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> processMyEvents(String userId) throws ServiceException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.exchange(
                PERSONALISE_API_BASE + "/api/v1/my-events",
                org.springframework.http.HttpMethod.GET,
                entity,
                Map.class
            ).getBody();

            if (response != null) {
                return response;
            }

            return new HashMap<>();
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve my events: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Map<String, Object> processRecommendedEvents(String userId) throws ServiceException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", userId);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) restTemplate.exchange(
                PERSONALISE_API_BASE + "/api/v1/recommended-events",
                org.springframework.http.HttpMethod.GET,
                entity,
                Map.class
            ).getBody();

            if (response != null) {
                return response;
            }

            return new HashMap<>();
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve recommended events: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}