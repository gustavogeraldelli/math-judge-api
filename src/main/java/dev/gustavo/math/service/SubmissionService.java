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
        if (challenge.getSubmissions().isEmpty())
            return;

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
                //
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                return;
            }
        }
        submission.setStatus(SubmissionStatus.ACCEPTED);
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
