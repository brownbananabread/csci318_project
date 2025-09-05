<<<<<<< Updated upstream
# CSCI318 Software Engineering Practices & Principles - Group Project
=======
# Spring Boot Microservices

This is an improved project based on
the project [Spring Boot Introduction - Rest Service](https://github.com/gxshub/rest-services-with-spring-v2).
The following new components and patterns are implmeneted:

### __Library and Book Services__
The Book and Library sub-domains are separately implemented in two standalone microservices:
__Book Service__ and __Library Service__. The Book Service uses the `RestTemplate`
API to communicate with the Library Service.

### Application Service Layer
An application service is added to the __Book Service__.
This layer interplays between the REST API Layer (`controller`) and
the Data Access Layer (`repository`).
It also includes a `RestTemplate` client to get data
from the __Library Service__.

### DTO Pattern
DTOs or Data Transfer Objects are objects that carry data
between processes in order to reduce the number of methods
calls. They are also views that prevent the exposure of
domain classes to the external world.

### Domain Events
The __Book Service__ implements domain event patterns. 
More specifically, it uses two ways of publishing and handling domain events
enabled in Spring Boot, i.e.,
the `AbstractAggregateRoot` generic class
(used in [`Book.java`](./book-service/src/main/java/csci318/demo/model/Book.java) and
the `ApplicationEventPublisher` interface
(used in [`BookService.java`](./book-service/src/main/java/csci318/demo/service/BookService.java)).
(Reference: [https://www.baeldung.com/spring-data-ddd](https://www.baeldung.com/spring-data-ddd))

### Event-Sourcing

The __Book Service__ also implements the event sourcing pattern.
When events of borrows books are created, they are persistent in a table named `BOOK_EVENT` in H2.
In other words, this table stores the change of system states.

#### View Tables in the H2 Console
<!--
Add the following two lines to `src/main/resources/application.properties`:
```properties
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb
```
-->
Go to the H2 console [http://localhost:8082/h2-console/](http://localhost:8082/h2-console/).
<!-- To log on, change the value in the `JDBC URL` entry to 
`jdbc:h2:mem:testdb`. 
Compare the `BOOK` table and the `BOOK_EVENT` table (which is
considered as an event store).-->



## Demonstration Use Cases
_(1) Find all books_:
```shell
curl -X GET http://localhost:8082/books
```
which returns
```json
[{"title":"Introduction to Software Engineering","isbn":"0-684-84328-5"},{"title":"Domain Drive Design","isbn":"93-86954-21-4"}]
```

_(2) Find libraries in which a book with a specific ISBN is available_:
```shell
curl -X GET http://localhost:8082/books/{0-684-84328-5}/available
```
which returns
```json
[{"libraryName":"Wollongong City Library","postcode":"2500"},{"libraryName":"UOW Library","postcode":"2522"}]
```

_(3) Borrow the book `0-684-84328-5` from the Wollongong City Library (2500)_:
```shell
curl -X PUT http://localhost:8082/books/borrow/0-684-84328-5/2500
```
_(4) Check the change of availability for the book_:
```shell
curl -X GET http://localhost:8082/books/{0-684-84328-5}/available
```
_(5) Return it to the same library_:
```shell
curl -X PUT http://localhost:8082/books/return/0-684-84328-5/2500
```
_(6) Check the change of availability (again)_:
```shell
curl -X GET http://localhost:8082/books/{0-684-84328-5}/available
```

### _TODO_
Examples of further improvement:
- Improve use case (3). Consider what information should be returned 
if borrowing a book is successful or unsuccessful.
- Add an application service layer in __Library Service__.
- Implement some DTO classes for one method in the Library Service.
- Consider a use case “For all books by library”. Implement this use case by allowing the Library Service to 
to communicate with the Book Service using the RestTemplate API.

<!---
_TODO._
When borrowing a book which is not available in the library
(i.e., performing step (1) two consecutive times), an event is not
created in the `BOOK_EVENT` event store.
This is correct as the unsuccessful request does not change the system state.
However, information should be returned to the customer to indicate the (un)successful
book borrowing request.
For this purpose,
implement a DTO object as the return of the PUT request used in step (1).

--->
>>>>>>> Stashed changes
