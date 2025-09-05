package csci318.demo.controller;

import csci318.demo.model.User;
import csci318.demo.service.UserApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UsersController {

    private final UserApiService userApiService;

    public UsersController(UserApiService userApiService) {
        this.userApiService = userApiService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            String userId = userApiService.signup(user);
            return ResponseEntity.status(201).body(Map.of("userId", userId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String userId = userApiService.login(user);
            return ResponseEntity.ok(Map.of("accessToken", userId));
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
            return ResponseEntity.status(400).body(Map.of("error", "Login failed"));
        }
    }

    @PutMapping("/update-account")
    public ResponseEntity<?> updateAccount(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody User user) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User must first login to update account"));
        }
        try {
            userApiService.updateAccount(token, user);
            return ResponseEntity.ok(Map.of("message", "Account updated successfully"));
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found or invalid token"));
            }
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update account"));
        }
    }

    @DeleteMapping("/remove-account")
    public ResponseEntity<?> removeAccount(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User must first login to remove account"));
        }
        try {
            userApiService.removeAccount(token);
            return ResponseEntity.ok(Map.of("message", "Account removed successfully"));
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found or invalid token"));
            }
            return ResponseEntity.status(500).body(Map.of("error", "Failed to remove account"));
        }
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User must first login to view account details"));
        }
        try {
            User user = userApiService.getAccount(token);
            return ResponseEntity.ok(user);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found or invalid token"));
            }
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get account"));
        }
    }
}