package dev.gustavo.math.service.judge;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JudgeServiceTest {

    @Test
    @DisplayName("Should route numeric problem to numeric evaluator")
    void shouldRouteNumericProblemToNumericEvaluator() {
        var problem = new Problem();
        problem.setType(ProblemType.NUMERIC);
        problem.setTestCases(List.of(new TestCase(1L, problem, null, "7.0")));
        var judgeService = new JudgeService(List.of(new NumericAnswerEvaluator(), new ExpressionAnswerEvaluator()));

        var result = judgeService.judge(problem, "7");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }

    @Test
    @DisplayName("Should route expression problem to expression evaluator")
    void shouldRouteExpressionProblemToExpressionEvaluator() {
        var problem = new Problem();
        problem.setType(ProblemType.EXPRESSION);
        problem.setVariables("[\"x\"]");
        problem.setTestCases(List.of(new TestCase(1L, problem, "2", "4.0")));
        var judgeService = new JudgeService(List.of(new NumericAnswerEvaluator(), new ExpressionAnswerEvaluator()));

        var result = judgeService.judge(problem, "2*x");

        assertEquals(SubmissionStatus.ACCEPTED, result.status());
    }

    @Test
    @DisplayName("Should reject problem without test cases")
    void shouldRejectProblemWithoutTestCases() {
        var problem = new Problem();
        problem.setType(ProblemType.NUMERIC);
        problem.setTestCases(List.of());
        var judgeService = new JudgeService(List.of(new NumericAnswerEvaluator(), new ExpressionAnswerEvaluator()));

        var result = judgeService.judge(problem, "7");

        assertEquals(SubmissionStatus.WRONG_ANSWER, result.status());
    }
}
