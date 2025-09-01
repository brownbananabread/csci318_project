package server.controller;

import server.model.UserContext;
import server.service.*;
import server.factory.ServiceFactory;
import server.decorator.CookieResponseDecorator;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MainController extends BaseController {
    @Autowired private ServiceFactory serviceFactory;
    @Autowired private CookieResponseDecorator cookieDecorator;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }
        
        try {
            UserService userService = serviceFactory.createUserService();
            Map<String, Object> user = userService.login(email, password);
            ResponseEntity<?> response = ResponseEntity.ok(user);
            return cookieDecorator.decorateWithCookie(response, String.valueOf(user.get("userId")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String email) {
        if (email == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }
        
        try {
            UserService userService = serviceFactory.createUserService();
            boolean isValid = userService.validateEmail(email);
            return isValid 
                ? ResponseEntity.ok(Map.of("message", "Email is valid"))
                : ResponseEntity.badRequest().body(Map.of("message", "Email is invalid"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> credentials) {
        try {
            UserService userService = serviceFactory.createUserService();
            Map<String, Object> user = userService.register(credentials);
            ResponseEntity<?> response = ResponseEntity.ok(user);
            return cookieDecorator.decorateWithCookie(response, String.valueOf(user.get("userId")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            UserService userService = serviceFactory.createUserService();
            return ResponseEntity.ok(userService.getProfile(context.userId));
        });
    }
    
    @GetMapping("/rating")
    public ResponseEntity<?> getRating(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            UserService userService = serviceFactory.createUserService();
            return ResponseEntity.ok(userService.getRatings(context.userId));
        });
    }
    
    @GetMapping("/rate")
    public ResponseEntity<?> rate(
            @RequestParam("userid") String userId,
            @RequestParam("rating") Integer rating,
            @CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            UserService userService = serviceFactory.createUserService();
            return ResponseEntity.ok(userService.submitRating(context.userId, userId, rating));
        });
    }
    
    @GetMapping("/jobs")
    public ResponseEntity<?> getJobs(
            @RequestParam(value = "status", required = false) String status,
            @CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            JobService jobService = serviceFactory.createJobService();
            return ResponseEntity.ok(jobService.getJobs(context, status));
        });
    }
    
    @PutMapping("/jobs/{jobId}/status")
    public ResponseEntity<?> updateJobStatus(
            @PathVariable String jobId,
            @RequestBody Map<String, String> body,
            @CookieValue(name = "accessToken", required = false) String accessToken) {
        String newStatus = body.get("status");
        if (newStatus == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status is required"));
        }
        
        return executeAuthenticatedRequest(accessToken, context -> {
            JobService jobService = serviceFactory.createJobService();
            return ResponseEntity.ok(jobService.updateJobStatus(context, jobId, newStatus));
        });
    }
    
    @PostMapping("/jobs")
    public ResponseEntity<?> createJob(
            @RequestBody Map<String, Object> jobData,
            @CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            if (!context.isCustomer()) {
                throw new RuntimeException("Only customers can create jobs");
            }
            JobService jobService = serviceFactory.createJobService();
            return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(context.userId, jobData));
        });
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            StatsService statsService = serviceFactory.createStatsService();
            return ResponseEntity.ok(statsService.getStats(context));
        });
    }
    
    @GetMapping("/activity")
    public ResponseEntity<?> getActivity(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            ActivityService activityService = serviceFactory.createActivityService();
            return ResponseEntity.ok(activityService.getActivity(context.userId));
        });
    }
    
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            if (!context.isAdmin()) {
                throw new RuntimeException("Admin access required");
            }
            UserService userService = serviceFactory.createUserService();
            return ResponseEntity.ok(userService.getAllUsers());
        });
    }
    
    @GetMapping("/all-jobs")
    public ResponseEntity<?> getAllJobs(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            if (!context.isAdmin()) {
                throw new RuntimeException("Admin access required");
            }
            JobService jobService = serviceFactory.createJobService();
            return ResponseEntity.ok(jobService.getAllJobs());
        });
    }
    
    @GetMapping("/all-activity")
    public ResponseEntity<?> getAllActivity(@CookieValue(name = "accessToken", required = false) String accessToken) {
        return executeAuthenticatedRequest(accessToken, context -> {
            if (!context.isAdmin()) {
                throw new RuntimeException("Admin access required");
            }
            ActivityService activityService = serviceFactory.createActivityService();
            return ResponseEntity.ok(activityService.getAllActivities());
        });
    }
    
    @Override
    protected void validateAuthorization(UserContext context) {}
}