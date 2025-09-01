package server.service;

import server.logging.ActivityLogger;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class UserService {
    private final DataSource dataSource;
    private final ActivityLogger activityLogger;
    
    public UserService(DataSource dataSource, ActivityLogger activityLogger) {
        this.dataSource = dataSource;
        this.activityLogger = activityLogger;
    }
    
    public Map<String, Object> login(String email, String password) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT userid, firstname, lastname, email, role, service FROM users WHERE email = ? AND password = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", rs.getInt("userid"));
                user.put("firstName", rs.getString("firstname"));
                user.put("lastName", rs.getString("lastname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("service", rs.getString("service"));
                
                activityLogger.logActivity((int)user.get("userId"), 
                    "User " + user.get("firstName") + " " + user.get("lastName") + " logged in successfully");
                return user;
            }
            throw new RuntimeException("Invalid email or password");
        }
    }
    
    public Map<String, Object> register(Map<String, String> credentials) throws SQLException {
        String firstName = credentials.get("firstName");
        String lastName = credentials.get("lastName");
        String email = credentials.get("email");
        String password = credentials.get("password");
        String role = credentials.get("role");
        String service = credentials.get("service");
        
        if (firstName == null || lastName == null || email == null || password == null || role == null) {
            throw new RuntimeException("All fields are required");
        }
        
        if ("business".equals(role) && (service == null || service.trim().isEmpty())) {
            throw new RuntimeException("Service is required for business users");
        }
        
        String sql = "business".equals(role) 
            ? "INSERT INTO users (firstname, lastname, email, password, role, service) VALUES (?, ?, ?, ?, ?::user_role, ?) RETURNING userid"
            : "INSERT INTO users (firstname, lastname, email, password, role) VALUES (?, ?, ?, ?, ?::user_role) RETURNING userid";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, email);
            stmt.setString(4, password);
            stmt.setString(5, role);
            if ("business".equals(role)) stmt.setString(6, service);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", rs.getInt("userid"));
                user.put("firstName", firstName);
                user.put("lastName", lastName);
                user.put("email", email);
                user.put("role", role);
                if ("business".equals(role)) user.put("service", service);
                
                String activityDesc = "New " + role + " user " + firstName + " " + lastName + " registered";
                if ("business".equals(role) && service != null) activityDesc += " for " + service + " service";
                activityLogger.logActivity((int)user.get("userId"), activityDesc);
                
                return user;
            }
            throw new SQLException("User creation failed");
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key")) throw new RuntimeException("Email already exists");
            throw e;
        }
    }
    
    public boolean validateEmail(String email) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    public Map<String, Object> getProfile(int userId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT userid, firstname, lastname, email, role, service FROM users WHERE userid = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", userId);
                user.put("firstName", rs.getString("firstname"));
                user.put("lastName", rs.getString("lastname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("service", rs.getString("service"));
                return user;
            }
            throw new RuntimeException("Unauthorized");
        }
    }
    
    public Integer[] getRatings(int userId) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT ratings FROM users WHERE userid = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Array sqlArray = rs.getArray("ratings");
                return sqlArray != null ? (Integer[]) sqlArray.getArray() : new Integer[0];
            }
            throw new RuntimeException("User not found");
        }
    }
    
    public Map<String, Object> submitRating(int raterId, String targetUserId, Integer rating) throws SQLException {
        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Invalid rating value");
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET ratings = array_append(ratings, ?) WHERE userid = ?")) {
            stmt.setInt(1, rating);
            stmt.setInt(2, Integer.parseInt(targetUserId));
            
            if (stmt.executeUpdate() > 0) {
                PreparedStatement userStmt = conn.prepareStatement("SELECT firstname, lastname FROM users WHERE userid = ?");
                userStmt.setInt(1, Integer.parseInt(targetUserId));
                ResultSet userRs = userStmt.executeQuery();
                
                String ratedUserName = userRs.next() 
                    ? userRs.getString("firstname") + " " + userRs.getString("lastname")
                    : "User " + targetUserId;
                
                activityLogger.logActivity(raterId, "Rated " + ratedUserName + " with " + rating + " stars");
                activityLogger.logActivity(Integer.parseInt(targetUserId), "Received a " + rating + " star rating");
                
                Map<String, Object> result = new HashMap<>();
                result.put("message", "Rating submitted successfully");
                result.put("rating", rating);
                return result;
            }
            throw new RuntimeException("User not found");
        }
    }
    
    public List<Map<String, Object>> getAllUsers() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT userid, firstname, lastname, email, role, service, ratings, created_at, updated_at FROM users")) {
            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> users = new ArrayList<>();
            
            while (rs.next()) {
                Map<String, Object> user = new HashMap<>();
                user.put("userId", rs.getInt("userid"));
                user.put("firstName", rs.getString("firstname"));
                user.put("lastName", rs.getString("lastname"));
                user.put("email", rs.getString("email"));
                user.put("role", rs.getString("role"));
                user.put("service", rs.getString("service"));
                user.put("createdAt", rs.getTimestamp("created_at"));
                user.put("updatedAt", rs.getTimestamp("updated_at"));
                
                Array ratingsArray = rs.getArray("ratings");
                user.put("ratings", ratingsArray != null ? ratingsArray.getArray() : new Integer[0]);
                users.add(user);
            }
            return users;
        }
    }
}