package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import app.model.UserDto;
import app.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user) {
        try {
            String userId = userService.signup(user);
            return ResponseEntity.status(201).body(Map.of("accessToken", userId));
        } catch (WebClientResponseException e) {
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage.isEmpty()) {
                errorMessage = e.getStatusCode().toString() + " " + e.getStatusText() + " from POST http://localhost:8081/user";
            }
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", errorMessage));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto user) {
        try {
            String userId = userService.login(user);
            return ResponseEntity.ok(Map.of("accessToken", userId));
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
            }
            return ResponseEntity.status(400).body(Map.of("error", "Login failed"));
        }
    }

    @PutMapping("/update-account")
    public ResponseEntity<?> updateAccount(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody UserDto user) {
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "User must first login to update account"));
        }
        try {
            userService.updateAccount(token, user);
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
            userService.removeAccount(token);
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
            UserDto user = userService.getAccount(token);
            return ResponseEntity.ok(user);
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is4xxClientError()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found or invalid token"));
            }
            return ResponseEntity.status(500).body(Map.of("error", "Failed to get account"));
        }
    }
}