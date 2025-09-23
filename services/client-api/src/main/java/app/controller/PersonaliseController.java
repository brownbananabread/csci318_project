package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.service.ActivityService;
import app.service.PersonaliseService;
import app.utils.ResponseHelper;
import app.exception.ServiceException;

import java.util.Map;

@RestController
@RequestMapping("/personalise")
public class PersonaliseController {

    private final ActivityService activityService;
    private final PersonaliseService personaliseService;

    public PersonaliseController(ActivityService activityService, PersonaliseService personaliseService) {
        this.activityService = activityService;
        this.personaliseService = personaliseService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary(@RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/personalise/summary";

        try {
            // Log the summary activity
            activityService.logActivity(token, "SUMMARY_REQUEST", "User requested summary", path);

            // Process summary through personalise service
            Map<String, Object> responseData = personaliseService.processSummary(token);

            return ResponseHelper.createResponse(HttpStatus.OK, path, "Summary processed successfully", responseData);
        } catch (ServiceException e) {
            activityService.logActivity(token, "SUMMARY_REQUEST_FAILED", "Summary request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Summary service temporarily unavailable. Please try again later.", null);
        } catch (Exception e) {
            activityService.logActivity(token, "SUMMARY_REQUEST_FAILED", "Summary request failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Summary service temporarily unavailable. Please try again later.", null);
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody Map<String, Object> chatData) {
        String path = "/personalise/chat";

        try {
            // Extract message from request body
            String message = (String) chatData.get("message");
            if (message == null || message.trim().isEmpty()) {
                return ResponseHelper.createResponse(HttpStatus.BAD_REQUEST, path, "Message is required", null);
            }

            // Log the chat activity
            activityService.logActivity(token, "CHAT_MESSAGE", "User sent a chat message: " + message, path);

            // Process chat through personalise service
            Map<String, Object> responseData = personaliseService.processChat(token, chatData);

            return ResponseHelper.createResponse(HttpStatus.OK, path, "Chat processed successfully", responseData);
        } catch (ServiceException e) {
            activityService.logActivity(token, "CHAT_MESSAGE_FAILED", "Chat message failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Chat service temporarily unavailable. Please try again later.", null);
        } catch (Exception e) {
            activityService.logActivity(token, "CHAT_MESSAGE_FAILED", "Chat message failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Chat service temporarily unavailable. Please try again later.", null);
        }
    }
}