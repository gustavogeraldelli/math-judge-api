package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JudgeService {

    private static final double EPSILON = 1e-9;

    public void judge(Problem problem, Submission submission) {
        if (problem.getTestCases().isEmpty())
            return;
        if (problem.getType() == ProblemType.NUMERIC)
            evaluateNumericAnswer(problem, submission);
        else if (problem.getType() == ProblemType.EXPRESSION)
            evaluateExpressionAnswer(problem, submission);
    }

    private void evaluateNumericAnswer(Problem problem, Submission submission) {
        TestCase tc =  problem.getTestCases().get(0);
        try {
            double expectedAnswer = Double.parseDouble(tc.getExpectedAnswer());
            double submittedAnswer = Double.parseDouble(submission.getAnswer());

            if (nearlyEquals(submittedAnswer, expectedAnswer))
                submission.setStatus(SubmissionStatus.ACCEPTED);
            else
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
        }
        catch (NumberFormatException e) {
            submission.setStatus(SubmissionStatus.WRONG_ANSWER);
        }
    }

    private boolean nearlyEquals(double actual, double expected) {
        return Math.abs(actual - expected) <= EPSILON;
    }

    private void evaluateExpressionAnswer(Problem problem, Submission submission) {
        for (TestCase tc : problem.getTestCases()) {
            try {
                Set<String> variables = extractVariables(submission.getAnswer());

                var exprBuilder = new ExpressionBuilder(submission.getAnswer());
                // no variable or only x for now
                for (String variable : variables) {
                    exprBuilder.variable(variable);
                }
                Expression expression = exprBuilder.build();
                for (String variable : variables) {
                    double variableValues = Double.parseDouble(tc.getVariableValues());
                    expression.setVariable(variable, variableValues);
                }

                double result = expression.evaluate();
                double expectedAnswer = Double.parseDouble(tc.getExpectedAnswer());
                if (!nearlyEquals(result, expectedAnswer)) {
                    submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                    return;
                }
            }
            catch (Exception e) {
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                return;
            }
        }
        submission.setStatus(SubmissionStatus.ACCEPTED);
    }

    private Set<String> extractVariables(String expression) {
        Set<String> vars = new HashSet<>();
        if (expression.contains("x"))
            vars.add("x");
        return vars;
    }
}
