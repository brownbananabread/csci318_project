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

@RestController
@RequestMapping("/api/v1")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping("/activities")
    public ResponseEntity<?> logActivity(@RequestHeader("Authorization") String bearerToken, 
                                          @RequestBody Map<String, Object> activityData) {
        try {
            String userId = Fetch.extractBearerToken(bearerToken);
            String activityId = activityService.logActivity(userId, activityData);
            return ResponseEntity.status(HttpStatus.CREATED).body(activityId);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/activities")
    public ResponseEntity<?> getAllActivities() {
        try {
            List<ActivityDto> activities = activityService.getAllActivities();
            return ResponseEntity.ok(activities);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }
}