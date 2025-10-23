#!/bin/bash

# Event Management System API Testing Script
# This script demonstrates all available API endpoints with example requests

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Base URLs
BASE_URL="http://localhost:8080"
USER_API="$BASE_URL/users"
EVENT_API="$BASE_URL/events"
PERSONALISE_API="$BASE_URL/personalise"
ANALYTICS_API="$BASE_URL/analytics"
ACTIVITY_API="$BASE_URL/activity"

# Function to print section headers
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

# Function to print step
print_step() {
    echo -e "${GREEN}>>> $1${NC}"
}

# Function to print result
print_result() {
    echo -e "${YELLOW}Response:${NC}"
    echo "$1" | jq '.'
    echo ""
}

# Function to pause
pause() {
    echo -e "${YELLOW}Press any key to continue...${NC}"
    read -n 1 -s
}

# Variables to store tokens and IDs
USER1_TOKEN=""
USER2_TOKEN=""
USER3_TOKEN=""
EVENT1_ID=""
EVENT2_ID=""
EVENT3_ID=""
EVENT4_ID=""

#############################################
# USER MANAGEMENT
#############################################

print_header "1. USER MANAGEMENT - Creating Users"

print_step "1.1. Sign up User 1 (John Doe)"
RESPONSE=$(curl -s -X POST "$USER_API/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123",
    "name": "John Doe"
  }')
print_result "$RESPONSE"
USER1_TOKEN=$(echo "$RESPONSE" | jq -r '.data.accessToken')
echo -e "${GREEN}User 1 Token: $USER1_TOKEN${NC}\n"

print_step "1.2. Sign up User 2 (Jane Smith)"
RESPONSE=$(curl -s -X POST "$USER_API/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.smith@example.com",
    "password": "password456",
    "name": "Jane Smith"
  }')
print_result "$RESPONSE"
USER2_TOKEN=$(echo "$RESPONSE" | jq -r '.data.accessToken')
echo -e "${GREEN}User 2 Token: $USER2_TOKEN${NC}\n"

print_step "1.3. Sign up User 3 (Bob Johnson)"
RESPONSE=$(curl -s -X POST "$USER_API/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob.johnson@example.com",
    "password": "password789",
    "name": "Bob Johnson"
  }')
print_result "$RESPONSE"
USER3_TOKEN=$(echo "$RESPONSE" | jq -r '.data.accessToken')
echo -e "${GREEN}User 3 Token: $USER3_TOKEN${NC}\n"

pause

print_header "2. USER AUTHENTICATION - Testing Login"

print_step "2.1. Login User 1"
RESPONSE=$(curl -s -X POST "$USER_API/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }')
print_result "$RESPONSE"

print_step "2.2. Login with invalid credentials (should fail)"
RESPONSE=$(curl -s -X POST "$USER_API/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "wrongpassword"
  }')
print_result "$RESPONSE"

pause

print_header "3. USER ACCOUNT MANAGEMENT"

print_step "3.1. Get User 1 account details"
RESPONSE=$(curl -s -X GET "$USER_API/account" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "3.2. Update User 1 account"
RESPONSE=$(curl -s -X PATCH "$USER_API/update-account" \
  -H "Authorization: $USER1_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe Updated"
  }')
print_result "$RESPONSE"

print_step "3.3. Verify account was updated"
RESPONSE=$(curl -s -X GET "$USER_API/account" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

pause

#############################################
# EVENT MANAGEMENT
#############################################

print_header "4. EVENT CREATION"

print_step "4.1. User 1 creates Event 1 (Tech Conference 2024)"
RESPONSE=$(curl -s -X POST "$EVENT_API" \
  -H "Authorization: $USER1_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tech Conference 2024",
    "description": "Annual technology conference featuring the latest in software development",
    "location": "Convention Center, Downtown",
    "startTime": "2025-11-15T09:00:00+11:00",
    "endTime": "2025-11-15T17:00:00+11:00",
    "maxParticipants": 100
  }')
print_result "$RESPONSE"
EVENT1_ID=$(echo "$RESPONSE" | jq -r '.data.eventId')
echo -e "${GREEN}Event 1 ID: $EVENT1_ID${NC}\n"

print_step "4.2. User 2 creates Event 2 (Team Building Workshop)"
RESPONSE=$(curl -s -X POST "$EVENT_API" \
  -H "Authorization: $USER2_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Team Building Workshop",
    "description": "Interactive workshop focused on team collaboration and communication",
    "location": "Corporate Training Room",
    "startTime": "2025-11-01T14:00:00+11:00",
    "endTime": "2025-11-01T18:00:00+11:00",
    "maxParticipants": 25
  }')
print_result "$RESPONSE"
EVENT2_ID=$(echo "$RESPONSE" | jq -r '.data.eventId')
echo -e "${GREEN}Event 2 ID: $EVENT2_ID${NC}\n"

print_step "4.3. User 3 creates Event 3 (Project Demo Day)"
RESPONSE=$(curl -s -X POST "$EVENT_API" \
  -H "Authorization: $USER3_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Project Demo Day",
    "description": "Showcase of innovative projects and prototypes",
    "location": "Innovation Lab",
    "startTime": "2025-10-30T10:00:00+11:00",
    "endTime": "2025-10-30T16:00:00+11:00",
    "maxParticipants": 50
  }')
print_result "$RESPONSE"
EVENT3_ID=$(echo "$RESPONSE" | jq -r '.data.eventId')
echo -e "${GREEN}Event 3 ID: $EVENT3_ID${NC}\n"

print_step "4.4. User 1 creates Event 4 (Networking Mixer)"
RESPONSE=$(curl -s -X POST "$EVENT_API" \
  -H "Authorization: $USER1_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Networking Mixer",
    "description": "Casual networking event for professionals in tech",
    "location": "Rooftop Lounge",
    "startTime": "2025-11-08T18:00:00+11:00",
    "endTime": "2025-11-08T21:00:00+11:00",
    "maxParticipants": 75
  }')
print_result "$RESPONSE"
EVENT4_ID=$(echo "$RESPONSE" | jq -r '.data.eventId')
echo -e "${GREEN}Event 4 ID: $EVENT4_ID${NC}\n"

pause

print_header "5. EVENT QUERIES"

print_step "5.1. Get all events"
RESPONSE=$(curl -s -X GET "$EVENT_API")
print_result "$RESPONSE"

print_step "5.2. Get specific event details (Event 1)"
RESPONSE=$(curl -s -X GET "$EVENT_API/$EVENT1_ID")
print_result "$RESPONSE"

print_step "5.3. Get User 1's created events"
RESPONSE=$(curl -s -X GET "$EVENT_API/my-events" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

pause

print_header "6. EVENT UPDATES"

print_step "6.1. User 1 updates Event 1"
RESPONSE=$(curl -s -X PATCH "$EVENT_API/$EVENT1_ID" \
  -H "Authorization: $USER1_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Tech Conference 2025 - Updated",
    "maxParticipants": 150
  }')
print_result "$RESPONSE"

print_step "6.2. Verify event was updated"
RESPONSE=$(curl -s -X GET "$EVENT_API/$EVENT1_ID")
print_result "$RESPONSE"

print_step "6.3. Try to update event as non-owner (should fail)"
RESPONSE=$(curl -s -X PATCH "$EVENT_API/$EVENT1_ID" \
  -H "Authorization: $USER2_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Hacked Event"
  }')
print_result "$RESPONSE"

pause

#############################################
# EVENT REGISTRATION
#############################################

print_header "7. EVENT REGISTRATION"

print_step "7.1. User 2 registers for Event 1"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT1_ID/register" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

print_step "7.2. User 3 registers for Event 1"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT1_ID/register" \
  -H "Authorization: $USER3_TOKEN")
print_result "$RESPONSE"

print_step "7.3. User 1 registers for Event 2"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT2_ID/register" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "7.4. User 3 registers for Event 2"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT2_ID/register" \
  -H "Authorization: $USER3_TOKEN")
print_result "$RESPONSE"

print_step "7.5. User 1 registers for Event 3"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT3_ID/register" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "7.6. User 2 registers for Event 3"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT3_ID/register" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

print_step "7.7. User 2 registers for Event 4"
RESPONSE=$(curl -s -X POST "$EVENT_API/$EVENT4_ID/register" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

pause

print_header "8. REGISTERED EVENTS QUERIES"

print_step "8.1. Get User 1's registered events"
RESPONSE=$(curl -s -X GET "$EVENT_API/registered" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "8.2. Get User 2's registered events"
RESPONSE=$(curl -s -X GET "$EVENT_API/registered" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

pause

print_header "9. EVENT DEREGISTRATION"

print_step "9.1. User 2 deregisters from Event 4"
RESPONSE=$(curl -s -X DELETE "$EVENT_API/$EVENT4_ID/register" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

print_step "9.2. Verify User 2's registered events"
RESPONSE=$(curl -s -X GET "$EVENT_API/registered" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

pause

#############################################
# ANALYTICS (Kafka Stream Processing)
#############################################

print_header "10. STREAM ANALYTICS - Real-time Kafka Processing"

print_step "10.1. Get trending events (5-minute sliding window)"
RESPONSE=$(curl -s -X GET "$ANALYTICS_API/trending-events")
print_result "$RESPONSE"

print_step "10.2. Get capacity alerts (events at 75%+ capacity)"
RESPONSE=$(curl -s -X GET "$ANALYTICS_API/capacity-alerts")
print_result "$RESPONSE"

print_step "10.3. Get per-event statistics"
RESPONSE=$(curl -s -X GET "$ANALYTICS_API/event-stats")
print_result "$RESPONSE"

print_step "10.4. Get global platform statistics"
RESPONSE=$(curl -s -X GET "$ANALYTICS_API/global-stats")
print_result "$RESPONSE"

print_step "10.5. Check stream processing health"
RESPONSE=$(curl -s -X GET "$ANALYTICS_API/health")
print_result "$RESPONSE"

pause

#############################################
# PERSONALISE (AI Agent)
#############################################

print_header "11. AI PERSONALISATION SERVICE"

print_step "11.1. User 1 chats with AI agent"
RESPONSE=$(curl -s -X POST "$PERSONALISE_API/chat" \
  -H "Authorization: $USER1_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What events can you recommend for me?"
  }')
print_result "$RESPONSE"

print_step "11.2. User 2 asks about available events"
RESPONSE=$(curl -s -X POST "$PERSONALISE_API/chat" \
  -H "Authorization: $USER2_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Show me all available events"
  }')
print_result "$RESPONSE"

print_step "11.3. Get AI-generated event recommendations for User 1"
RESPONSE=$(curl -s -X GET "$PERSONALISE_API/recommended-events" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "11.4. Get User 1's events via personalise service"
RESPONSE=$(curl -s -X GET "$PERSONALISE_API/my-events" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

pause

#############################################
# ACTIVITY LOG
#############################################

print_header "12. ACTIVITY LOGGING"

print_step "12.1. Get User 1's activity log"
RESPONSE=$(curl -s -X GET "$ACTIVITY_API/my-activity" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "12.2. Get User 2's activity log"
RESPONSE=$(curl -s -X GET "$ACTIVITY_API/my-activity" \
  -H "Authorization: $USER2_TOKEN")
print_result "$RESPONSE"

pause

#############################################
# EVENT DELETION
#############################################

print_header "13. EVENT DELETION"

print_step "13.1. User 1 deletes Event 4"
RESPONSE=$(curl -s -X DELETE "$EVENT_API/$EVENT4_ID" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

print_step "13.2. Try to get deleted event (should fail)"
RESPONSE=$(curl -s -X GET "$EVENT_API/$EVENT4_ID")
print_result "$RESPONSE"

print_step "13.3. Verify all events (Event 4 should be gone)"
RESPONSE=$(curl -s -X GET "$EVENT_API")
print_result "$RESPONSE"

pause

#############################################
# USER DELETION
#############################################

print_header "14. USER ACCOUNT DELETION"

print_step "14.1. Delete User 3's account"
RESPONSE=$(curl -s -X DELETE "$USER_API/delete-account" \
  -H "Authorization: $USER3_TOKEN")
print_result "$RESPONSE"

print_step "14.2. Try to login as deleted user (should fail)"
RESPONSE=$(curl -s -X POST "$USER_API/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob.johnson@example.com",
    "password": "password789"
  }')
print_result "$RESPONSE"

pause

#############################################
# SUMMARY
#############################################

print_header "15. FINAL STATE SUMMARY"

print_step "15.1. All remaining events"
RESPONSE=$(curl -s -X GET "$EVENT_API")
print_result "$RESPONSE"

print_step "15.2. User 1's activity summary"
RESPONSE=$(curl -s -X GET "$ACTIVITY_API/my-activity" \
  -H "Authorization: $USER1_TOKEN")
print_result "$RESPONSE"

echo -e "\n${GREEN}========================================${NC}"
echo -e "${GREEN}API Testing Complete!${NC}"
echo -e "${GREEN}========================================${NC}\n"

echo -e "Summary of created resources:"
echo -e "  ${BLUE}Users:${NC} User 1 (John), User 2 (Jane)"
echo -e "  ${BLUE}Events:${NC} Event 1, Event 2, Event 3"
echo -e "  ${BLUE}Deleted:${NC} User 3 (Bob), Event 4"
echo -e ""
echo -e "Tokens for manual testing:"
echo -e "  ${YELLOW}User 1:${NC} $USER1_TOKEN"
echo -e "  ${YELLOW}User 2:${NC} $USER2_TOKEN"
echo -e ""
echo -e "Event IDs:"
echo -e "  ${YELLOW}Event 1:${NC} $EVENT1_ID"
echo -e "  ${YELLOW}Event 2:${NC} $EVENT2_ID"
echo -e "  ${YELLOW}Event 3:${NC} $EVENT3_ID"
echo -e ""
