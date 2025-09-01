package server.logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ActivityLogger {
    private final List<ActivityObserver> observers = new ArrayList<>();
    @Autowired private DataSource dataSource;
    
    public void attach(ActivityObserver observer) { observers.add(observer); }
    public void detach(ActivityObserver observer) { observers.remove(observer); }
    
    public void logActivity(int userId, String description) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO activity (userid, description) VALUES (?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setString(2, description);
            stmt.executeUpdate();
            
            LocalDateTime now = LocalDateTime.now();
            observers.forEach(o -> o.onActivityLogged(userId, description, now));
        } catch (SQLException e) {
            System.err.println("Failed to log activity: " + e.getMessage());
        }
    }
}