# Math Judge API

The backend for a platform designed to host and evaluate mathematical challenges.
Users can register, submit solutions to math problems, and have their answers automatically validated against a set of test cases.

## Overview
The API was designed to be a complete and efficient solution for mathematical challenge platforms, offering:

- User registration and authentication with JWT tokens
- Challenge management, including creation, updating, and deletion
- Test case management for each challenge
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
### Authentication (`/api/v1/auth`)
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

### Users (`/api/v1/users`)
- `GET /api/v1/users` → lists all users (admin only)
- `GET /api/v1/users/{id}` → get user info (self or admin)
- `GET /api/v1/{id}/submissions` → list all submissions of a user (self or admin)
- `GET /api/v1/{userId}/challenges/{challengeId}/submissions` → list all submissions of a user for a specific challenge (self or admin)


### Challenges (`/api/v1/challenges`)
- `GET /api/v1/challenges` → list all challenges (user/admin)
- `GET /api/v1/challenges/{id}/submissions` → list all submissions for a challenge (admin only)

### Submissions (`/api/v1/submissions`)
- `POST /api/v1/submissions` → submits a solution to a challenge
  - Payload
    ```json
    {
      "challenge": 1,
      "user": "user-uuid",
      "expression": "2x"
    }
    ```
  - Response
    ```json
    {
      "challenge": 1,
      "status": "ACCEPTED",
      "submittedAt": "2025-08-16T21:38:00"
    }
    ```

Refer to the Swagger documentation for detailed examples of all endpoints.

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
- [ ] Improve expression evaluation
- [ ] Expressions with multiple variables (beyond just 'x')
- [ ] Async processing
  - Implement `PENDING`, `EVALUATING`, `ACCEPTED`, `WRONG_ANSWER` statuses using asynchronous evaluation logic
