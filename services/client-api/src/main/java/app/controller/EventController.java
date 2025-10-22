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

import app.exception.ServiceException;
import app.model.EventDto;
import app.service.EventService;
import app.service.ActivityService;
import app.utils.ResponseHelper;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Event management and registration")
public class EventController {

    private final EventService eventService;
    private final ActivityService activityService;

    public EventController(EventService eventService, ActivityService activityService) {
        this.eventService = eventService;
        this.activityService = activityService;
    }

    @Operation(summary = "Get all events", description = "Retrieve all available events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Events retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

    @Operation(
        summary = "Create new event",
        description = "Create a new event (requires authentication)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Event created successfully"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PostMapping()
    public ResponseEntity<?> createEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @RequestBody EventDto event) {
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

    @Operation(
        summary = "Update event",
        description = "Update an existing event (requires authentication and ownership)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @PathVariable String eventId,
            @RequestBody EventDto event) {
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

    @Operation(
        summary = "Delete event",
        description = "Delete an event (requires authentication and ownership)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @PathVariable String eventId) {
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

    @Operation(
        summary = "Register for event",
        description = "Register the current user for an event",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully registered"),
        @ApiResponse(responseCode = "503", description = "Service temporarily unavailable")
    })
    @PostMapping("/{eventId}/register")
    public ResponseEntity<?> registerForEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @Parameter(description = "Event ID", required = true)
            @PathVariable String eventId) {
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

    @Operation(
        summary = "Deregister from event",
        description = "Remove registration from an event",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{eventId}/register")
    public ResponseEntity<?> deregisterFromEvent(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token,
            @PathVariable String eventId) {
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

    @Operation(
        summary = "Get my created events",
        description = "Get all events created by the current user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/my-events")
    public ResponseEntity<?> getUserEvents(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
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

    @Operation(
        summary = "Get registered events",
        description = "Get all events the current user has registered for",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/registered")
    public ResponseEntity<?> getRegisteredEvents(
            @Parameter(hidden = true)
            @RequestHeader(value = "Authorization", required = true) String token) {
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