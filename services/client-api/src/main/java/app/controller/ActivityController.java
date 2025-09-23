package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.service.ActivityService;
import app.utils.ResponseHelper;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/my-activity")
    public ResponseEntity<?> getMyActivities(@RequestHeader(value = "Authorization", required = true) String token) {
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