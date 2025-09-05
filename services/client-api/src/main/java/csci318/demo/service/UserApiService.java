package csci318.demo.service;

import csci318.demo.model.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class UserApiService {

    private final WebClient userApiWebClient;

    public UserApiService() {
        this.userApiWebClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    public String signup(User user) {
        Map<String, Boolean> existsResponse = userApiWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/user")
                        .queryParam("email", user.getEmail())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Boolean>>() {})
                .block();

        if (existsResponse != null && existsResponse.get("exists")) {
            throw new RuntimeException("User with that email already exists");
        }

        String userId = userApiWebClient.post()
                .uri("/user")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return userId;
    }

    public String login(User user) {
        String userId = userApiWebClient.post()
                .uri("/login")
                .bodyValue(Map.of("email", user.getEmail(), "password", user.getPassword()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return userId;
    }

    public void updateAccount(String token, User user) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        userApiWebClient.put()
                .uri("/user")
                .header("Authorization", actualToken)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void removeAccount(String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        userApiWebClient.delete()
                .uri("/user")
                .header("Authorization", actualToken)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public User getAccount(String token) {
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        User user = userApiWebClient.get()
                .uri("/user")
                .header("Authorization", actualToken)
                .retrieve()
                .bodyToMono(User.class)
                .block();

        return user;
    }
}