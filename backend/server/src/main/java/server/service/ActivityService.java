package server.service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityService {
    private final DataSource dataSource;
    
    public ActivityService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public List<Map<String, Object>> getActivity(int userId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT description, created_at FROM activity WHERE userid = ? ORDER BY created_at DESC")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            List<Map<String, Object>> activities = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("description", rs.getString("description"));
                activity.put("created_at", rs.getTimestamp("created_at"));
                activities.add(activity);
            }
            return activities;
        }
    }
    
    public List<Map<String, Object>> getAllActivities() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT a.activityid, a.userid, a.description, a.created_at, " +
                "u.firstname, u.lastname FROM activity a " +
                "LEFT JOIN users u ON a.userid = u.userid ORDER BY a.created_at DESC")) {
            
            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> activities = new ArrayList<>();
            
            while (rs.next()) {
                Map<String, Object> activity = new HashMap<>();
                activity.put("id", String.valueOf(rs.getInt("activityid")));
                activity.put("userId", String.valueOf(rs.getInt("userid")));
                activity.put("description", rs.getString("description"));
                activity.put("timestamp", rs.getTimestamp("created_at"));
                
                String userName = rs.getString("firstname") + " " + rs.getString("lastname");
                activity.put("userName", userName.trim().isEmpty() ? "User " + rs.getInt("userid") : userName);
                
                String desc = rs.getString("description").toLowerCase();
                String type = desc.contains("job") || desc.contains("assigned") ? "job"
                    : desc.contains("logged") || desc.contains("rated") ? "user" : "system";
                activity.put("type", type);
                activity.put("action", rs.getString("description"));
                activity.put("details", rs.getString("description"));
                activities.add(activity);
            }
            return activities;
        }
    }
}