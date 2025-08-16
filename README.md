# Math Judge API

## About

The backend for a platform designed to host and evaluate mathematical challenges.
Users can register, submit solutions to math problems, and have their answers automatically validated against a set of test cases.

### Technologies

- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Security with JWT authentication
- PostgreSQL
- Flyway for database versioning
- MapStruct for DTO mapping
- Lombok
- exp4j for mathematical expression evaluation

## API

### Authentication (`/api/v1/auth`)

<details>

<summary>Endpoints</summary>

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

</details>

      
### Users (`/api/v1/users`)

<details>

<summary>Endpoints</summary>

- `GET /api/v1/users` → lists all users (admin only)
- `GET /api/v1/users/{id}` → get user info (self or admin)
- `PUT /api/v1/users/{id}` → update user (self or admin)
- `DELETE /api/v1/users/{id}` → delete user and its submissions (self or admin)
- `GET /api/v1/{id}/submissions` → list all submissions of a user (self or admin)
- `GET /api/v1/{userId}/challenges/{challengeId}/submissions` → ist all submissions of a user for a specific challenge (self or admin)

</details>

### Challenges (`/api/v1/challenges`)

<details>

<summary>Endpoints</summary>

- `POST /api/v1/challenges` → creates a new challenge
  - Payload
    ```json
    {
      "title": "Derivatives 101",
      "description": "Find the derivative of x^2",
      "difficulty": "EASY"
    }
    ```
- `GET /api/v1/challenges` → lists all challenges (user/admin)
- `GET /api/v1/challenges/{id}` → returns a specific challenge (user/admin)
- `PUT /api/v1/challenges/{id}` → updates a challenge (admin only)
- `DELETE /api/v1/challenges/{id}` → deletes a challenge and its test cases and submissions (admin only)
- `GET /api/v1/challenges/{id}/submissions` → list all submissions for a challenge (admin only)

</details>

### Test Cases (`/api/v1/testcases`)

<details>

<summary>Endpoints</summary>

- `POST /api/v1/testcases` → create a test case for a challenge (admin only)
  - Payload
    ```json
    {
      "challenge": 1,
      "input": "10",
      "expectedOutput": "20"
    } 
    ```
- `PUT /api/v1/testcases/{id}` → updates a test case (admin only)
- `DELETE /api/v1/testcases/{id}` → deletes a test case (admin only)

</details>

### Submissions (`/api/v1/submissions`)

<details>

<summary>Endpoints</summary>

- `POST /api/v1/submissions` → create a submission (user/admin)
  - Payload
    ```json
    {
      "challenge": 1,
      "user": "uuid-of-user",
      "expression": "2x"
    }
    ```
  - Response
    ```json
    {
      "challenge": 1,
      "status": "ACCEPTED",
      "submittedAt": "timestamp"
    }
    ```
- `GET /api/v1/submissions` → lists all submissions (admin only)
- `GET /api/v1/submissions/{id}` → returns a specific submission (user/admin)
- `DELETE /api/v1/submissions/{id}` → deletes a submission (admin only)

</details>

---

### Pending Features

- [ ] Swagger/OpenAPI documentation
- [ ] Improve DTOs
- [ ] User ranking system
- [ ] Users must be able to see only their own submissions by ID
- [ ] Improve expression evaluation
- [x] Additional endpoints
    - List all submissions of a user
    - List submissions of a user for a specific challenge
    - List all submissions for a specific challenge
- [x] Additional endpoint validations
- [x] Security
    - JWT token authentication
    - Route protection by role (`ROLE_USER`, `ROLE_ADMIN`)
    - Password encryption
  - Expressions with multiple variables (beyond just 'x')
- [ ] Async processing
  - Implement `PENDING`, `EVALUATING`, `ACCEPTED`, `WRONG_ANSWER` statuses using asynchronous evaluation logic
