package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.model.UserDto;
import app.service.UserService;
import app.service.ActivityService;
import app.utils.ResponseHelper;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ActivityService activityService;

    public UserController(UserService userService, ActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user) {
        String path = "/users/signup";

        try {
            String userId = userService.signup(user);
            activityService.logActivity(userId, "USER_SIGNUP", "User signed up successfully", path);
            return ResponseHelper.createResponse(HttpStatus.CREATED, path, "User signed up successfully", Map.of("accessToken", userId));
        } catch (ServiceException e) {
            activityService.logActivity("unknown", "USER_SIGNUP_FAILED", "User signup failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to create account at this time. Please try again later.", null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDto user) {
        String path = "/users/login";

        try {
            String userId = userService.login(user);
            activityService.logActivity(userId, "USER_LOGIN", "User logged in successfully", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "User logged in successfully", Map.of("accessToken", userId));
        } catch (ServiceException e) {
            activityService.logActivity("unknown", "USER_LOGIN_FAILED", "User login failed: " + e.getMessage(), path);
            HttpStatus statusCode = e.getStatus() == HttpStatus.UNAUTHORIZED ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
            String message = statusCode == HttpStatus.UNAUTHORIZED ? "Invalid credentials." : "Login service temporarily unavailable. Please try again later.";
            return ResponseHelper.createResponse(statusCode, path, message, null);
        }
    }

    @PatchMapping("/update-account")
    public ResponseEntity<?> updateAccount(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody UserDto user) {
        String path = "/users/update-account";
        
        try {
            userService.updateAccount(token, user);
            activityService.logActivity(token, "USER_UPDATE", "User account updated successfully", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Account updated successfully", null);
        } catch (ServiceException e) {
            activityService.logActivity(token, "USER_UPDATE_FAILED", "User account update failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to update account at this time. Please try again later.", null);
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> removeAccount(@RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/users/delete-account";

        try {
            userService.removeAccount(token);
            activityService.logActivity(token, "USER_DELETE", "User account deleted successfully", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Account deleted successfully", null);
        } catch (ServiceException e) {
            activityService.logActivity(token, "USER_DELETE_FAILED", "User account deletion failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to delete account at this time. Please try again later.", null);
        }
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAccount(@RequestHeader(value = "Authorization", required = true) String accessToken) {
        String path = "/users/account";

        try {
            UserDto userObject = userService.getAccount(accessToken);
            activityService.logActivity(accessToken, "USER_PROFILE_VIEW", "User viewed their profile", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Account retrieved successfully", userObject);
        } catch (ServiceException e) {
            activityService.logActivity(accessToken, "USER_PROFILE_VIEW_FAILED", "User profile view failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to retrieve account information at this time. Please try again later.", null);
        }
    }
}