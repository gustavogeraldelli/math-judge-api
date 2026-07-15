package dev.gustavo.math.service.judge;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ExpressionAnswerEvaluator implements AnswerEvaluator {

    private static final double EPSILON = 1e-9;

    @Override
    public ProblemType supports() {
        return ProblemType.EXPRESSION;
    }

    @Override
    public EvaluationResult evaluate(Problem problem, String answer) {
        for (TestCase testCase : problem.getTestCases()) {
            try {
                Set<String> variables = extractVariables(answer);

                var expressionBuilder = new ExpressionBuilder(answer);
                for (String variable : variables) {
                    expressionBuilder.variable(variable);
                }

                Expression expression = expressionBuilder.build();
                for (String variable : variables) {
                    double variableValue = Double.parseDouble(testCase.getVariableValues());
                    expression.setVariable(variable, variableValue);
                }

                double result = expression.evaluate();
                double expectedAnswer = Double.parseDouble(testCase.getExpectedAnswer());
                if (!nearlyEquals(result, expectedAnswer))
                    return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);
            }
            catch (Exception e) {
                return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);
            }
        }

        return new EvaluationResult(SubmissionStatus.ACCEPTED);
    }

    private Set<String> extractVariables(String expression) {
        Set<String> variables = new HashSet<>();
        if (expression.contains("x"))
            variables.add("x");
        return variables;
    }

    private boolean nearlyEquals(double actual, double expected) {
        return Math.abs(actual - expected) <= EPSILON;
    }

}
