package dev.gustavo.math.service.judge;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import org.springframework.stereotype.Component;

@Component
public class NumericAnswerEvaluator implements AnswerEvaluator {

    private static final double EPSILON = 1e-9;

    @Override
    public ProblemType supports() {
        return ProblemType.NUMERIC;
    }

    @Override
    public EvaluationResult evaluate(Problem problem, String answer) {
        TestCase testCase =  problem.getTestCases().getFirst();

        try {
            double expectedAnswer = Double.parseDouble(testCase.getExpectedAnswer());
            double submittedAnswer = Double.parseDouble(answer);

            if (nearlyEquals(submittedAnswer, expectedAnswer))
                return new EvaluationResult(SubmissionStatus.ACCEPTED);
            return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);
        }
        catch (NumberFormatException e) {
            return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);
        }
    }

    private boolean nearlyEquals(double actual, double expected) {
        return Math.abs(actual - expected) <= EPSILON;
    }

}
