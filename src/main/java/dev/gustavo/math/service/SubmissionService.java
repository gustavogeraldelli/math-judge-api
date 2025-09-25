package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
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
    private final ProblemService problemService;
    private final JudgeService judgeService;

    public Page<Submission> findAll(Pageable pageable) {
        return submissionRepository.findAll(pageable);
    }

    public Submission findByIdWithUser(Long id) {
        return submissionRepository.findByIdWithUser(id).orElseThrow(
                () -> new EntityNotFoundException("Submission", id.toString()));
    }

    public Submission create(Submission submission) {
        userService.existsById(submission.getUser().getId());

        var problem = problemService.findByIdWithTestCases(submission.getProblem().getId());

        judgeService.judge(problem, submission);

        return submissionRepository.save(submission);
    }

    public void delete(Long id) {
        if (!submissionRepository.existsById(id))
            throw new EntityNotFoundException("Submission", id.toString());
        submissionRepository.deleteById(id);
    }

    public Page<Submission> listFromUser(User user, Pageable pageable) {
        userService.existsById(user.getId());
        return submissionRepository.findByUserIdWithProblem(user.getId(), pageable);
    }

    public Page<Submission> listInProblem(Problem problem, Pageable pageable) {
        problemService.existsById(problem.getId());
        return submissionRepository.findByProblemIdWithUser(problem.getId(), pageable);
    }

    public Page<Submission> listFromUserInProblem(User user, Problem problem, Pageable pageable) {
        userService.existsById(user.getId());
        problemService.existsById(problem.getId());
        return submissionRepository.findByUserAndProblem(user, problem, pageable);
    }

}
