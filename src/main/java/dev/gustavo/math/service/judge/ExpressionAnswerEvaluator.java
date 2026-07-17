package dev.gustavo.math.service.judge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.util.ProblemVariables;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ExpressionAnswerEvaluator implements AnswerEvaluator {

    private static final double EPSILON = 1e-9;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Double>> VARIABLE_VALUES_TYPE = new TypeReference<>() {
    };
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
    private static final Set<String> EXP4J_FUNCTIONS = Set.of(
            "abs", "acos", "asin", "atan", "cbrt", "ceil", "cos", "cosh", "exp", "floor",
            "log", "log10", "log2", "sin", "sinh", "sqrt", "tan", "tanh", "signum"
    );
    private static final Set<String> EXP4J_CONSTANTS = Set.of("e", "pi");

    @Override
    public ProblemType supports() {
        return ProblemType.EXPRESSION;
    }

    @Override
    public EvaluationResult evaluate(Problem problem, String answer) {
        Set<String> declaredVariables;
        Set<String> answerVariables;

        try {
            declaredVariables = new HashSet<>(ProblemVariables.fromJson(problem.getVariables()));
            answerVariables = extractVariables(answer);
        }
        catch (Exception e) {
            return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);
        }

        if (declaredVariables.isEmpty() || !declaredVariables.containsAll(answerVariables))
            return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);

        for (TestCase testCase : problem.getTestCases()) {
            try {
                Map<String, Double> variableValues = parseVariableValues(testCase, declaredVariables);

                var expressionBuilder = new ExpressionBuilder(answer);
                for (String variable : answerVariables) {
                    expressionBuilder.variable(variable);
                }

                Expression expression = expressionBuilder.build();
                for (String variable : answerVariables) {
                    Double variableValue = variableValues.get(variable);
                    if (variableValue == null)
                        return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);
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
        var matcher = IDENTIFIER_PATTERN.matcher(expression);
        while (matcher.find()) {
            String identifier = matcher.group();
            if (!EXP4J_FUNCTIONS.contains(identifier) && !EXP4J_CONSTANTS.contains(identifier))
                variables.add(identifier);
        }
        return variables;
    }

    private Map<String, Double> parseVariableValues(TestCase testCase, Set<String> declaredVariables) throws JsonProcessingException {
        String rawVariableValues = testCase.getVariableValues();
        if (rawVariableValues == null || rawVariableValues.isBlank())
            return Map.of();

        String trimmedVariableValues = rawVariableValues.trim();
        if (trimmedVariableValues.startsWith("{"))
            return OBJECT_MAPPER.readValue(trimmedVariableValues, VARIABLE_VALUES_TYPE);

        if (declaredVariables.size() == 1) {
            Map<String, Double> variableValues = new HashMap<>();
            variableValues.put(declaredVariables.iterator().next(), Double.parseDouble(trimmedVariableValues));
            return variableValues;
        }

        return Map.of();
    }

    private boolean nearlyEquals(double actual, double expected) {
        return Math.abs(actual - expected) <= EPSILON;
    }

}
