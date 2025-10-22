package app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.service.ActivityService;
import app.utils.ResponseHelper;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/activity")
@Tag(name = "Activity", description = "Activity logging and audit trail")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Operation(
        summary = "Get user activity log",
        description = "Retrieve the current user's activity history",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Activities retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/my-activity")
    public ResponseEntity<?> getMyActivities(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/activity/my-activity";

        try {
            List<Map<String, Object>> activities = activityService.getUserActivities(token);

            activityService.logActivity(token, "ACTIVITY_VIEW_MY", "User viewed their activity log", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Activities retrieved successfully", activities);
        } catch (Exception e) {
            System.err.println("ActivityController error: " + e.getMessage());
            e.printStackTrace();
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Unable to retrieve activity log at this time. Please try again later.", List.of());
        }
    }
}