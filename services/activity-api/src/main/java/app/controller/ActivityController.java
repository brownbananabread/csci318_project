package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.model.ActivityDto;
import app.service.ActivityService;
import app.utils.Fetch;

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

    @PostMapping("/activities")
    public ResponseEntity<?> logActivity(@RequestHeader(value = "Authorization", required = true) String bearerToken, @RequestBody Map<String, Object> activityData) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/activities";

        try {
            String userId = Fetch.extractBearerToken(bearerToken);
            String activityId = activityService.logActivity(userId, activityData);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "timestamp", timestamp,
                "status", 201,
                "data", Map.of("activityId", activityId),
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

    @GetMapping("/activities")
    public ResponseEntity<?> getAllActivities() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/activities";

        try {
            List<ActivityDto> activities = activityService.getAllActivities();
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", activities,
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