package csci318.demo.service;

import csci318.demo.model.ApiResponse;
import csci318.demo.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Service
public class UserApiService {

    private final WebClient userApiWebClient;

    public UserApiService(WebClient userApiWebClient) {
        this.userApiWebClient = userApiWebClient;
    }

    public ApiResponse signup(User user) {
        Map<String, Boolean> existsResponse = userApiWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .queryParam("email", user.getEmail())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Boolean>>() {})
                .block();

        if (existsResponse != null && existsResponse.get("exists")) {
            return new ApiResponse("User with that email already exists", 409);
        }

        ApiResponse createResponse = userApiWebClient.post()
                .uri("/user")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block();
        
        String userId = createResponse.getMessage().toString();

        return new ApiResponse(Map.of("userId", userId), 201);
    }

    public ApiResponse login(User user) {
        try {
            ApiResponse loginResponse = userApiWebClient.post()
                    .uri("/login")
                    .bodyValue(Map.of("email", user.getEmail(), "password", user.getPassword()))
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();
            
            String userId = loginResponse.getMessage().toString();

            Map<String, String> response = Map.of("accessToken", userId);
            return new ApiResponse(response, 200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                return new ApiResponse("Invalid credentials", 401);
            }
            return new ApiResponse("Login failed", 400);
        }
    }

    public ApiResponse updateAccount(String token, User user) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            userApiWebClient.put()
                    .uri("/user")
                    .header("Authorization", actualToken)
                    .bodyValue(user)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            return new ApiResponse("Account updated successfully", 200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return new ApiResponse("User not found or invalid token", 404);
            }
            return new ApiResponse("Failed to update account", 500);
        }
    }

    public ApiResponse removeAccount(String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            userApiWebClient.delete()
                    .uri("/user")
                    .header("Authorization", actualToken)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            return new ApiResponse("Account removed successfully", 200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return new ApiResponse("User not found or invalid token", 404);
            }
            return new ApiResponse("Failed to remove account", 500);
        }
    }

    public ApiResponse getAccount(String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            ApiResponse userResponse = userApiWebClient.get()
                    .uri("/user")
                    .header("Authorization", actualToken)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block();

            return new ApiResponse(userResponse.getMessage(), 200);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return new ApiResponse("User not found or invalid token", 404);
            }
            return new ApiResponse("Failed to get account", 500);
        }
    }
}