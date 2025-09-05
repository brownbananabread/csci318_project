# CSCI318 Software Engineering Practices & Principles - Group Project

## User Management Microservices

This project implements a user management system using Spring Boot microservices architecture with two main services:

### Architecture

- **Client API (Port 8080)** - Gateway service that handles client requests and forwards them to the User API
- **User API (Port 8081)** - Core user management service with database operations

### Services

#### Client API Service
Acts as an API gateway that provides user management endpoints and communicates with the User API service.

**Endpoints:**
- `POST /api/v1/signup` - User registration
- `POST /api/v1/login` - User authentication  
- `GET /api/v1/account` - Get user account details (requires Authorization header)
- `PUT /api/v1/update-account` - Update user account (requires Authorization header)
- `DELETE /api/v1/remove-account` - Delete user account (requires Authorization header)

**Response Format:**
All endpoints return a standardized `ApiResponse` with:
```json
{
  "datetime": "2025-09-05T18:30:00",
  "message": "User ID or message text",
  "status": "success|error"
}
```

#### User API Service  
Core service that manages user data and provides CRUD operations.

**Endpoints:**
- `GET /user?email=example@email.com` - Check if user exists
- `GET /user` (with Authorization header) - Get user details by ID
- `POST /user` - Create new user
- `PUT /user` (with Authorization header) - Update user
- `DELETE /user` (with Authorization header) - Delete user
- `POST /login` - Authenticate user

### Data Model

**User:**
- `id` (Long) - Auto-generated primary key
- `name` (String) - User's full name
- `email` (String) - Unique email address
- `password` (String) - User password

### Authentication

The system uses a simple token-based authentication where:
- User ID serves as the bearer token for protected endpoints
- Tokens are passed via the `Authorization` header
- No expiration or complex JWT implementation

### Usage Examples

**Sign up a new user:**
```bash
curl -X POST http://localhost:8080/api/v1/signup \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","password":"password123"}'
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/v1/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"password123"}'
```

**Get account details:**
```bash
curl -X GET http://localhost:8080/api/v1/account \
  -H "Authorization: 1"
```

### Running the Services

1. **Start User API (Port 8081):**
```bash
cd services/user-api
mvn spring-boot:run
```

2. **Start Client API (Port 8080):**
```bash
cd services/client-api  
mvn spring-boot:run
```

### Database

Both services use H2 in-memory database with sample data pre-loaded for testing.
