# Event Management Microservices Platform

A microservices-based event management system with Kafka event-driven architecture, real-time stream processing, and AI-powered personalization.

---

## Installation

### macOS

```bash
# Install Java 21
brew install openjdk@21

# Install Maven
brew install maven

# Install Kafka
brew install kafka

# Install Ollama (for AI features)
brew install ollama
ollama pull llama3.1:8b
```

### Windows

1. **Java 21**: Download from [Adoptium](https://adoptium.net/) and install
2. **Maven**: Download from [Maven](https://maven.apache.org/download.cgi), extract to `C:\Program Files\Maven`, add to PATH
3. **Kafka**: Download from [Apache Kafka](https://kafka.apache.org/downloads), extract to `C:\kafka`
4. **Ollama**: Download from [Ollama](https://ollama.com/download/windows), install, then run `ollama pull llama3.1:8b`

---

## Running the Project

### 1. Start Kafka

**macOS:**
```bash
brew services start zookeeper
brew services start kafka
```

**Windows:**
```cmd
# Terminal 1 - Zookeeper
cd C:\kafka
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# Terminal 2 - Kafka
cd C:\kafka
bin\windows\kafka-server-start.bat config\server.properties
```

### 2. Create Kafka Topics

**macOS:**
```bash
kafka-topics --create --topic user-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic event-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic user-registered-event --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics --create --topic event-capacity-reached --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

**Windows:**
```cmd
kafka-topics.bat --create --topic user-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics.bat --create --topic event-created --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics.bat --create --topic user-registered-event --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
kafka-topics.bat --create --topic event-capacity-reached --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```

### 3. Start Ollama

```bash
ollama serve
```

### 4. Start All Microservices

Open 5 separate terminals and run:

```bash
# Terminal 1 - User API
cd services/user-api
mvn spring-boot:run

# Terminal 2 - Event API
cd services/event-api
mvn spring-boot:run

# Terminal 3 - Activity API
cd services/activity-api
mvn spring-boot:run

# Terminal 4 - Personalise API
cd services/personalise-api
mvn spring-boot:run

# Terminal 5 - Client API (Gateway)
cd services/client-api
mvn spring-boot:run
```

### 5. Verify It's Running

```bash
curl http://localhost:8080/actuator/health
```

Should return: `{"status":"UP"}`

**Access Swagger UI:** http://localhost:8080/swagger-ui.html

---

## Use Cases

### Use Case 1: Create and Manage Events

**1. Create a user:**
```bash
curl -X POST http://localhost:8080/users/signup \
  -H "Content-Type: application/json" \
  -d '{"name": "Alice", "email": "alice@example.com", "password": "pass123"}'
```
Response: `"1"` (your user ID)

**2. Create an event:**
```bash
curl -X POST http://localhost:8080/events \
  -H "Content-Type: application/json" \
  -H "Authorization: 1" \
  -d '{
    "title": "AI Workshop",
    "description": "Learn AI",
    "location": "Room 301",
    "startTime": "2025-11-15T14:00:00",
    "endTime": "2025-11-15T17:00:00",
    "maxParticipants": 30
  }'
```
Response: Event ID (save this)

**3. View all events:**
```bash
curl http://localhost:8080/events
```

**4. Register for an event:**
```bash
curl -X POST "http://localhost:8080/events/{EVENT_ID}/register" \
  -H "Authorization: 1"
```

---

### Use Case 2: AI-Powered Event Assistant

**Get AI summary of your events:**
```bash
curl http://localhost:8080/api/v1/my-events -H "Authorization: 1"
```

**Chat with AI:**
```bash
curl -X POST http://localhost:8080/api/v1/chat \
  -H "Content-Type: application/json" \
  -H "Authorization: 1" \
  -d '{"message": "How many events have I created?"}'
```

**Get AI recommendations:**
```bash
curl http://localhost:8080/api/v1/recommended-events -H "Authorization: 1"
```

---

### Use Case 3: Real-Time Analytics

**Check trending events:**
```bash
curl http://localhost:8082/api/v1/analytics/trending-events
```

**Check event capacity:**
```bash
curl http://localhost:8082/api/v1/analytics/capacity-status
```

**Get platform statistics:**
```bash
curl http://localhost:8082/api/v1/analytics/global-stats
```

**View your activity log:**
```bash
curl http://localhost:8080/activity/my-activity -H "Authorization: 1"
```

---

## Quick Test

For easy testing, use **Swagger UI**: http://localhost:8080/swagger-ui.html
- Click "Authorize" and enter your user ID
- Try any endpoint with the "Try it out" button

---

## Service Ports

| Service | Port |
|---------|------|
| Client API (Gateway) | 8080 |
| User API | 8081 |
| Event API | 8082 |
| Activity API | 8083 |
| Personalise API | 8084 |

---

## Troubleshooting

**Kafka not starting:**
```bash
# macOS
brew services restart kafka
brew services restart zookeeper
```

**Port in use:**
```bash
# macOS/Linux
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

**Services fail to start:**
```bash
# Check Java version (must be 21)
java -version

# Rebuild
cd services/<service-name>
mvn clean install
```

---

## Technology Stack

- **Spring Boot 3.3.2** - Microservices framework
- **Apache Kafka** - Event-driven messaging
- **Spring Cloud Stream** - Stream processing
- **LangChain4j** - AI agent framework
- **Ollama (llama3.1:8b)** - Local LLM
- **H2 Database** - Data storage
- **Java 21** - Programming language

---

## What This Does

1. **Event-Driven Architecture** - Services communicate via Kafka events
2. **Real-Time Stream Processing** - Live analytics on registrations and trending events
3. **AI Agent** - LLM that autonomously answers questions and makes recommendations
4. **Microservices** - 5 independent services working together
5. **Interactive API** - Full Swagger documentation for easy testing
