# Math Judge API
## Overview
The API was designed to be a complete and efficient solution for mathematical problem platforms, offering:

- User registration and authentication with JWT access tokens and refresh tokens
- Problem management, including creation, updating, and deletion
- Test case management for each problem
- Solution submission and automatic evaluation
- Role-based access control for administrative endpoints

## Technologies
- **Java 21**, **Spring Boot 3.5.4**
- **Maven**
- **PostgreSQL**, **Flyway**, **Spring Data JPA**
- **Spring Cache** with **Caffeine** for local in-memory caching
- **Spring Security** with **JWT**
- **Spring Boot Actuator** for healthcheck endpoints
- **JUnit 5**, **Mockito** and **Testcontainers** for tests
- **Springdoc OpenAPI (Swagger)** for API documentation
- **MapStruct** and **Lombok** for productivity
- **exp4j** for mathematical expression evaluation

## API Documentation
- Access the Swagger UI at `http://localhost:8080/swagger-ui/index.html`
- The documentation JSON is available at `http://localhost:8080/api-docs`

## Healthcheck
- `GET /actuator/health` → public application healthcheck
- `GET /actuator/info` → public application info endpoint
- Only `health` and `info` are exposed by default.

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
- `POST /api/v1/auth/login` → returns access and refresh tokens
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
      "accessToken": "jwt-access-token",
      "refreshToken": "refresh-token",
      "tokenType": "Bearer",
      "expiresIn": 1800
    }
    ```
- `POST /api/v1/auth/refresh` → exchanges a valid refresh token for a new access token
  - Payload
    ```json
    {
      "refreshToken": "refresh-token"
    }
    ```
- `POST /api/v1/auth/logout` → revokes a refresh token
  - Payload
    ```json
    {
      "refreshToken": "refresh-token"
    }
    ```

### Users `/api/v1/users`
- `GET /api/v1/users` → list users (admin only)
- `GET /api/v1/users/{id}` → get user info (self or admin)
- `PUT /api/v1/users/{id}` → update user info (self or admin)
- `DELETE /api/v1/users/{id}` → delete user (self or admin)

### Problems `/api/v1/problems`
- `GET /api/v1/problems` → list all problems (user/admin)
- `GET /api/v1/problems/{id}` → get problem details (user/admin)
- `POST /api/v1/problems` → create a problem (admin only)
- `PUT /api/v1/problems/{id}` → update a problem (admin only)
- `DELETE /api/v1/problems/{id}` → delete a problem (admin only)
- Expression problems expose `variables`, the valid variable names that submissions must use.

### Test cases
- `GET /api/v1/problems/{problemId}/testcases` → list test cases for a problem (admin only)
- `POST /api/v1/problems/{problemId}/testcases` → create a test case for a problem (admin only)
- `PUT /api/v1/testcases/{id}` → update a test case (admin only)
- `DELETE /api/v1/testcases/{id}` → delete a test case (admin only)

### Submissions `/api/v1/submissions`
- `GET /api/v1/submissions` → list submissions with optional filters
  - Filters: `userId`, `problemId`, `status`, `page`, `size`
  - Admin users can query any submission.
  - Regular users can only query their own submissions.
- `GET /api/v1/submissions/{id}` → get a submission (owner or admin)
- `POST /api/v1/problems/{problemId}/submissions` → submit a solution to a problem
  - The submission is created with `PENDING` status and evaluated asynchronously.
- `DELETE /api/v1/submissions/{id}` → delete a submission (admin only)
  - Payload
    ```json
    {
      "answer": "2x"
    }
    ```
  - Response
    ```json
    {
      "id": 42,
      "problem": 1,
      "status": "PENDING",
      "submittedAt": "2025-08-16T21:38:00"
    }
    ```

### Ranking `/api/v1/ranking`
- `GET /api/v1/ranking` → list users ordered by distinct accepted problems
  - Optional filters: `difficulty`, `page`, `size`
  - Example: `GET /api/v1/ranking?difficulty=HARD`

Refer to the Swagger documentation for detailed examples of all endpoints.

</details>

## Problems and Submissions
Each problem describes a mathematical problem. Users must submit a solution that passes all predefined test cases for that problem.

### 1. Numeric submission
The user must find the numeric value of one or more variables that satisfy an equation.
- Example
  - Problem: _"What is the solution for the equation `2x + 6 = 10`?"_
  - Submission:
    ```json 
    { "answer": "2" }
    ```
    
### 2. Expression submission
The user must provide a mathematical expression that matches the problem statement.
Expression problems declare the valid variables explicitly. A submission that uses a different variable name is rejected.
- Example
  - Problem: _"Find the derivative of `x^3`"_
  - Problem variables:
    ```json
    ["x"]
    ```
  - Test case:
    ```json
    {
      "variableValues": "{\"x\": 2}",
      "expectedAnswer": "12"
    }
    ```
  - Submission:
    ```json 
    { "answer": "3*x^2" }
    ```

## Build, Execution, and Shutdown
- Start the database with Docker
  ```bash
  docker-compose -f docker/docker-compose.yml up -d
  ```

- Run the fast test suite
  ```bash
  ./mvnw test
  ```

- Run the full verification, including Testcontainers integration tests
  ```bash
  ./mvnw verify
  ```

- Run the application
  ```bash
  ./mvnw spring-boot:run
  ```

- Stop the database
  ```bash
  docker-compose -f docker/docker-compose.yml down
  ```


## Future Improvements
- [ ] Improve DTOs
- [ ] Expressions with multiple variables (beyond just 'x')
