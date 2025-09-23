package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import app.exception.ServiceException;
import app.model.EventDto;
import app.service.EventService;
import app.service.ActivityService;
import app.utils.ResponseHelper;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final ActivityService activityService;

    public EventController(EventService eventService, ActivityService activityService) {
        this.eventService = eventService;
        this.activityService = activityService;
    }

    @GetMapping()
    public ResponseEntity<?> getAllEvents() {
        String path = "/events";

        try {
            List<EventDto> events = eventService.getAllEvents();
            activityService.logActivity("anonymous", "EVENTS_VIEW_ALL", "User viewed all events", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Events retrieved successfully", events);
        } catch (ServiceException e) {
            activityService.logActivity("anonymous", "EVENTS_VIEW_ALL_FAILED", "Failed to view all events: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Unable to retrieve events at this time. Please try again later.", List.of());
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable String eventId) {
        String path = "/events/" + eventId;

        try {
            EventDto event = eventService.getEvent(eventId);
            activityService.logActivity("anonymous", "EVENT_VIEW", "User viewed event details", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Event retrieved successfully", event);
        } catch (ServiceException e) {
            activityService.logActivity("anonymous", "EVENT_VIEW_FAILED", "Failed to view event: " + e.getMessage(), path);
            HttpStatus statusCode = e.getStatus() == HttpStatus.NOT_FOUND ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            String message = statusCode == HttpStatus.NOT_FOUND ? "Event not found." : "Unable to retrieve event at this time. Please try again later.";
            return ResponseHelper.createResponse(statusCode, path, message, null);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createEvent(@RequestHeader(value = "Authorization", required = true) String token, @RequestBody EventDto event) {
        String path = "/events";
        try {
            String eventId = eventService.createEvent(token, event);
            activityService.logActivity(token, "EVENT_CREATE", "User created a new event: " + event.getTitle(), path);
            return ResponseHelper.createResponse(HttpStatus.CREATED, path, "Event created successfully", Map.of("eventId", eventId));
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_CREATE_FAILED", "Event creation failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to create event at this time. Please try again later.", null);
        }
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId, @RequestBody EventDto event) {
        String path = "/events/" + eventId;

        try {
            eventService.updateEvent(token, eventId, event);
            activityService.logActivity(token, "EVENT_UPDATE", "User updated an event", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Event updated successfully", null);
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_UPDATE_FAILED", "Event update failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to update event at this time. Please try again later.", null);
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId) {
        String path = "/events/" + eventId;

        try {
            eventService.deleteEvent(token, eventId);
            activityService.logActivity(token, "EVENT_DELETE", "User deleted an event", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Event deleted successfully", null);
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_DELETE_FAILED", "Event deletion failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to delete event at this time. Please try again later.", null);
        }
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<?> registerForEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId) {
        String path = "/events/" + eventId + "/register";

        try {
            eventService.registerForEvent(token, eventId);
            activityService.logActivity(token, "EVENT_REGISTER", "User registered for an event", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Successfully registered for event", null);
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_REGISTER_FAILED", "Event registration failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to register for event at this time. Please try again later.", null);
        }
    }

    @DeleteMapping("/{eventId}/register")
    public ResponseEntity<?> deregisterFromEvent(@RequestHeader(value = "Authorization", required = true) String token, @PathVariable String eventId) {
        String path = "/events/" + eventId + "/register";

        try {
            eventService.deregisterFromEvent(token, eventId);
            activityService.logActivity(token, "EVENT_DEREGISTER", "User deregistered from an event", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Successfully deregistered from event", null);
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENT_DEREGISTER_FAILED", "Event deregistration failed: " + e.getMessage(), path);
            return ResponseHelper.createResponse(e.getStatus(), path, "Unable to deregister from event at this time. Please try again later.", null);
        }
    }

    @GetMapping("/my-events")
    public ResponseEntity<?> getUserEvents(@RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/my-events";

        try {
            List<EventDto> events = eventService.getUserEvents(token);
            activityService.logActivity(token, "EVENTS_VIEW_MY", "User viewed their events", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "User events retrieved successfully", events);
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENTS_VIEW_MY_FAILED", "Failed to view user events: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Unable to retrieve your events at this time. Please try again later.", List.of());
        }
    }

    @GetMapping("/registered")
    public ResponseEntity<?> getRegisteredEvents(@RequestHeader(value = "Authorization", required = true) String token) {
        String path = "/registered";

        try {
            List<EventDto> events = eventService.getRegisteredEvents(token);
            activityService.logActivity(token, "EVENTS_VIEW_REGISTERED", "User viewed their registered events", path);
            return ResponseHelper.createResponse(HttpStatus.OK, path, "Registered events retrieved successfully", events);
        } catch (ServiceException e) {
            activityService.logActivity(token, "EVENTS_VIEW_REGISTERED_FAILED", "Failed to view registered events: " + e.getMessage(), path);
            return ResponseHelper.createResponse(HttpStatus.INTERNAL_SERVER_ERROR, path, "Unable to retrieve registered events at this time. Please try again later.", List.of());
        }
    }
}