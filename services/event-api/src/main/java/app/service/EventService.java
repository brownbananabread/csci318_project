package app.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.exception.ServiceException;
import app.model.EventDto;
import app.model.EventRegistration;
import app.repository.EventRepository;
import app.repository.EventRegistrationRepository;
import app.publisher.EventEventPublisher;
import app.events.EventCreatedEvent;
import app.events.UserRegisteredForEventEvent;
import app.events.EventCapacityReachedEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventEventPublisher eventPublisher;

    public EventService(EventRepository eventRepository,
                       EventRegistrationRepository registrationRepository,
                       EventEventPublisher eventPublisher) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll();
    }

    public EventDto getEvent(String eventId) {
        EventDto event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ServiceException("Event not found", HttpStatus.NOT_FOUND));

        EventDto eventWithUsers = copyEvent(event);
        List<String> registeredUsers = registrationRepository.findByEventId(eventId)
            .stream()
            .map(EventRegistration::getUserId)
            .collect(Collectors.toList());

        eventWithUsers.setUserIds(registeredUsers);
        return eventWithUsers;
    }

    public String createEvent(String userId, EventDto event) {
        String eventId = UUID.randomUUID().toString();
        event.setId(eventId);
        event.setCreatedBy(userId);
        event.setCurrentParticipants(0);

        EventDto savedEvent = eventRepository.save(event);

        // Publish domain event for event-driven architecture
        EventCreatedEvent createdEvent = new EventCreatedEvent(
            savedEvent.getId(),
            savedEvent.getTitle(),
            savedEvent.getDescription(),
            savedEvent.getLocation(),
            savedEvent.getCreatedBy(),
            savedEvent.getMaxParticipants(),
            savedEvent.getStartTime(),
            savedEvent.getEndTime(),
            OffsetDateTime.now()
        );
        eventPublisher.publishEventCreated(createdEvent);

        return eventId;
    }

    public void updateEvent(String userId, String eventId, EventDto updatedEvent) {
        EventDto existingEvent = eventRepository.findById(eventId)
            .orElseThrow(() -> new ServiceException("Event not found", HttpStatus.NOT_FOUND));

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

        eventRepository.save(existingEvent);
    }

    @Transactional
    public void deleteEvent(String userId, String eventId) {
        EventDto event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ServiceException("Event not found", HttpStatus.NOT_FOUND));

        if (!event.getCreatedBy().equals(userId)) {
            throw new ServiceException("You can only delete events you created", HttpStatus.FORBIDDEN);
        }

        registrationRepository.deleteByEventId(eventId);
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public void registerForEvent(String userId, String eventId) {
        EventDto event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ServiceException("Event not found", HttpStatus.NOT_FOUND));

        if (registrationRepository.findByUserIdAndEventId(userId, eventId).isPresent()) {
            throw new ServiceException("Already registered for this event", HttpStatus.CONFLICT);
        }

        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            throw new ServiceException("Event is at maximum capacity", HttpStatus.CONFLICT);
        }

        EventRegistration registration = new EventRegistration(userId, eventId);
        registrationRepository.save(registration);

        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);

        // Publish domain event for user registration
        UserRegisteredForEventEvent registeredEvent = new UserRegisteredForEventEvent(
            userId,
            eventId,
            event.getTitle(),
            event.getCurrentParticipants(),
            event.getMaxParticipants(),
            OffsetDateTime.now()
        );
        eventPublisher.publishUserRegisteredForEvent(registeredEvent);

        // Check if event reached capacity and publish event if so
        if (event.getCurrentParticipants() >= event.getMaxParticipants()) {
            EventCapacityReachedEvent capacityEvent = new EventCapacityReachedEvent(
                eventId,
                event.getTitle(),
                event.getMaxParticipants(),
                OffsetDateTime.now()
            );
            eventPublisher.publishEventCapacityReached(capacityEvent);
        }
    }

    @Transactional
    public void deregisterFromEvent(String userId, String eventId) {
        EventDto event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ServiceException("Event not found", HttpStatus.NOT_FOUND));

        EventRegistration registration = registrationRepository.findByUserIdAndEventId(userId, eventId)
            .orElseThrow(() -> new ServiceException("Not registered for this event", HttpStatus.CONFLICT));

        registrationRepository.delete(registration);

        event.setCurrentParticipants(Math.max(0, event.getCurrentParticipants() - 1));
        eventRepository.save(event);
    }

    public List<EventDto> getUserEvents(String userId) {
        return eventRepository.findByCreatedBy(userId);
    }

    public List<EventDto> getRegisteredEvents(String userId) {
        List<String> eventIds = registrationRepository.findByUserId(userId)
            .stream()
            .map(EventRegistration::getEventId)
            .collect(Collectors.toList());

        if (eventIds.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.findAllById(eventIds);
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