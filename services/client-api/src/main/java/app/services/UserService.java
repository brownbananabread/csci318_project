package app.services;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import app.models.UserDto;
import app.utils.Fetch;
import app.utils.ServiceException;
import java.util.Map;

@Service
public class UserService {

    private final WebClient userApiWebClient;

    public UserService() {
        this.userApiWebClient = WebClient.builder().baseUrl("http://localhost:8081").build();
    }

    public String signup(UserDto user) {
        try {
            Map<String, Boolean> existsResponse = userApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/user")
                            .queryParam("email", user.getEmail())
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Boolean>>() {})
                    .block();

            if (existsResponse != null && existsResponse.get("exists")) {
                throw new ServiceException("User with that email already exists", HttpStatus.CONFLICT);
            }

            String userId = userApiWebClient.post()
                    .uri("/user")
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (userId == null) {
                throw new ServiceException("Signup failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return userId;
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public String login(UserDto user) {
        try {
            String userId = userApiWebClient.post()
                    .uri("/login")
                    .bodyValue(Map.of("email", user.getEmail(), "password", user.getPassword()))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (userId == null) {
                throw new ServiceException("Login failed", HttpStatus.UNAUTHORIZED);
            }

            return userId;
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public void updateAccount(String token, UserDto user) {
        boolean hasName = user.getName() != null && !user.getName().trim().isEmpty();
        boolean hasEmail = user.getEmail() != null && !user.getEmail().trim().isEmpty();
        boolean hasPassword = user.getPassword() != null && !user.getPassword().trim().isEmpty();
        
        if (!hasName && !hasEmail && !hasPassword) {
            throw new ServiceException("At least one field (name, email, or password) is required", HttpStatus.BAD_REQUEST);
        }
        
        String actualToken = Fetch.extractBearerToken(token);
        try {
            userApiWebClient.patch()
                    .uri("/user")
                    .header("Authorization", actualToken)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public void removeAccount(String token) {
        String actualToken = Fetch.extractBearerToken(token);
        try {
            userApiWebClient.delete()
                    .uri("/user")
                    .header("Authorization", actualToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }

    public UserDto getAccount(String token) {
        String actualToken = Fetch.extractBearerToken(token);
        try {
            UserDto user = userApiWebClient.get()
                    .uri("/user")
                    .header("Authorization", actualToken)
                    .retrieve()
                    .bodyToMono(UserDto.class)
                    .block();

            if (user == null) {
                throw new ServiceException("User not found", HttpStatus.NOT_FOUND);
            }

            return user;
        } catch (WebClientResponseException e) {
            String errorMessage = Fetch.extractErrorMessage(e);
            throw new ServiceException(errorMessage, HttpStatus.valueOf(e.getStatusCode().value()));
        }
    }
}