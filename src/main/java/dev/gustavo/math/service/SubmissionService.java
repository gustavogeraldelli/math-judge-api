package dev.gustavo.math.service;

import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.InvalidForeignKeyException;
import dev.gustavo.math.repository.SubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final ChallengeService challengeService;

    public Page<Submission> findAll(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }

    public Submission findById(Long id) {
        return submissionRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Submission with id %s not found", id)));
    }

    public Submission create(Submission submission) {
        if (!userService.existsById(submission.getUser().getId()))
            throw new InvalidForeignKeyException("submission", submission.getUser().getId().toString());

        var challenge = challengeService.findByIdWithTestCases(submission.getChallenge().getId());

        judgeSubmission(challenge, submission);

        return submissionRepository.save(submission);
    }

    public void delete(Long id) {
        if (!submissionRepository.existsById(id))
            throw new EntityNotFoundException(String.format("Submission with id %s not found", id));
        submissionRepository.deleteById(id);
    }

    private void judgeSubmission(Challenge challenge, Submission submission) {
        for (TestCase tc : challenge.getTestCases()) {
            try {
                double input = Double.parseDouble(tc.getInput());
                Expression expression = new ExpressionBuilder(submission.getExpression())
                        .variables("x")
                        .build()
                        .setVariable("x", input);
                double result = expression.evaluate();
                double expectedOutput = Double.parseDouble(tc.getExpectedOutput());

                if (Math.abs(result - expectedOutput) > 1e-9) {
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

}
