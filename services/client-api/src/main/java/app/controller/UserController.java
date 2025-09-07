package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.model.UserDto;
import app.service.UserService;

import java.util.Map;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/signup";
        
        try {
            String userId = userService.signup(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "timestamp", timestamp,
                "status", 201,
                "data", Map.of("accessToken", userId),
                "path", path
            ));
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto user) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/login";
        
        try {
            String userId = userService.login(user);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", Map.of("accessToken", userId),
                "path", path
            ));
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @PatchMapping("/update-account")
    public ResponseEntity<?> updateAccount(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody UserDto user) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/update-account";
        
        try {
            userService.updateAccount(token, user);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Account updated successfully",
                "path", path
            ));
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> removeAccount(@RequestHeader(value = "Authorization", required = true) String token) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/delete-account";

        try {
            userService.removeAccount(token);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Account deleted successfully",
                "path", path
            ));
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount(@RequestHeader(value = "Authorization", required = true) String accessToken) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/account";
        
        try {
            UserDto userObject = userService.getAccount(accessToken);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", userObject,
                "path", path
            ));
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }
}