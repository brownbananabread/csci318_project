# Event API Usage

This document describes the available endpoints and usage for the Event API service through the Client API Gateway.

## Base URL
```
http://localhost:8080/api/v1
```

**Note:** The Event API (port 8082) is an internal service. All event operations should go through the Client API Gateway (port 8080) which handles user authentication and validation before forwarding requests to the Event API.

## Authentication
All protected endpoints require authentication via Bearer token in the Authorization header:
```
Authorization: Bearer <your-token>
```

## Architecture Flow
When you make event-related requests:
1. **Client API Gateway** (port 8080) receives your request
2. **User API** (port 8081) validates your authentication token
3. **Event API** (port 8082) processes the event-specific operations
4. **Client API Gateway** returns the response to you

## Endpoints

### Public Endpoints (No Authentication Required)

#### Get All Events
```http
GET /events
```
Returns a list of all available events.

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "data": [
    {
      "id": "event-123",
      "title": "Tech Conference 2024",
      "description": "Annual technology conference",
      "location": "Convention Center",
      "startTime": "2024-02-15T09:00:00Z",
      "endTime": "2024-02-15T17:00:00Z",
      "createdBy": "user-456",
      "maxParticipants": 100,
      "currentParticipants": 25
    }
  ],
  "path": "/api/v1/events"
}
```

#### Get Specific Event
```http
GET /events/{eventId}
```
Returns details for a specific event.

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "data": {
    "id": "event-123",
    "title": "Tech Conference 2024",
    "description": "Annual technology conference",
    "location": "Convention Center",
    "startTime": "2024-02-15T09:00:00Z",
    "endTime": "2024-02-15T17:00:00Z",
    "createdBy": "user-456",
    "maxParticipants": 100,
    "currentParticipants": 25
  },
  "path": "/api/v1/events/event-123"
}
```

### Protected Endpoints (Authentication Required)

#### Create Event
```http
POST /events
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "My Event",
  "description": "Event description",
  "location": "Event location",
  "startTime": "2024-02-15T09:00:00Z",
  "endTime": "2024-02-15T17:00:00Z",
  "maxParticipants": 50
}
```

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 201,
  "data": {
    "eventId": "event-789"
  },
  "path": "/api/v1/events"
}
```

#### Update Event
```http
PATCH /events/{eventId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Event Title",
  "description": "Updated description"
}
```

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "message": "Event updated successfully",
  "path": "/api/v1/events/event-123"
}
```

#### Delete Event
```http
DELETE /events/{eventId}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "message": "Event deleted successfully",
  "path": "/api/v1/events/event-123"
}
```

#### Register for Event
```http
POST /events/{eventId}/register
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "message": "Successfully registered for event",
  "path": "/api/v1/events/event-123/register"
}
```

#### Deregister from Event
```http
DELETE /events/{eventId}/register
Authorization: Bearer <token>
```

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "message": "Successfully deregistered from event",
  "path": "/api/v1/events/event-123/register"
}
```

#### Get My Created Events
```http
GET /events/my-events
Authorization: Bearer <token>
```
Returns events created by the authenticated user.

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "data": [
    {
      "id": "event-123",
      "title": "My Event",
      "description": "Event I created",
      "location": "My Location",
      "startTime": "2024-02-15T09:00:00Z",
      "endTime": "2024-02-15T17:00:00Z",
      "createdBy": "current-user-id",
      "maxParticipants": 50,
      "currentParticipants": 10
    }
  ],
  "path": "/api/v1/events/my-events"
}
```

#### Get Registered Events
```http
GET /events/registered
Authorization: Bearer <token>
```
Returns events the authenticated user is registered for.

**Response:**
```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 200,
  "data": [
    {
      "id": "event-456",
      "title": "Event I'm Attending",
      "description": "Event description",
      "location": "Event location",
      "startTime": "2024-02-20T10:00:00Z",
      "endTime": "2024-02-20T18:00:00Z",
      "createdBy": "other-user-id",
      "maxParticipants": 100,
      "currentParticipants": 45
    }
  ],
  "path": "/api/v1/events/registered"
}
```

## Error Responses

All endpoints return consistent error responses:

```json
{
  "timestamp": "2024-01-01T12:00:00Z",
  "status": 400,
  "error": "Error message describing what went wrong",
  "path": "/api/v1/events"
}
```

Common HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `500` - Internal Server Error

## Usage Examples

### Creating and Managing Events

1. **Create an event:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/events \
     -H "Authorization: Bearer your-token" \
     -H "Content-Type: application/json" \
     -d '{
       "title": "Team Meeting",
       "description": "Weekly team sync",
       "location": "Conference Room A",
       "startTime": "2024-02-15T14:00:00Z",
       "endTime": "2024-02-15T15:00:00Z",
       "maxParticipants": 10
     }'
   ```

2. **Register for an event:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/events/event-123/register \
     -H "Authorization: Bearer your-token"
   ```

3. **View your events:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/events/my-events \
     -H "Authorization: Bearer your-token"
   ```

### Event Discovery

1. **Browse all events:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/events
   ```

2. **Get event details:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/events/event-123
   ```

## Server Configuration

The architecture consists of:
- **Client API Gateway** (port 8080) - Main entry point for all requests
- **User API** (port 8081) - Handles user authentication and management
- **Event API** (port 8082) - Internal service for event operations

## Validation Flow

For protected operations, the Client API Gateway performs these validations:

1. **User Authentication** - Validates the Bearer token with User API
2. **Event Existence** - Checks if the event exists via Event API
3. **Authorization** - For update/delete operations, verifies the user owns the event
4. **Business Logic** - Processes the request through Event API

This ensures secure and validated operations across all event-related functionality.