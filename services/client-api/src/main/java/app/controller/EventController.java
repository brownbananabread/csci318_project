package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.model.EventDto;
import app.service.EventService;
import app.service.ActivityService;

import java.util.Map;
import java.util.List;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;
    private final ActivityService activityService;

    public EventController(EventService eventService, ActivityService activityService) {
        this.eventService = eventService;
        this.activityService = activityService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events";

        try {
            List<EventDto> events = eventService.getAllEvents();
            activityService.logActivity("anonymous", "EVENTS_VIEW_ALL", "User viewed all events", "/api/v1/events");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", events,
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity("anonymous", "EVENTS_VIEW_ALL_FAILED", "Failed to view all events: " + e.getMessage(), "/api/v1/events");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId;

        try {
            EventDto event = eventService.getEvent(eventId);
            activityService.logActivity("anonymous", "EVENT_VIEW", "User viewed event details", "/api/v1/events/" + eventId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", event,
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity("anonymous", "EVENT_VIEW_FAILED", "Failed to view event: " + e.getMessage(), "/api/v1/events/" + eventId);
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody EventDto event) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events";

        try {
            String eventId = eventService.createEvent(token, event);
            activityService.logActivity(token, "EVENT_CREATE", "User created a new event: " + event.getTitle(), "/api/v1/events");
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "timestamp", timestamp,
                "status", 201,
                "data", Map.of("eventId", eventId),
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_CREATE_FAILED", "Event creation failed: " + e.getMessage(), "/api/v1/events");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId, @RequestBody EventDto event) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId;

        try {
            eventService.updateEvent(token, eventId, event);
            activityService.logActivity(token, "EVENT_UPDATE", "User updated an event", "/api/v1/events/" + eventId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Event updated successfully",
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_UPDATE_FAILED", "Event update failed: " + e.getMessage(), "/api/v1/events/" + eventId);
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<?> deleteEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId;

        try {
            eventService.deleteEvent(token, eventId);
            activityService.logActivity(token, "EVENT_DELETE", "User deleted an event", "/api/v1/events/" + eventId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Event deleted successfully",
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_DELETE_FAILED", "Event deletion failed: " + e.getMessage(), "/api/v1/events/" + eventId);
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<?> registerForEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId + "/register";

        try {
            eventService.registerForEvent(token, eventId);
            activityService.logActivity(token, "EVENT_REGISTER", "User registered for an event", "/api/v1/events/" + eventId + "/register");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Successfully registered for event",
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_REGISTER_FAILED", "Event registration failed: " + e.getMessage(), "/api/v1/events/" + eventId + "/register");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @DeleteMapping("/events/{eventId}/register")
    public ResponseEntity<?> deregisterFromEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId + "/register";

        try {
            eventService.deregisterFromEvent(token, eventId);
            activityService.logActivity(token, "EVENT_DEREGISTER", "User deregistered from an event", "/api/v1/events/" + eventId + "/register");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "message", "Successfully deregistered from event",
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_DEREGISTER_FAILED", "Event deregistration failed: " + e.getMessage(), "/api/v1/events/" + eventId + "/register");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @GetMapping("/events/my-events")
    public ResponseEntity<?> getUserEvents(@RequestHeader(value = "Authorization", required = true) String token) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/my-events";

        try {
            List<EventDto> events = eventService.getUserEvents(token);
            activityService.logActivity(token, "EVENTS_VIEW_MY", "User viewed their events", "/api/v1/events/my-events");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", events,
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENTS_VIEW_MY_FAILED", "Failed to view user events: " + e.getMessage(), "/api/v1/events/my-events");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }

    @GetMapping("/events/registered")
    public ResponseEntity<?> getRegisteredEvents(@RequestHeader(value = "Authorization", required = true) String token) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/registered";

        try {
            List<EventDto> events = eventService.getRegisteredEvents(token);
            activityService.logActivity(token, "EVENTS_VIEW_REGISTERED", "User viewed their registered events", "/api/v1/events/registered");
            return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "timestamp", timestamp,
                "status", 200,
                "data", events,
                "path", path
            ));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENTS_VIEW_REGISTERED_FAILED", "Failed to view registered events: " + e.getMessage(), "/api/v1/events/registered");
            return ResponseEntity.status(e.getStatus()).body(Map.of(
                "timestamp", timestamp,
                "status", e.getStatus().value(),
                "error", e.getMessage(),
                "path", path
            ));
        }
    }
}