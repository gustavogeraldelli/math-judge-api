package dev.gustavo.math.service.judge;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.enums.ProblemType;

public interface AnswerEvaluator {

    ProblemType supports();

    EvaluationResult evaluate(Problem problem, String answer);
}
