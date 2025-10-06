package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JudgeServiceTest {

    private JudgeService judgeService;

    private Problem exprProblem;
    private Problem numProblem;
    private Submission submission;

    @BeforeAll
    void setUp() {
        judgeService = new JudgeService();

        exprProblem = new Problem();
        var testCases1 = List.of(new TestCase(1L, exprProblem, "2", "4.0"),
                new TestCase(2L, exprProblem, "5", "10.0"));
        exprProblem.setId(1L);
        exprProblem.setType(ProblemType.EXPRESSION);
        exprProblem.setTestCases(testCases1);

        numProblem = new Problem();
        var testCases2 = List.of(new TestCase(3L, numProblem, null, "7.0"));
        numProblem.setId(1L);
        numProblem.setType(ProblemType.NUMERIC);
        numProblem.setTestCases(testCases2);

        submission = new Submission();
        submission.setId(1L);
    }

    @Nested
    @DisplayName("Judging expression problems")
    class ExpressionJudge {
        @Test
        @DisplayName("Should set status to ACCEPTED for correct expression")
        void shouldAcceptCorrectExpression() {
            submission.setAnswer("2*x");

            judgeService.judge(exprProblem, submission);

            assertEquals(SubmissionStatus.ACCEPTED, submission.getStatus());
        }

        @Test
        @DisplayName("Should set status to WRONG_ANSWER when test case fails")
        void shouldBeWrongAnswerWhenTestCaseFails() {
            submission.setAnswer("x+2");

            judgeService.judge(exprProblem, submission);

            assertEquals(SubmissionStatus.WRONG_ANSWER, submission.getStatus());
        }

        @Test
        @DisplayName("Should set status to WRONG_ANSWER for syntactically invalid expression")
        void shouldSetWrongAnswerForInvalidSyntaxExpression() {
            submission.setAnswer("2*x+");

            judgeService.judge(exprProblem, submission);

            assertEquals(SubmissionStatus.WRONG_ANSWER, submission.getStatus());
        }
    }

    @Nested
    @DisplayName("Judging numeric problems")
    class NumericJudge {
        @Test
        @DisplayName("Should set status to ACCEPTED for a correct answer")
        void shouldAcceptCorrectAnswer() {
            submission.setAnswer("7.0");

            judgeService.judge(numProblem, submission);

            assertEquals(SubmissionStatus.ACCEPTED, submission.getStatus());
        }

        @Test
        @DisplayName("Should set status to WRONG_ANSWER for incorrect answer")
        void shouldSetWrongAnswerWhenAnswerIsWrong() {
            submission.setAnswer("8.0");

            judgeService.judge(numProblem, submission);

            assertEquals(SubmissionStatus.WRONG_ANSWER, submission.getStatus());
        }

        @Test
        @DisplayName("Should set status to WRONG_ANSWER for syntactically invalid expression")
        void shouldSetWrongAnswerForInvalidSyntaxAnswer() {
            submission.setAnswer("7+");

            judgeService.judge(numProblem, submission);

            assertEquals(SubmissionStatus.WRONG_ANSWER, submission.getStatus());
        }
    }

}
