package app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
@Tag(name = "Users", description = "User authentication and account management")
public class UserController {

    private final UserService userService;
    private final ActivityService activityService;

    public UserController(UserService userService, ActivityService activityService) {
        this.userService = userService;
        this.activityService = activityService;
    }

    @Operation(summary = "Sign up a new user", description = "Create a new user account")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "path": "/users/signup",
                      "status": 201,
                      "message": "User signed up successfully",
                      "data": {
                        "accessToken": "1"
                      },
                      "timestamp": "2025-10-22T22:00:00+11:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody(
                description = "User signup details",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                          "email": "john.doe@example.com",
                          "password": "mySecurePassword123",
                          "name": "John Doe"
                        }
                        """
                    )
                )
            )
            @org.springframework.web.bind.annotation.RequestBody UserDto user) {
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

    @Operation(summary = "User login", description = "Authenticate user and return access token")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "path": "/users/login",
                      "status": 200,
                      "message": "User logged in successfully",
                      "data": {
                        "accessToken": "1"
                      },
                      "timestamp": "2025-10-22T22:00:00+11:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody(
                description = "User login credentials",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                          "email": "john.doe@example.com",
                          "password": "mySecurePassword123"
                        }
                        """
                    )
                )
            )
            @org.springframework.web.bind.annotation.RequestBody UserDto user) {
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

    @Operation(
        summary = "Update user account",
        description = "Update current user's account information",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PatchMapping("/update-account")
    public ResponseEntity<?> updateAccount(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @RequestBody UserDto user) {
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

    @Operation(
        summary = "Delete user account",
        description = "Delete current user's account",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @DeleteMapping("/delete-account")
    public ResponseEntity<?> removeAccount(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
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

    @Operation(
        summary = "Get user account details",
        description = "Retrieve current user's account information",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @GetMapping("/account")
    public ResponseEntity<?> getAccount(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String accessToken) {
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