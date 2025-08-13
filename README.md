# Math Judge API

## About

The backend for a platform designed to host and evaluate mathematical challenges. The system allows users to register, submit solutions to math problems, and have their answers automatically validated running them against set of test cases.

### Technologies

- Java 21
- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Flyway for database versioning
- MapStruct for DTO mapping
- Lombok
- exp4j for mathematical expression evaluation

## Features already implemented

### Users (`/api/v1/users`)
- `POST /api/v1/users` → creates a new user
    ```json
    {
      "username": "username",
      "password": "password",
      "nickname": "nickname"
    }
    ```
- `GET /api/v1/users` → lists all users
- `GET /api/v1/users/{id}` → returns a specific user
- `PUT /api/v1/users/{id}` → updates a user
- `DELETE /api/v1/users/{id}` → deletes a user and its submissions

### Challenges (`/api/v1/challenges`)
- `POST /api/v1/challenges` → creates a new challenge
    ```json
    {
      "title": "Double a number",
      "description": "Given an input x, return 2*x",
      "difficulty": "EASY"
    }
    ```
- `GET /api/v1/challenges` → lists all challenges
- `GET /api/v1/challenges/{id}` → returns a specific challenge
- `PUT /api/v1/challenges/{id}` → updates a challenge
- `DELETE /api/v1/challenges/{id}` → deletes a challenge and its test cases/submissions

### Test Cases (`/api/v1/testcases`)
- `POST /api/v1/testcases` → creates a test case linked to a challenge
    ```json
    {
      "challenge": 1,
      "input": "10",
      "expectedOutput": "20"
    } 
    ```
- `PUT /api/v1/testcases/{id}` → updates a test case
- `DELETE /api/v1/testcases/{id}` → deletes a test case

### Submissions (`/api/v1/submissions`)
- `POST /api/v1/submissions` → creates a submission, evaluates it automatically, and saves the result (`ACCEPTED` or `WRONG_ANSWER`)
    ```json
    {
      "challenge": 1,
      "user": "uuid-of-user",
      "expression": "2x"
    }
    ```
    - Uses an optimized query to load challenge + test cases in one trip to the DB
    - Evaluation is done via **exp4j** using a single variable `x`
- `GET /api/v1/submissions` → lists all submissions
- `GET /api/v1/submissions/{id}` → returns a specific submission
- `DELETE /api/v1/submissions/{id}` → deletes a submission

---

### Pending Features

- [ ] Additional endpoints
    - List all submissions of a user
    - List submissions of a user for a specific challenge
    - List all submissions for a specific challenge
- [ ] Additional endpoint validations
- [ ] Swagger/OpenAPI documentation
- [ ] Security
    - JWT token authentication
    - Route protection by role (`ROLE_USER`, `ROLE_ADMIN`)
    - Password encryption
- [ ] User ranking system
- [ ] Improve expression evaluation
  - Expressions with multiple variables (beyond just 'x')
- [ ] [Not priority] Async processing
  - Implement `PENDING`, `EVALUATING`, `ACCEPTED`, `WRONG_ANSWER` statuses using asynchronous evaluation logic
