package dev.gustavo.math.integration;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.entity.enums.UserRank;
import dev.gustavo.math.entity.enums.UserRole;
import dev.gustavo.math.repository.SubmissionRepository;
import dev.gustavo.math.service.ProblemService;
import dev.gustavo.math.service.SubmissionService;
import dev.gustavo.math.service.TestCaseService;
import dev.gustavo.math.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
class SubmissionFlowIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.show-sql", () -> "false");
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Test
    void shouldPersistAndEvaluateSubmissionWithPostgresAndFlyway() throws InterruptedException {
        var user = userService.create(user());
        var problem = problemService.create(numericProblem());
        testCaseService.create(testCase(problem));

        var submission = new Submission();
        submission.setProblem(problem);
        submission.setAnswer("4");

        var createdSubmission = submissionService.create(submission, user.getId());

        var persistedSubmission = submissionRepository.findById(createdSubmission.getId()).orElseThrow();
        assertNotNull(createdSubmission.getId());
        assertNotNull(createdSubmission.getSubmittedAt());
        assertEquals(SubmissionStatus.PENDING, createdSubmission.getStatus());
        assertEquals(SubmissionStatus.ACCEPTED, awaitFinalStatus(createdSubmission.getId()));
    }

    private User user() {
        var user = new User();
        user.setUsername("john");
        user.setPassword("password123");
        user.setNickname("Johnny");
        user.setRole(UserRole.ROLE_USER);
        user.setRank(UserRank.BEGINNER);
        return user;
    }

    private Problem numericProblem() {
        var problem = new Problem();
        problem.setTitle("Equation");
        problem.setDescription("Solve 2x = 8");
        problem.setDifficulty(ProblemDifficulty.EASY);
        problem.setType(ProblemType.NUMERIC);
        return problem;
    }

    private TestCase testCase(Problem problem) {
        var testCase = new TestCase();
        testCase.setProblem(problem);
        testCase.setVariableValues("{}");
        testCase.setExpectedAnswer("4");
        return testCase;
    }

    private SubmissionStatus awaitFinalStatus(Long submissionId) throws InterruptedException {
        for (int attempt = 0; attempt < 20; attempt++) {
            var status = submissionRepository.findById(submissionId).orElseThrow().getStatus();
            if (status == SubmissionStatus.ACCEPTED || status == SubmissionStatus.WRONG_ANSWER || status == SubmissionStatus.ERROR)
                return status;
            Thread.sleep(100);
        }
        return submissionRepository.findById(submissionId).orElseThrow().getStatus();
    }
}
