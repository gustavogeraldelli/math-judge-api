package dev.gustavo.math.service;

import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.ForbiddenOperationException;
import dev.gustavo.math.repository.SubmissionRepository;
import dev.gustavo.math.service.judge.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserService userService;
    private final ProblemService problemService;
    private final JudgeService judgeService;

    public Page<Submission> findByFilters(UUID userId, Long problemId, SubmissionStatus status,
                                          UUID currentUserId, boolean isAdmin, Pageable pageable) {
        UUID effectiveUserId = userId;

        if (!isAdmin) {
            if (userId != null && !userId.equals(currentUserId))
                throw new ForbiddenOperationException("You cannot access another user's submissions");
            effectiveUserId = currentUserId;
        }

        if (effectiveUserId != null)
            userService.existsById(effectiveUserId);

        if (problemId != null)
            problemService.existsById(problemId);

        return submissionRepository.findByFilters(effectiveUserId, problemId, status, pageable);
    }

    public Submission findByIdWithUser(Long id) {
        return submissionRepository.findByIdWithUser(id).orElseThrow(
                () -> new EntityNotFoundException("Submission", id.toString()));
    }

    public Submission findByIdForUser(Long id, UUID currentUserId, boolean isAdmin) {
        var submission = findByIdWithUser(id);

        if (!isAdmin && !submission.getUser().getId().equals(currentUserId))
            throw new ForbiddenOperationException("You cannot access this submission");

        return submission;
    }

    public Submission create(Submission submission, UUID currentUserId) {
        return create(submission, submission.getProblem().getId(), currentUserId);
    }

    public Submission create(Submission submission, Long problemId, UUID currentUserId) {
        userService.existsById(currentUserId);

        var problem = problemService.findByIdWithTestCases(problemId);
        var user = new User();
        user.setId(currentUserId);
        submission.setProblem(problem);
        submission.setUser(user);

        var result = judgeService.judge(problem, submission.getAnswer());
        submission.setStatus(result.status());

        return submissionRepository.save(submission);
    }

    public void delete(Long id) {
        if (!submissionRepository.existsById(id))
            throw new EntityNotFoundException("Submission", id.toString());
        submissionRepository.deleteById(id);
    }

}
