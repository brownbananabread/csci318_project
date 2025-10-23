package app.service;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import app.exception.ServiceException;
import app.model.ActivityDto;
import app.model.ActivityEntity;
import app.repository.ActivityRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ObjectMapper objectMapper;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
        this.objectMapper = new ObjectMapper();
    }

    public String logActivity(String userId, Map<String, Object> activityData) throws ServiceException {
        try {
            // Convert the activity data to JSON string
            String activityJson = objectMapper.writeValueAsString(activityData);

            ActivityEntity activity = new ActivityEntity(userId, activityJson);
            ActivityEntity savedActivity = activityRepository.save(activity);
            return savedActivity.getId().toString();
        } catch (Exception e) {
            throw new ServiceException("Failed to log activity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ActivityDto> getAllActivities() throws ServiceException {
        try {
            List<ActivityEntity> activities = activityRepository.findAll();
            return activities.stream()
                    .map(ActivityDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<ActivityDto> getActivitiesByUserId(String userId) throws ServiceException {
        try {
            List<ActivityEntity> activities = activityRepository.findByUserIdOrderByIdDesc(userId);
            return activities.stream()
                    .map(ActivityDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve user activities", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}