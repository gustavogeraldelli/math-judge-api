package dev.gustavo.math.processor;

import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.SubmissionRepository;
import dev.gustavo.math.service.judge.JudgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubmissionJudgingProcessor {

    private final SubmissionRepository submissionRepository;
    private final JudgeService judgeService;

    @Transactional
    public void judge(Long submissionId) {
        var submission = submissionRepository.findByIdWithProblemAndTestCases(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("Submission", submissionId.toString()));

        submission.setStatus(SubmissionStatus.EVALUATING);

        try {
            var result = judgeService.judge(submission.getProblem(), submission.getAnswer());
            submission.setStatus(result.status());
        }
        catch (RuntimeException e) {
            submission.setStatus(SubmissionStatus.ERROR);
        }
    }
}
