# Math Judge API
## Overview
The API was designed to be a complete and efficient solution for mathematical problem platforms, offering:

- User registration and authentication with JWT tokens
- Problem management, including creation, updating, and deletion
- Test case management for each problem
- Solution submission and automatic evaluation
- Role-based access control for administrative endpoints

## Technologies
- **Java 21**, **Spring Boot 3.5.4**
- **Maven**
- **PostgreSQL**, **Flyway**, **Spring Data JPA**
- **Spring Security** with **JWT**
- **JUnit 5** and **Mockito** for tests
- **Springdoc OpenAPI (Swagger)** for API documentation
- **MapStruct** and **Lombok** for productivity
- **exp4j** for mathematical expression evaluation

## API Documentation
- Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`
- The documentation JSON is available at `http://localhost:8080/api-docs`

## Main Endpoints

<details>
<summary><b>See more</b></summary>

### Authentication `/api/v1/auth`
- `POST /api/v1/auth/register` → registers a new user
  - Payload
    ```json
    {
      "username": "username",
      "password": "password",
      "nickname": "nickname"
    }
    ```
- `POST /api/v1/auth/login` → returns a JWT token
  - Payload
    ```json
    {
      "username": "username",
      "password": "password"
    }
    ```

  - Response
    ```json
    {
      "token": "jwt-token"
    }
    ```

### Users `/api/v1/users`
- `GET /api/v1/users/{id}` → get user info (self or admin)
- `GET /api/v1/{id}/submissions` → list all submissions of a user (self or admin)
- `GET /api/v1/{userId}/problems/{problemId}/submissions` → list all submissions of a user for a specific problem (self or admin)


### Problems `/api/v1/problems`
- `GET /api/v1/problems` → list all problems (user/admin)
- `GET /api/v1/problems/{id}/submissions` → list all submissions for a problem (admin only)

### Submissions `/api/v1/submissions`
- `POST /api/v1/submissions` → submits a solution to a problem
  - Payload
    ```json
    {
      "problem": 1,
      "user": "user-uuid",
      "expression": "2x"
    }
    ```
  - Response
    ```json
    {
      "problem": 1,
      "status": "ACCEPTED",
      "submittedAt": "2025-08-16T21:38:00"
    }
    ```

Refer to the Swagger documentation for detailed examples of all endpoints.

</details>

## Problems and Submissions
Each problem describes a mathematical problem. Users must submit a solution that passes all predefined test cases for that problem.

### 1. Numeric submission
The user must find the numeric value of one or more variables that satisfy an equation.
- Example
  - Problem: _"What is the solution of the equation `2x + 6 = 10`?"_
  - Submission:
    ```json 
    { "expression": "2" }
    ```
    
### 2. Expression submission
The user must provide a mathematical expression that matches the problem statement.
- Example
  - Problem: _"Find the derivative of `x^3`?"_
  - Submission:
    ```json 
    { "expression": "3x^2" }
    ```
## Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── dev/gustavo/math/
│   │       ├── controller/ 
│   │       │   ├── dto/
│   │       │   └── doc/
│   │       ├── entity/
│   │       │   └── enums
│   │       ├── exceptions/
│   │       ├── infra/
│   │       │   ├── config/
│   │       │   └── security/
│   │       ├── mapper/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── db/migration/
│       └── application.yml
└── test/
    └── java/
        └── dev/gustavo/math/
            └── service/
```

## Build, Execution, and Shutdown
- Start the database with Docker
  ```bash
  docker-compose -f docker/docker-compose.yml up -d
  ```

- Run the tests
  ```bash
  ./mvnw test
  ```

- Run the application
  ```bash
  ./mvnw spring-boot:run
  ```

- Stop the database
  ```bash
  docker-compose docker/docker-compose.yml down
  ```


## Future Improvements
- [ ] Improve DTOs
- [ ] User ranking system
- [ ] Async processing
  - Implement `PENDING`, `EVALUATING`, `ACCEPTED`, `WRONG_ANSWER` statuses using asynchronous evaluation logic
- [ ] Expressions with multiple variables (beyond just 'x')
