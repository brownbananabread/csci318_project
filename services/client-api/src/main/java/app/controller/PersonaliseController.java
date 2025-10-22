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

import app.service.ActivityService;
import app.service.PersonaliseService;
import app.utils.ResponseHelper;
import app.utils.Fetch;
import app.exception.ServiceException;

import java.util.Map;

@RestController
@RequestMapping("/personalise")
@Tag(name = "Personalise", description = "AI-powered personalization and chat features")
public class PersonaliseController {

    private final ActivityService activityService;
    private final PersonaliseService personaliseService;

    public PersonaliseController(ActivityService activityService, PersonaliseService personaliseService) {
        this.activityService = activityService;
        this.personaliseService = personaliseService;
    }

    @Operation(
        summary = "Get AI-powered event summary",
        description = "Returns an AI-generated summary of user's registered events and activity",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Summary generated successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @GetMapping("/summary")
    public ResponseEntity<?> summary(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/personalise/summary";

        try {
            // Extract user ID from Bearer token
            String userId = Fetch.extractBearerToken(token);

            // Log the summary activity
            activityService.logActivity(userId, "SUMMARY_REQUEST", "User requested summary", path);

            // Process summary through personalise service
            Map<String, Object> responseData = personaliseService.processSummary(userId);

            return ResponseHelper.createResponse(HttpStatus.OK, path, "Summary processed successfully", responseData);
        } catch (ServiceException e) {
            activityService.logActivity(token, "SUMMARY_REQUEST_FAILED", "Summary request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Summary service temporarily unavailable. Please try again later.", null);
        } catch (Exception e) {
            activityService.logActivity(token, "SUMMARY_REQUEST_FAILED", "Summary request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Summary service temporarily unavailable. Please try again later.", null);
        }
    }

    @Operation(
        summary = "Chat with AI assistant",
        description = "Send a message to the AI assistant for event recommendations and queries",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Chat message processed successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "path": "/personalise/chat",
                      "status": 200,
                      "message": "Chat processed successfully",
                      "data": {
                        "message": "Hello, what can you do?",
                        "response": "I can help you with events! I can show you events you've created, events you're registered for, recommend new events, and answer questions about available events.",
                        "agenticBehavior": "The AI agent autonomously selected which tools to call based on your question"
                      },
                      "timestamp": "2025-10-22T22:00:00+11:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid message format"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PostMapping("/chat")
    public ResponseEntity<?> chat(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @RequestBody(
                description = "Chat message payload",
                content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                        value = """
                        {
                          "message": "Hello, what can you do?"
                        }
                        """
                    )
                )
            )
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> chatData) {
        String path = "/personalise/chat";

        try {
            // Extract user ID from Bearer token
            String userId = Fetch.extractBearerToken(token);

            // Extract message from request body
            String message = (String) chatData.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseHelper.createResponse(HttpStatus.BAD_REQUEST, path, "Message is required", null);
            }

            // Log the chat activity
            activityService.logActivity(userId, "CHAT_MESSAGE", "User sent a chat message: " + message, path);

            // Process chat through personalise service
            Map<String, Object> responseData = personaliseService.processChat(userId, chatData);

            return ResponseHelper.createResponse(HttpStatus.OK, path, "Chat processed successfully", responseData);
        } catch (ServiceException e) {
            activityService.logActivity(token, "CHAT_MESSAGE_FAILED", "Chat message failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Chat service temporarily unavailable. Please try again later.", null);
        } catch (Exception e) {
            activityService.logActivity(token, "CHAT_MESSAGE_FAILED", "Chat message failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Chat service temporarily unavailable. Please try again later.", null);
        }
    }

    @Operation(
        summary = "Get my registered events",
        description = "Returns all events the user has registered for",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @GetMapping("/my-events")
    public ResponseEntity<?> myEvents(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/personalise/my-events";

        try {
            // Extract user ID from Bearer token
            String userId = Fetch.extractBearerToken(token);

            activityService.logActivity(userId, "MY_EVENTS_REQUEST", "User requested their registered events", path);
            Map<String, Object> responseData = personaliseService.processMyEvents(userId);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "My events retrieved successfully", responseData);
        } catch (ServiceException e) {
            activityService.logActivity(token, "MY_EVENTS_REQUEST_FAILED", "My events request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to retrieve your events at this time. Please try again later.", null);
        } catch (Exception e) {
            activityService.logActivity(token, "MY_EVENTS_REQUEST_FAILED", "My events request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Unable to retrieve your events at this time. Please try again later.", null);
        }
    }

    @Operation(
        summary = "Get AI-recommended events",
        description = "Returns personalized event recommendations based on user preferences and activity using AI",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @GetMapping("/recommended-events")
    public ResponseEntity<?> recommendedEvents(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/personalise/recommended-events";

        try {
            // Extract user ID from Bearer token
            String userId = Fetch.extractBearerToken(token);

            activityService.logActivity(userId, "RECOMMENDED_EVENTS_REQUEST", "User requested event recommendations", path);
            Map<String, Object> responseData = personaliseService.processRecommendedEvents(userId);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Recommendations retrieved successfully", responseData);
        } catch (ServiceException e) {
            activityService.logActivity(token, "RECOMMENDED_EVENTS_REQUEST_FAILED", "Recommended events request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to retrieve recommendations at this time. Please try again later.", null);
        } catch (Exception e) {
            activityService.logActivity(token, "RECOMMENDED_EVENTS_REQUEST_FAILED", "Recommended events request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Unable to retrieve recommendations at this time. Please try again later.", null);
        }
    }
}