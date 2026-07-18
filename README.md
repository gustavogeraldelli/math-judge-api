# Math Judge API

Backend API for a math problem judging platform. The system lets users authenticate, browse problems, submit answers, evaluate submissions asynchronously, and rank users by accepted solutions.

The API combines security, relational modeling, database migrations, asynchronous processing, caching, observability, rate limiting, API documentation, and automated tests in a single Spring Boot service.

## Features

- User registration and authentication with JWT access tokens and refresh token rotation.
- Role-based authorization for users and administrators.
- Problem and test case management.
- Numeric and expression-based answer evaluation.
- Declared variables for expression problems, preventing ambiguous variable names in submissions.
- Asynchronous submission judging with Spring events and `@Async`.
- Ranking by distinct accepted problems, with optional difficulty filtering.
- Local caching for problem reads and ranking queries.
- Public healthcheck endpoints through Spring Boot Actuator.
- Local rate limiting for login and submission creation.
- Unit, controller, and integration tests with PostgreSQL through Testcontainers.

## Tech Stack

- **Java 21**
- **Spring Boot 3.5**
- **Spring Web**, **Spring Data JPA**, **Spring Security**, **Spring Cache**, **Spring Boot Actuator**
- **PostgreSQL** and **Flyway**
- **JWT** with `java-jwt`
- **Caffeine** for local in-memory cache
- **Bucket4j** for local rate limiting
- **MapStruct** and **Lombok**
- **Springdoc OpenAPI / Swagger**
- **JUnit 5**, **Mockito**, **MockMvc**, **Testcontainers**
- **exp4j** for expression evaluation

## Architecture

The application follows a layered Spring Boot structure:

```text
controller      HTTP endpoints, validation, authentication context
dto             request and response contracts
mapper          MapStruct conversions between DTOs and entities
service         business rules and transactional workflows
repository      Spring Data JPA persistence
entity          JPA domain model
processor       asynchronous submission judging
infra           security, cache, rate limiting, actuator-related config
```

Main flow for a submission:

```text
POST /api/v1/problems/{problemId}/submissions
  -> JWT authentication identifies the user
  -> submission rate limit is checked per authenticated user
  -> submission is saved as PENDING
  -> SubmissionCreatedEvent is published
  -> @Async listener starts background judging
  -> JudgeService selects the evaluator by problem type
  -> submission status becomes ACCEPTED, WRONG_ANSWER, or ERROR
```

## Domain Model

- **User**: account with role and rank.
- **Problem**: math challenge with difficulty, type, and optional declared variables.
- **TestCase**: input values and expected answer for a problem.
- **Submission**: user answer and judging status.
- **RefreshToken**: hashed refresh token with expiration and revocation support.

Supported problem types:

- **NUMERIC**: the submitted answer is a numeric value compared with tolerance.
- **EXPRESSION**: the submitted answer is a mathematical expression evaluated against all test cases.

Expression problems explicitly declare valid variable names. For example, a problem with `variables: ["x"]` expects submissions to use `x`; an answer using another variable name is rejected.

## Security

- Access tokens are JWTs signed with an application secret.
- Refresh tokens are generated as random values and stored only as SHA-256 hashes.
- Refresh token rotation revokes the current token and returns a new one.
- Logout revokes the refresh token.
- Authorization is enforced with route rules and method-level checks where ownership matters.

Rate limits:

- `POST /api/v1/auth/login`: 5 attempts per minute per IP.
- `POST /api/v1/problems/{problemId}/submissions`: 30 submissions per minute per authenticated user.

The current rate limiting implementation is local and in-memory. A distributed deployment would require shared storage such as Redis.

## Observability

Only essential Actuator endpoints are exposed:

- `GET /actuator/health`
- `GET /actuator/info`

The project also logs relevant security and judging events, such as rejected access tokens and submission judging results.

## API Documentation

Run the application and open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`

## Main Endpoints

### Authentication

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`

### Users

- `GET /api/v1/users`
- `GET /api/v1/users/{id}`
- `PUT /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

### Problems

- `GET /api/v1/problems`
- `GET /api/v1/problems/{id}`
- `POST /api/v1/problems`
- `PUT /api/v1/problems/{id}`
- `DELETE /api/v1/problems/{id}`

### Test Cases

- `GET /api/v1/problems/{problemId}/testcases`
- `POST /api/v1/problems/{problemId}/testcases`
- `PUT /api/v1/testcases/{id}`
- `DELETE /api/v1/testcases/{id}`

### Submissions

- `GET /api/v1/submissions`
- `GET /api/v1/submissions/{id}`
- `POST /api/v1/problems/{problemId}/submissions`
- `DELETE /api/v1/submissions/{id}`

`GET /api/v1/submissions` supports `userId`, `problemId`, `status`, `page`, and `size`. Admin users can query all submissions; regular users can only access their own.

### Ranking

- `GET /api/v1/ranking`

Optional query params: `difficulty`, `page`, `size`.

## Examples

### Numeric Problem

Problem:

```text
What is the solution for 2x + 6 = 10?
```

Expected submission:

```json
{
  "answer": "2"
}
```

### Expression Problem

Problem:

```text
Find the derivative of x^3.
```

Problem variables:

```json
["x"]
```

Test case:

```json
{
  "variableValues": "{\"x\": 2}",
  "expectedAnswer": "12"
}
```

Expected submission:

```json
{
  "answer": "3*x^2"
}
```

## Running Locally

Start PostgreSQL:

```bash
docker-compose -f docker/docker-compose.yml up -d
```

Run the application:

```bash
./mvnw spring-boot:run
```

Stop PostgreSQL:

```bash
docker-compose -f docker/docker-compose.yml down
```

## Testing

Run the fast test suite:

```bash
./mvnw test
```

Run full verification, including the Testcontainers integration test:

```bash
./mvnw verify
```

Current coverage style:

- Service tests for business rules.
- Controller tests for HTTP contracts, validation, authentication, and authorization.
- Dedicated judge tests for numeric and expression evaluation.
- Integration test for the main submission flow against real PostgreSQL.

## Implementation Notes

- **Caffeine over Redis**: keeps local development simple while preserving explicit cache design and invalidation decisions.
- **Spring events with `@Async`**: decouples submission creation from judging without introducing external messaging infrastructure.
- **Refresh token hashing**: avoids storing raw refresh tokens in the database.
- **Declared expression variables**: makes problem contracts explicit and avoids accepting arbitrary variable names.
- **Testcontainers**: validates Flyway migrations and persistence behavior against PostgreSQL instead of relying only on H2.
