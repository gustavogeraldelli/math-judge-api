package dev.gustavo.math.service;

import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final ChallengeService challengeService;

    public Page<Submission> findAll(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }

    public Submission findByIdWithUser(Long id) {
        return submissionRepository.findByIdWithUser(id).orElseThrow(
                () -> new EntityNotFoundException("Submission", id.toString()));
    }

    public Submission create(Submission submission) {
        userService.existsById(submission.getUser().getId());

        var challenge = challengeService.findByIdWithTestCases(submission.getChallenge().getId());

        judgeSubmission(challenge, submission);

        return submissionRepository.save(submission);
    }

    public void delete(Long id) {
        if (!submissionRepository.existsById(id))
            throw new EntityNotFoundException("Submission", id.toString());
        submissionRepository.deleteById(id);
    }

    private void judgeSubmission(Challenge challenge, Submission submission) {
        if (challenge.getTestCases().isEmpty())
            return;

        for (TestCase tc : challenge.getTestCases()) {
            try {
                Set<String> variables = extractVariables(submission.getExpression());

                var exprBuilder = new ExpressionBuilder(submission.getExpression());
                // no variable or only x for now
                for (String variable : variables) {
                    exprBuilder.variable(variable);
                }
                Expression expression = exprBuilder.build();
                for (String variable : variables) {
                    double input = Double.parseDouble(tc.getInput());
                    expression.setVariable(variable, input);
                }

                double result = expression.evaluate();
                double expectedOutput = Double.parseDouble(tc.getExpectedOutput());

                if (Math.abs(result - expectedOutput) > 1e-9) {
                    submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                    return;
                }
            }
            catch (Exception e) {
                //
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                return;
            }
        }
        submission.setStatus(SubmissionStatus.ACCEPTED);
    }

    private Set<String> extractVariables(String expression) {
        Set<String> vars = new HashSet<>();
        if (expression.contains("x")) {
            vars.add("x");
        }
        return vars;
    }

    public Page<Submission> listFromUser(User user, Pageable pageable) {
        userService.existsById(user.getId());
        return submissionRepository.findByUserIdWithChallenge(user.getId(), pageable);
    }

    public Page<Submission> listInChallenge(Challenge challenge, Pageable pageable) {
        challengeService.existsById(challenge.getId());
        return submissionRepository.findByChallengeIdWithUser(challenge.getId(), pageable);
    }

    public Page<Submission> listFromUserInChallenge(User user, Challenge challenge, Pageable pageable) {
        userService.existsById(user.getId());
        challengeService.existsById(challenge.getId());
        return submissionRepository.findByUserAndChallenge(user, challenge, pageable);
    }

}
