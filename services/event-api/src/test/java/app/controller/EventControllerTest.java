package app.controller;

import app.model.EventDto;
import app.repository.EventRepository;
import app.publisher.EventEventPublisher;
import app.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private EventEventPublisher eventPublisher;

    private EventDto testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new EventDto();
        testEvent.setId("1");
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setLocation("Test Location");
        testEvent.setCreatedBy("1");
        testEvent.setStartTime(OffsetDateTime.now().plusDays(1));
        testEvent.setEndTime(OffsetDateTime.now().plusDays(1).plusHours(2));
        testEvent.setMaxParticipants(100);
        testEvent.setCurrentParticipants(0);
    }

    @Test
    void getAllEvents_ReturnsEventList() throws Exception {
        when(eventService.getAllEvents()).thenReturn(Arrays.asList(testEvent));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Event"));

        verify(eventService).getAllEvents();
    }

    @Test
    void getEventById_WithValidId_ReturnsEvent() throws Exception {
        when(eventService.getEvent("1")).thenReturn(testEvent);

        mockMvc.perform(get("/api/v1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Event"));

        verify(eventService).getEvent("1");
    }

    @Test
    void createEvent_WithValidData_CreatesEvent() throws Exception {
        when(eventService.createEvent(anyString(), any(EventDto.class))).thenReturn("1");

        mockMvc.perform(post("/api/v1/events")
                .header("Authorization", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Event\",\"description\":\"Test Description\",\"location\":\"Test Location\",\"startTime\":\"2025-11-01T10:00:00Z\",\"endTime\":\"2025-11-01T12:00:00Z\",\"maxParticipants\":100}"))
                .andExpect(status().isCreated());

        verify(eventService, atLeastOnce()).createEvent(anyString(), any(EventDto.class));
    }

    @Test
    void registerForEvent_WithAvailableCapacity_RegistersUser() throws Exception {
        doNothing().when(eventService).registerForEvent("2", "1");

        mockMvc.perform(post("/api/v1/events/1/register")
                .header("Authorization", "2"))
                .andExpect(status().isOk());

        verify(eventService).registerForEvent("2", "1");
    }

    @Test
    void registerForEvent_WhenFull_ReturnsBadRequest() throws Exception {
        doThrow(new app.exception.ServiceException("Event is full", org.springframework.http.HttpStatus.BAD_REQUEST))
                .when(eventService).registerForEvent("2", "1");

        mockMvc.perform(post("/api/v1/events/1/register")
                .header("Authorization", "2"))
                .andExpect(status().isBadRequest());

        verify(eventService).registerForEvent("2", "1");
    }

    @Test
    void unregisterFromEvent_WithRegisteredUser_UnregistersUser() throws Exception {
        doNothing().when(eventService).deregisterFromEvent("2", "1");

        mockMvc.perform(delete("/api/v1/events/1/register")
                .header("Authorization", "2"))
                .andExpect(status().isOk());

        verify(eventService).deregisterFromEvent("2", "1");
    }
}
