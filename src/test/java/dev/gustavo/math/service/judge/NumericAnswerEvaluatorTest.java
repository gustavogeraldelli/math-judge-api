package dev.gustavo.math.service.judge;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NumericAnswerEvaluatorTest {

    private NumericAnswerEvaluator evaluator;
    private Problem problem;

    @BeforeEach
    void setUp() {
        evaluator = new NumericAnswerEvaluator();

        problem = new Problem();
        problem.setId(1L);
        problem.setType(ProblemType.NUMERIC);
        problem.setTestCases(List.of(new TestCase(1L, problem, null, "7.0")));
    }

    @Test
    @DisplayName("Should accept a correct answer")
    void shouldAcceptCorrectAnswer() {
        var result = evaluator.evaluate(problem, "7.0");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }

    @Test
    @DisplayName("Should accept numerically equivalent answers")
    void shouldAcceptNumericallyEquivalentAnswer() {
        var result = evaluator.evaluate(problem, "7");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }

    @Test
    @DisplayName("Should accept numeric answer within tolerance")
    void shouldAcceptNumericAnswerWithinTolerance() {
        var result = evaluator.evaluate(problem, "7.0000000005");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }

    @Test
    @DisplayName("Should reject incorrect answer")
    void shouldRejectIncorrectAnswer() {
        var result = evaluator.evaluate(problem, "8.0");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }

    @Test
    @DisplayName("Should reject invalid submitted answer")
    void shouldRejectInvalidSubmittedAnswer() {
        var result = evaluator.evaluate(problem, "7+");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }

    @Test
    @DisplayName("Should reject invalid expected answer")
    void shouldRejectInvalidExpectedAnswer() {
        problem.setTestCases(List.of(new TestCase(1L, problem, null, "seven")));

        var result = evaluator.evaluate(problem, "7");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }
}
