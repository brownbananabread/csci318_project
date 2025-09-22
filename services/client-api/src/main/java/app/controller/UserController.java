package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.model.UserDto;
import app.service.UserService;
import app.service.ActivityService;

import java.util.Map;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final ActivityService activityService;

    public UserController(UserService userService, ActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/signup";
        
        try {
            String userId = userService.signup(user);
            activityService.logActivity(userId, "USER_SIGNUP", "User signed up successfully", "/api/v1/signup");
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "timestamp", timestamp,
                "status", 201,
                "data", Map.of("accessToken", userId),
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity("unknown", "USER_SIGNUP_FAILED", "User signup failed: " + e.getMessage(), "/api/v1/signup");
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
            activityService.logActivity(userId, "USER_LOGIN", "User logged in successfully", "/api/v1/login");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", Map.of("accessToken", userId),
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity("unknown", "USER_LOGIN_FAILED", "User login failed: " + e.getMessage(), "/api/v1/login");
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
            activityService.logActivity(token, "USER_UPDATE", "User account updated successfully", "/api/v1/update-account");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Account updated successfully",
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "USER_UPDATE_FAILED", "User account update failed: " + e.getMessage(), "/api/v1/update-account");
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
            activityService.logActivity(token, "USER_DELETE", "User account deleted successfully", "/api/v1/delete-account");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Account deleted successfully",
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "USER_DELETE_FAILED", "User account deletion failed: " + e.getMessage(), "/api/v1/delete-account");
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
            activityService.logActivity(accessToken, "USER_PROFILE_VIEW", "User viewed their profile", "/api/v1/account");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", userObject,
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(accessToken, "USER_PROFILE_VIEW_FAILED", "User profile view failed: " + e.getMessage(), "/api/v1/account");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }
}