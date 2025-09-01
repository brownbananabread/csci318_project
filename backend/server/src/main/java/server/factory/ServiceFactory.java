package server.factory;

import server.service.*;
import server.logging.ActivityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class ServiceFactory {
    @Autowired private DataSource dataSource;
    @Autowired private ActivityLogger activityLogger;
    
    public UserService createUserService() { return new UserService(dataSource, activityLogger); }
    public JobService createJobService() { return new JobService(dataSource, activityLogger); }
    public ActivityService createActivityService() { return new ActivityService(dataSource); }
    public StatsService createStatsService() { return new StatsService(dataSource); }
}