package csci318.demo.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class UserApiService {

    private final WebClient userApiWebClient;

    public UserApiService(@Qualifier("userApiWebClient") WebClient userApiWebClient) {
        this.userApiWebClient = userApiWebClient;
    }

    public List<Object> getUsers() {
        return userApiWebClient.get()
                .uri("/users")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Object>>() {})
                .block();
    }
}