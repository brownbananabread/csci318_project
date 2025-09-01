package server.service;

import server.model.UserContext;
import server.logging.ActivityLogger;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class JobService {
    private final DataSource dataSource;
    private final ActivityLogger activityLogger;
    
    public JobService(DataSource dataSource, ActivityLogger activityLogger) {
        this.dataSource = dataSource;
        this.activityLogger = activityLogger;
    }
    
    public List<Map<String, Object>> getJobs(UserContext context, String status) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            String baseSql = "SELECT j.*, u.firstname as customer_first_name, u.lastname as customer_last_name, " +
                           "u.email as customer_email FROM jobs j JOIN users u ON j.customerid = u.userid ";
            
            PreparedStatement stmt;
            
            if (context.isBusinessUser()) {
                if ("unassigned".equals(status)) {
                    stmt = conn.prepareStatement(baseSql + "WHERE j.status = ?::job_status AND j.service = ? ORDER BY j.created_at DESC");
                    stmt.setString(1, "unassigned");
                    stmt.setString(2, context.service);
                } else if (status != null && status.contains(",")) {
                    String[] statuses = status.split(",\\s*");
                    StringBuilder whereClause = new StringBuilder("WHERE j.businessid = ? AND j.status IN (");
                    for (int i = 0; i < statuses.length; i++) {
                        whereClause.append("?::job_status");
                        if (i < statuses.length - 1) {
                            whereClause.append(", ");
                        }
                    }
                    whereClause.append(") ORDER BY j.created_at DESC");
                    
                    stmt = conn.prepareStatement(baseSql + whereClause.toString());
                    stmt.setInt(1, context.userId);
                    for (int i = 0; i < statuses.length; i++) {
                        stmt.setString(i + 2, statuses[i].trim());
                    }
                } else if ("assigned".equals(status)) {
                    stmt = conn.prepareStatement(baseSql + "WHERE j.businessid = ? AND j.status = 'assigned'::job_status ORDER BY j.created_at DESC");
                    stmt.setInt(1, context.userId);
                } else if ("pending_payment".equals(status)) {
                    stmt = conn.prepareStatement(baseSql + "WHERE j.businessid = ? AND j.status = 'pending_payment'::job_status ORDER BY j.created_at DESC");
                    stmt.setInt(1, context.userId);
                } else if ("complete".equals(status)) {
                    stmt = conn.prepareStatement(baseSql + "WHERE j.businessid = ? AND j.status = 'complete'::job_status ORDER BY j.created_at DESC");
                    stmt.setInt(1, context.userId);
                } else {
                    stmt = conn.prepareStatement(baseSql + "WHERE j.businessid = ? ORDER BY j.created_at DESC");
                    stmt.setInt(1, context.userId);
                }
            } else if (context.isCustomer()) {
                if (status != null && !status.isEmpty()) {
                    if (status.contains(",")) {
                        String[] statuses = status.split(",\\s*");
                        StringBuilder whereClause = new StringBuilder("WHERE j.customerid = ? AND j.status IN (");
                        for (int i = 0; i < statuses.length; i++) {
                            whereClause.append("?::job_status");
                            if (i < statuses.length - 1) {
                                whereClause.append(", ");
                            }
                        }
                        whereClause.append(") ORDER BY j.created_at DESC");
                        
                        stmt = conn.prepareStatement(baseSql + whereClause.toString());
                        stmt.setInt(1, context.userId);
                        for (int i = 0; i < statuses.length; i++) {
                            stmt.setString(i + 2, statuses[i].trim());
                        }
                    } else {
                        stmt = conn.prepareStatement(baseSql + "WHERE j.customerid = ? AND j.status = ?::job_status ORDER BY j.created_at DESC");
                        stmt.setInt(1, context.userId);
                        stmt.setString(2, status);
                    }
                } else {
                    stmt = conn.prepareStatement(baseSql + "WHERE j.customerid = ? ORDER BY j.created_at DESC");
                    stmt.setInt(1, context.userId);
                }
            } else {
                if (status != null && !status.isEmpty()) {
                    if (status.contains(",")) {
                        String[] statuses = status.split(",\\s*");
                        StringBuilder whereClause = new StringBuilder("WHERE j.status IN (");
                        for (int i = 0; i < statuses.length; i++) {
                            whereClause.append("?::job_status");
                            if (i < statuses.length - 1) {
                                whereClause.append(", ");
                            }
                        }
                        whereClause.append(") ORDER BY j.created_at DESC");
                        
                        stmt = conn.prepareStatement(baseSql + whereClause.toString());
                        for (int i = 0; i < statuses.length; i++) {
                            stmt.setString(i + 1, statuses[i].trim());
                        }
                    } else {
                        stmt = conn.prepareStatement(baseSql + "WHERE j.status = ?::job_status ORDER BY j.created_at DESC");
                        stmt.setString(1, status);
                    }
                } else {
                    stmt = conn.prepareStatement(baseSql + "ORDER BY j.created_at DESC");
                }
            }
            
            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> jobs = new ArrayList<>();
            
            while (rs.next()) {
                Map<String, Object> job = new HashMap<>();
                job.put("jobId", rs.getInt("jobid"));
                job.put("customerId", rs.getInt("customerid"));
                job.put("businessId", rs.getObject("businessid"));
                job.put("title", rs.getString("title"));
                job.put("description", rs.getString("description"));
                job.put("service", rs.getString("service"));
                job.put("amount", rs.getDouble("amount"));
                job.put("status", rs.getString("status"));
                job.put("location", rs.getString("location"));
                job.put("createdAt", rs.getTimestamp("created_at"));
                job.put("customerFirstName", rs.getString("customer_first_name"));
                job.put("customerLastName", rs.getString("customer_last_name"));
                job.put("customerEmail", rs.getString("customer_email"));
                jobs.add(job);
            }
            
            stmt.close();
            return jobs;
        }
    }

    public Map<String, Object> updateJobStatus(UserContext context, String jobId, String newStatus) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement infoStmt = conn.prepareStatement(
                "SELECT j.title, j.status, u.firstname, u.lastname FROM jobs j " +
                "JOIN users u ON j.customerid = u.userid WHERE j.jobid = ?");
            infoStmt.setInt(1, Integer.parseInt(jobId));
            ResultSet infoRs = infoStmt.executeQuery();
            
            String jobTitle = "Job #" + jobId;
            String customerName = "";
            if (infoRs.next()) {
                jobTitle = infoRs.getString("title");
                customerName = infoRs.getString("firstname") + " " + infoRs.getString("lastname");
            }
            
            PreparedStatement updateStmt;
            String activityDesc;
            
            if ("unassigned".equals(newStatus)) {
                updateStmt = conn.prepareStatement(
                    "UPDATE jobs SET status = 'unassigned'::job_status, businessid = NULL, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE jobid = ? AND businessid = ?");
                updateStmt.setInt(1, Integer.parseInt(jobId));
                updateStmt.setInt(2, context.userId);
                activityDesc = "Unassigned from job \"" + jobTitle + "\" (customer: " + customerName + ")";
            } else if ("assigned".equals(newStatus)) {
                updateStmt = conn.prepareStatement(
                    "UPDATE jobs SET status = 'assigned'::job_status, businessid = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE jobid = ? AND status = 'unassigned'");
                updateStmt.setInt(1, context.userId);
                updateStmt.setInt(2, Integer.parseInt(jobId));
                activityDesc = "Assigned to job \"" + jobTitle + "\" (customer: " + customerName + ")";
            } else {
                updateStmt = conn.prepareStatement(
                    "UPDATE jobs SET status = ?::job_status, updated_at = CURRENT_TIMESTAMP WHERE jobid = ? AND businessid = ?");
                updateStmt.setString(1, newStatus);
                updateStmt.setInt(2, Integer.parseInt(jobId));
                updateStmt.setInt(3, context.userId);
                activityDesc = "Updated job \"" + jobTitle + "\" status to " + newStatus + " (customer: " + customerName + ")";
            }
            
            if (updateStmt.executeUpdate() > 0) {
                activityLogger.logActivity(context.userId, activityDesc);
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Job status updated successfully");
                response.put("jobId", jobId);
                response.put("status", newStatus);
                return response;
            }
            throw new RuntimeException("Job not found or unauthorized");
        }
    }
    
    public Map<String, Object> createJob(int customerId, Map<String, Object> jobData) throws SQLException {
        String title = (String) jobData.get("title");
        String description = (String) jobData.get("description");
        String service = (String) jobData.get("service");
        String location = (String) jobData.get("location");
        Double amount = jobData.get("amount") instanceof Number 
            ? ((Number) jobData.get("amount")).doubleValue() 
            : Double.parseDouble((String) jobData.get("amount"));
        
        if (title == null || description == null || service == null || amount == null || location == null) {
            throw new RuntimeException("All fields are required");
        }
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO jobs (customerid, title, description, service, amount, location, status, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'unassigned'::job_status, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) " +
                "RETURNING jobid, created_at")) {
            
            stmt.setInt(1, customerId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, service);
            stmt.setDouble(5, amount);
            stmt.setString(6, location);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                activityLogger.logActivity(customerId, 
                    "Created new " + service + " job request: \"" + title + "\" for $" + String.format("%.2f", amount));
                
                Map<String, Object> response = new HashMap<>();
                response.put("jobId", rs.getInt("jobid"));
                response.put("customerId", customerId);
                response.put("title", title);
                response.put("description", description);
                response.put("service", service);
                response.put("amount", amount);
                response.put("location", location);
                response.put("status", "unassigned");
                response.put("createdAt", rs.getTimestamp("created_at"));
                response.put("message", "Job created successfully");
                return response;
            }
            throw new SQLException("Failed to create job");
        }
    }
    
    public List<Map<String, Object>> getAllJobs() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                "SELECT j.*, u1.firstname as customer_first_name, u1.lastname as customer_last_name, " +
                "u2.firstname as business_first_name, u2.lastname as business_last_name, u2.service as business_service " +
                "FROM jobs j LEFT JOIN users u1 ON j.customerid = u1.userid " +
                "LEFT JOIN users u2 ON j.businessid = u2.userid ORDER BY j.created_at DESC")) {
            
            ResultSet rs = stmt.executeQuery();
            List<Map<String, Object>> jobs = new ArrayList<>();
            
            while (rs.next()) {
                Map<String, Object> job = new HashMap<>();
                job.put("id", String.valueOf(rs.getInt("jobid")));
                job.put("title", rs.getString("title"));
                job.put("description", rs.getString("description"));
                job.put("service", rs.getString("service"));
                job.put("price", rs.getDouble("amount"));
                job.put("status", rs.getString("status"));
                job.put("location", rs.getString("location"));
                job.put("createdAt", rs.getTimestamp("created_at"));
                job.put("updatedAt", rs.getTimestamp("updated_at"));
                job.put("customerId", rs.getInt("customerid"));
                
                String customerName = rs.getString("customer_first_name") + " " + rs.getString("customer_last_name");
                job.put("customerName", customerName.trim().isEmpty() ? "Unknown Customer" : customerName);
                
                Integer businessId = (Integer) rs.getObject("businessid");
                if (businessId != null) {
                    String businessService = rs.getString("business_service");
                    String businessName = businessService != null ? businessService 
                        : rs.getString("business_first_name") + " " + rs.getString("business_last_name");
                    job.put("businessName", businessName);
                    job.put("businessId", businessId);
                } else {
                    job.put("businessName", "Unassigned");
                    job.put("businessId", null);
                }
                jobs.add(job);
            }
            return jobs;
        }
    }
}