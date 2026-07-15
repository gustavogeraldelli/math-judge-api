package dev.gustavo.math.service.judge;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JudgeService {

    private final List<AnswerEvaluator> evaluators;

    public EvaluationResult judge(Problem problem, String answer) {
        if (problem.getTestCases().isEmpty())
            return new EvaluationResult(SubmissionStatus.WRONG_ANSWER);

        return evaluators.stream()
                .filter(evaluator -> evaluator.supports() == problem.getType())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported problem type: " + problem.getType()))
                .evaluate(problem, answer);
    }
}
