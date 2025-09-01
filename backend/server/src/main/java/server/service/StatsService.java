package server.service;

import server.model.UserContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class StatsService {
    private final DataSource dataSource;
    
    public StatsService(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Map<String, Object> getStats(UserContext context) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            Map<String, Object> stats = new HashMap<>();
            
            String[] queries = {
                "SELECT COUNT(*) as count FROM jobs WHERE status = 'unassigned' AND service = ?",
                "SELECT COUNT(*) as count FROM jobs WHERE status = 'assigned' AND businessid = ?",
                "SELECT COUNT(*) as count FROM jobs WHERE status = 'pending_payment' AND businessid = ?",
                "SELECT COUNT(*) as count FROM jobs WHERE status = 'complete' AND businessid = ?"
            };
            String[] keys = {"availableJobs", "activeJobs", "paymentPendingJobs", "completedJobs"};
            
            for (int i = 0; i < queries.length; i++) {
                PreparedStatement stmt = conn.prepareStatement(queries[i]);
                stmt.setObject(1, i == 0 ? context.service : context.userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) stats.put(keys[i], rs.getInt("count"));
            }
            
            return stats;
        }
    }
}