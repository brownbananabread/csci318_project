package server.auth;

import server.model.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.*;

// Strategy Pattern - Authentication
public interface AuthenticationStrategy {
    boolean authenticate(String accessToken);
    UserContext getUserContext(String accessToken) throws SQLException;
}

@Component
class TokenAuthenticationStrategy implements AuthenticationStrategy {
    @Autowired private DataSource dataSource;
    
    public boolean authenticate(String accessToken) {
        if (accessToken == null) return false;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT userid FROM users WHERE userid = ?")) {
            stmt.setInt(1, Integer.parseInt(accessToken));
            return stmt.executeQuery().next();
        } catch (Exception e) {
            return false;
        }
    }
    
    public UserContext getUserContext(String accessToken) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT userid, firstname, lastname, email, role, service FROM users WHERE userid = ?")) {
            stmt.setInt(1, Integer.parseInt(accessToken));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new UserContext(
                    rs.getInt("userid"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("email"),
                    rs.getString("role"),
                    rs.getString("service")
                );
            }
            throw new RuntimeException("Invalid token");
        }
    }
}