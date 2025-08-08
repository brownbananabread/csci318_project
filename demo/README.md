# Library Management System

A Spring Boot REST API for managing libraries, books, and addresses.

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

To start the application, run the following command in the project root directory:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080` by default.

### Database
This application uses H2 database with console enabled. You can access the database console at:

**H2 Console URL:** `http://localhost:8080/h2-console`

**Database Configuration:**
- JDBC URL: `jdbc:h2:file:/Users/jacobbrown/workspace/h2/mydb`
- Username: `jacob`
- Password: `password`

## API Endpoints

### Address Management

#### Get All Addresses
```http
GET /addresses
```
Returns a list of all addresses.

**Response:**
```json
[
  {
    "id": 1,
    "location": "123 Main St, City, State"
  }
]
```

#### Get Address by ID
```http
GET /addresses/{id}
```
Returns a specific address by its ID.

**Response:**
```json
{
  "id": 1,
  "location": "123 Main St, City, State"
}
```

#### Create New Address
```http
POST /addresses
Content-Type: application/json
```
Creates a new address.

**Request Body:**
```json
{
  "location": "456 Oak Ave, Town, State"
}
```

**Response:**
```json
{
  "id": 2,
  "location": "456 Oak Ave, Town, State"
}
```

### Book Management

#### Get All Books
```http
GET /books
```
Returns a list of all books.

**Response:**
```json
[
  {
    "id": 1,
    "title": "The Great Gatsby"
  }
]
```

#### Create New Book
```http
POST /books
Content-Type: application/json
```
Creates a new book.

**Request Body:**
```json
{
  "title": "To Kill a Mockingbird"
}
```

**Response:**
```json
{
  "id": 2,
  "title": "To Kill a Mockingbird"
}
```

### Library Management

#### Get All Libraries
```http
GET /libraries
```
Returns a list of all libraries.

**Response:**
```json
[
  {
    "id": 1,
    "name": "Central Library"
  }
]
```

#### Get Library by ID
```http
GET /libraries/{id}
```
Returns a specific library by its ID.

**Response:**
```json
{
  "id": 1,
  "name": "Central Library"
}
```

#### Create New Library
```http
POST /libraries
Content-Type: application/json
```
Creates a new library.

**Request Body:**
```json
{
  "name": "Downtown Library"
}
```

**Response:**
```json
{
  "id": 2,
  "name": "Downtown Library"
}
```

#### Update Library Name
```http
PUT /libraries/{id}
Content-Type: application/json
```
Updates the name of a specific library.

**Request Body:**
```json
{
  "name": "Updated Library Name"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Updated Library Name"
}
```

#### Update Library Address
```http
PUT /libraries/{id}/address/{addressId}
```
Associates an address with a library.

**Response:**
```json
{
  "id": 1,
  "name": "Central Library"
}
```

## Data Models

### Address
- `id` (Long): Unique identifier
- `location` (String): Address location

### Book
- `id` (Long): Unique identifier
- `title` (String): Book title

### Library
- `id` (Long): Unique identifier
- `name` (String): Library name
- `address` (Address): Associated address (one-to-one relationship)

## Testing the API

You can test the API using curl commands or any REST client like Postman.

### Example curl commands:

```bash
# Get all libraries
curl -X GET http://localhost:8080/libraries

# Create a new address
curl -X POST http://localhost:8080/addresses \
  -H "Content-Type: application/json" \
  -d '{"location": "123 Main St, City, State"}'

# Create a new book
curl -X POST http://localhost:8080/books \
  -H "Content-Type: application/json" \
  -d '{"title": "The Great Gatsby"}'

# Create a new library
curl -X POST http://localhost:8080/libraries \
  -H "Content-Type: application/json" \
  -d '{"name": "Central Library"}'
```

## Error Handling

The API returns appropriate HTTP status codes:
- `200 OK`: Successful operation
- `201 Created`: Resource created successfully
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## Technologies Used

- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- Maven
- Java 17+
