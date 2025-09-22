package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.model.ActivityDto;
import app.service.ActivityService;

import java.util.Map;
import java.util.List;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activity/my-activity")
    public ResponseEntity<?> getMyActivities(@RequestHeader(value = "Authorization", required = true) String token) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/activity/my-activity";

        try {
            List<Map<String, Object>> activities = activityService.getUserActivities(token);

            activityService.logActivity(token, "ACTIVITY_VIEW_MY", "User viewed their activity log", "/api/v1/activity/my-activity");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", activities,
                "path", path
            ));
        } catch (Exception e) {
            activityService.logActivity(token, "ACTIVITY_VIEW_MY_FAILED", "Failed to view activity log: " + e.getMessage(), "/api/v1/activity/my-activity");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", timestamp,
                "status", 500,
                "error", e.getMessage(),
                "path", path
            ));
        }
    }
}