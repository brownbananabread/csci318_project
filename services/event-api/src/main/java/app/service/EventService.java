package app.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import app.exception.ServiceException;
import app.model.EventDto;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import java.time.OffsetDateTime;

@Service
public class EventService {

    private final Map<String, EventDto> events = new HashMap<>();
    private final Map<String, List<String>> userEventRegistrations = new HashMap<>();

    public List<EventDto> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    public EventDto getEvent(String eventId) {
        EventDto event = events.get(eventId);
        if (event == null) {
            throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
        }

        EventDto eventWithUsers = copyEvent(event);
        List<String> registeredUsers = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : userEventRegistrations.entrySet()) {
            if (entry.getValue().contains(eventId)) {
                registeredUsers.add(entry.getKey());
            }
        }

        eventWithUsers.setUserIds(registeredUsers);
        return eventWithUsers;
    }

    public String createEvent(String userId, EventDto event) {
        String eventId = UUID.randomUUID().toString();
        event.setId(eventId);
        event.setCreatedBy(userId);
        event.setCurrentParticipants(0);

        events.put(eventId, event);
        return eventId;
    }

    public void updateEvent(String userId, String eventId, EventDto updatedEvent) {
        EventDto existingEvent = events.get(eventId);
        if (existingEvent == null) {
            throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
        }

        if (!existingEvent.getCreatedBy().equals(userId)) {
            throw new ServiceException("You can only modify events you created", HttpStatus.FORBIDDEN);
        }

        if (updatedEvent.getTitle() != null) {
            existingEvent.setTitle(updatedEvent.getTitle());
        }
        if (updatedEvent.getDescription() != null) {
            existingEvent.setDescription(updatedEvent.getDescription());
        }
        if (updatedEvent.getLocation() != null) {
            existingEvent.setLocation(updatedEvent.getLocation());
        }
        if (updatedEvent.getStartTime() != null) {
            existingEvent.setStartTime(updatedEvent.getStartTime());
        }
        if (updatedEvent.getEndTime() != null) {
            existingEvent.setEndTime(updatedEvent.getEndTime());
        }
        if (updatedEvent.getMaxParticipants() > 0) {
            existingEvent.setMaxParticipants(updatedEvent.getMaxParticipants());
        }

        events.put(eventId, existingEvent);
    }

    public void deleteEvent(String userId, String eventId) {
        EventDto event = events.get(eventId);
        if (event == null) {
            throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
        }

        if (!event.getCreatedBy().equals(userId)) {
            throw new ServiceException("You can only delete events you created", HttpStatus.FORBIDDEN);
        }

        events.remove(eventId);

        for (List<String> userEvents : userEventRegistrations.values()) {
            userEvents.remove(eventId);
        }
    }

    public void registerForEvent(String userId, String eventId) {
        EventDto event = events.get(eventId);
        if (event == null) {
            throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
        }

        List<String> userEvents = userEventRegistrations.computeIfAbsent(userId, k -> new ArrayList<>());

        if (userEvents.contains(eventId)) {
            throw new ServiceException("Already registered for this event", HttpStatus.CONFLICT);
        }

        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            throw new ServiceException("Event is at maximum capacity", HttpStatus.CONFLICT);
        }

        userEvents.add(eventId);
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        events.put(eventId, event);
    }

    public void deregisterFromEvent(String userId, String eventId) {
        EventDto event = events.get(eventId);
        if (event == null) {
            throw new ServiceException("Event not found", HttpStatus.NOT_FOUND);
        }

        List<String> userEvents = userEventRegistrations.get(userId);
        if (userEvents == null || !userEvents.contains(eventId)) {
            throw new ServiceException("Not registered for this event", HttpStatus.CONFLICT);
        }

        userEvents.remove(eventId);
        event.setCurrentParticipants(Math.max(0, event.getCurrentParticipants() - 1));
        events.put(eventId, event);
    }

    public List<EventDto> getUserEvents(String userId) {
        return events.values().stream()
                .filter(event -> event.getCreatedBy().equals(userId))
                .toList();
    }

    public List<EventDto> getRegisteredEvents(String userId) {
        List<String> userEventIds = userEventRegistrations.get(userId);
        if (userEventIds == null) {
            return new ArrayList<>();
        }

        return userEventIds.stream()
                .map(events::get)
                .filter(event -> event != null)
                .toList();
    }

    private EventDto copyEvent(EventDto original) {
        EventDto copy = new EventDto();
        copy.setId(original.getId());
        copy.setTitle(original.getTitle());
        copy.setDescription(original.getDescription());
        copy.setLocation(original.getLocation());
        copy.setStartTime(original.getStartTime());
        copy.setEndTime(original.getEndTime());
        copy.setCreatedBy(original.getCreatedBy());
        copy.setMaxParticipants(original.getMaxParticipants());
        copy.setCurrentParticipants(original.getCurrentParticipants());
        return copy;
    }

}