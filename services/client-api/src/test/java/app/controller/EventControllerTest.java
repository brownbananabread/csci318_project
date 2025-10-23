package app.controller;

import app.controllers.EventController;
import app.models.EventDto;
import app.services.ActivityService;
import app.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.OffsetDateTime;
import java.util.Arrays;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private ActivityService activityService;

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

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Test Event"));

        verify(eventService).getAllEvents();
    }

    @Test
    void getEventById_WithValidId_ReturnsEvent() throws Exception {
        when(eventService.getEvent("1")).thenReturn(testEvent);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Test Event"));

        verify(eventService).getEvent("1");
    }

    @Test
    void createEvent_WithValidData_CreatesEvent() throws Exception {
        when(eventService.createEvent(eq("1"), any(EventDto.class))).thenReturn("1");

        mockMvc.perform(post("/events")
                .header("Authorization", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Test Event\",\"description\":\"Test Description\",\"location\":\"Test Location\",\"startTime\":\"2025-11-01T10:00:00Z\",\"endTime\":\"2025-11-01T12:00:00Z\",\"maxParticipants\":100}"))
                .andExpect(status().isCreated());

        verify(eventService).createEvent(eq("1"), any(EventDto.class));
    }

    @Test
    void registerForEvent_CallsService() throws Exception {
        mockMvc.perform(post("/events/1/register")
                .header("Authorization", "2"))
                .andExpect(status().isOk());

        verify(eventService).registerForEvent("2", "1");
    }

    @Test
    void deregisterFromEvent_CallsService() throws Exception {
        mockMvc.perform(delete("/events/1/register")
                .header("Authorization", "2"))
                .andExpect(status().isOk());

        verify(eventService).deregisterFromEvent("2", "1");
    }
}
