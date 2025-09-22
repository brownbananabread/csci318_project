package app.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import app.utils.Fetch;

import app.exception.ServiceException;
import app.model.EventDto;
import app.model.UserDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.Map;

@Service
public class EventService {

    private final WebClient eventApiWebClient;
    private final WebClient userApiWebClient;
    private final ObjectMapper objectMapper;

    public EventService() {
        this.eventApiWebClient = WebClient.builder().baseUrl("http://localhost:8082").build();
        this.userApiWebClient = WebClient.builder().baseUrl("http://localhost:8081").build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public List<EventDto> getAllEvents() {
        try {
            Map<String, Object> response = eventApiWebClient.get()
                    .uri("/api/v1/events")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.get("data") == null) {
                throw new ServiceException("Failed to retrieve events", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) response.get("data");
            return eventMaps.stream()
                    .map(eventMap -> objectMapper.convertValue(eventMap, EventDto.class))
                    .toList();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public EventDto getEvent(String eventId) {
        try {
            Map<String, Object> response = eventApiWebClient.get()
                    .uri("/api/v1/events/{id}", eventId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.get("data") == null) {
                throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> eventMap = (Map<String, Object>) response.get("data");
            return objectMapper.convertValue(eventMap, EventDto.class);
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public String createEvent(String token, EventDto event) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            Map<String, Object> response = eventApiWebClient.post()
                    .uri("/api/v1/events")
                    .header("Authorization", user.getId())
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.get("data") == null) {
                throw new ServiceException("Event creation failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return (String) data.get("eventId");
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public void updateEvent(String token, String eventId, EventDto event) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            eventApiWebClient.patch()
                    .uri("/api/v1/events/{id}", eventId)
                    .header("Authorization", user.getId())
                    .bodyValue(event)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public void deleteEvent(String token, String eventId) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            eventApiWebClient.delete()
                    .uri("/api/v1/events/{id}", eventId)
                    .header("Authorization", user.getId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public void registerForEvent(String token, String eventId) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            eventApiWebClient.post()
                    .uri("/api/v1/events/{id}/register", eventId)
                    .header("Authorization", user.getId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public void deregisterFromEvent(String token, String eventId) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            eventApiWebClient.delete()
                    .uri("/api/v1/events/{id}/register", eventId)
                    .header("Authorization", user.getId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public List<EventDto> getUserEvents(String token) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            Map<String, Object> response = eventApiWebClient.get()
                    .uri("/api/v1/events/my-events")
                    .header("Authorization", user.getId())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.get("data") == null) {
                throw new ServiceException("Failed to retrieve user events", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) response.get("data");
            return eventMaps.stream()
                    .map(eventMap -> objectMapper.convertValue(eventMap, EventDto.class))
                    .toList();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public List<EventDto> getRegisteredEvents(String token) {
        String actualToken = Fetch.extractBearerToken(token);

        UserDto user = validateUser(actualToken);

        try {
            Map<String, Object> response = eventApiWebClient.get()
                    .uri("/api/v1/events/registered")
                    .header("Authorization", user.getId())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            if (response == null || response.get("data") == null) {
                throw new ServiceException("Failed to retrieve registered events", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            List<Map<String, Object>> eventMaps = (List<Map<String, Object>>) response.get("data");
            return eventMaps.stream()
                    .map(eventMap -> objectMapper.convertValue(eventMap, EventDto.class))
                    .toList();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    private UserDto validateUser(String token) {
        try {
            UserDto user = userApiWebClient.get()
                    .uri("/user")
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();

            if (user == null) {
                throw new ServiceException("User not found", HttpStatus.UNAUTHORIZED);
            }

            return user;
        } catch (WebClientResponseException e) {
            throw new ServiceException("Invalid user token", HttpStatus.UNAUTHORIZED);
        }
    }

}