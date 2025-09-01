package server.model;

public class UserContext {
    public final int userId;
    public final String firstName, lastName, email, role, service;
    
    public UserContext(int userId, String firstName, String lastName, String email, String role, String service) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.service = service;
    }
    
    public boolean isBusinessUser() { return "business".equals(role); }
    public boolean isCustomer() { return "customer".equals(role); }
    public boolean isAdmin() { return "admin".equals(role); }
}