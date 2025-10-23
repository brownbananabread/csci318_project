package app.service;

import app.exception.ServiceException;
import app.model.ActivityDto;
import app.model.ActivityEntity;
import app.repository.ActivityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    private ActivityEntity testActivity;

    @BeforeEach
    void setUp() {
        testActivity = new ActivityEntity("1", "{\"eventId\":\"100\",\"action\":\"created\"}");
        testActivity.setId(1L);
    }

    @Test
    void getActivitiesByUserId_ReturnsActivitiesList() throws ServiceException {
        when(activityRepository.findByUserIdOrderByIdDesc("1"))
                .thenReturn(Arrays.asList(testActivity));

        List<ActivityDto> activities = activityService.getActivitiesByUserId("1");

        assertNotNull(activities);
        assertEquals(1, activities.size());
        verify(activityRepository).findByUserIdOrderByIdDesc("1");
    }

    @Test
    void logActivity_SavesActivity() throws ServiceException {
        Map<String, Object> activityData = new HashMap<>();
        activityData.put("eventId", "100");
        activityData.put("action", "created");

        when(activityRepository.save(any(ActivityEntity.class))).thenReturn(testActivity);

        String activityId = activityService.logActivity("1", activityData);

        assertNotNull(activityId);
        verify(activityRepository).save(any(ActivityEntity.class));
    }

    @Test
    void getAllActivities_ReturnsAllActivities() throws ServiceException {
        when(activityRepository.findAll()).thenReturn(Arrays.asList(testActivity));

        List<ActivityDto> activities = activityService.getAllActivities();

        assertNotNull(activities);
        assertEquals(1, activities.size());
        verify(activityRepository).findAll();
    }

    @Test
    void getActivitiesByUserId_WithNoActivities_ReturnsEmptyList() throws ServiceException {
        when(activityRepository.findByUserIdOrderByIdDesc("1"))
                .thenReturn(Arrays.asList());

        List<ActivityDto> activities = activityService.getActivitiesByUserId("1");

        assertNotNull(activities);
        assertTrue(activities.isEmpty());
        verify(activityRepository).findByUserIdOrderByIdDesc("1");
    }
}
