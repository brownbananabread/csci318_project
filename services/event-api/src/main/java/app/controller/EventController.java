package app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import app.exception.ServiceException;
import app.model.EventDto;
import app.service.EventService;
import java.util.Map;
import java.util.List;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<?> getAllEvents() {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events";

        try {
            List<EventDto> events = eventService.getAllEvents();
            return ResponseEntity.status(HttpStatus.OK).body(events);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/events/{eventId}")
    public ResponseEntity<?> getEvent(@PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId;

        try {
            EventDto event = eventService.getEvent(eventId);
            return ResponseEntity.status(HttpStatus.OK).body(event);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestHeader(value = "Authorization", required = true) String userId, @RequestBody EventDto event) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events";

        try {
            String eventId = eventService.createEvent(userId, event);
            return ResponseEntity.status(HttpStatus.CREATED).body(eventId);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @PatchMapping("/events/{eventId}")
    public ResponseEntity<?> updateEvent(@RequestHeader(value = "Authorization", required = true) String userId, @PathVariable String eventId, @RequestBody EventDto event) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId;

        try {
            eventService.updateEvent(userId, eventId, event);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<?> deleteEvent(@RequestHeader(value = "Authorization", required = true) String userId, @PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId;

        try {
            eventService.deleteEvent(userId, eventId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @PostMapping("/events/{eventId}/register")
    public ResponseEntity<?> registerForEvent(@RequestHeader(value = "Authorization", required = true) String userId, @PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId + "/register";

        try {
            eventService.registerForEvent(userId, eventId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @DeleteMapping("/events/{eventId}/register")
    public ResponseEntity<?> deregisterFromEvent(@RequestHeader(value = "Authorization", required = true) String userId, @PathVariable String eventId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/" + eventId + "/register";

        try {
            eventService.deregisterFromEvent(userId, eventId);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/events/my-events")
    public ResponseEntity<?> getUserEvents(@RequestHeader(value = "Authorization", required = true) String userId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/my-events";

        try {
            List<EventDto> events = eventService.getUserEvents(userId);
            return ResponseEntity.status(HttpStatus.OK).body(events);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }

    @GetMapping("/events/registered")
    public ResponseEntity<?> getRegisteredEvents(@RequestHeader(value = "Authorization", required = true) String userId) {
        OffsetDateTime timestamp = OffsetDateTime.now();
        String path = "/api/v1/events/registered";

        try {
            List<EventDto> events = eventService.getRegisteredEvents(userId);
            return ResponseEntity.status(HttpStatus.OK).body(events);
        } catch (ServiceException e) {
            return ResponseEntity.status(e.getStatus()).build();
        }
    }
}