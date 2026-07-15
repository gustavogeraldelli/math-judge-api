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

class ExpressionAnswerEvaluatorTest {

    private ExpressionAnswerEvaluator evaluator;
    private Problem problem;

    @BeforeEach
    void setUp() {
        evaluator = new ExpressionAnswerEvaluator();

        problem = new Problem();
        problem.setId(1L);
        problem.setType(ProblemType.EXPRESSION);
        problem.setTestCases(List.of(
                new TestCase(1L, problem, "2", "4.0"),
                new TestCase(2L, problem, "5", "10.0")
        ));
    }

    @Test
    @DisplayName("Should accept correct expression")
    void shouldAcceptCorrectExpression() {
        var result = evaluator.evaluate(problem, "2*x");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }

    @Test
    @DisplayName("Should reject expression when test case fails")
    void shouldRejectExpressionWhenTestCaseFails() {
        var result = evaluator.evaluate(problem, "x+2");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }

    @Test
    @DisplayName("Should reject syntactically invalid expression")
    void shouldRejectInvalidSyntaxExpression() {
        var result = evaluator.evaluate(problem, "2*x+");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }

    @Test
    @DisplayName("Should reject test case with invalid variable value")
    void shouldRejectInvalidVariableValue() {
        problem.setTestCases(List.of(new TestCase(1L, problem, "two", "4.0")));

        var result = evaluator.evaluate(problem, "2*x");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }

    @Test
    @DisplayName("Should use tolerance when comparing expression result")
    void shouldUseToleranceWhenComparingExpressionResult() {
        problem.setTestCases(List.of(new TestCase(1L, problem, "2", "4.0000000005")));

        var result = evaluator.evaluate(problem, "2*x");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }
}
