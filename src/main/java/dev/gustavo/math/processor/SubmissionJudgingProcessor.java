package dev.gustavo.math.processor;

import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.SubmissionRepository;
import dev.gustavo.math.service.judge.JudgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionJudgingProcessor {

    private final SubmissionRepository submissionRepository;
    private final JudgeService judgeService;

    @Transactional
    @CacheEvict(value = "ranking", allEntries = true)
    public void judge(Long submissionId) {
        var submission = submissionRepository.findByIdWithProblemAndTestCases(submissionId)
                .orElseThrow(() -> {
                    log.warn("Submission judging skipped: submissionId={} reason=not_found", submissionId);
                    return new EntityNotFoundException("Submission", submissionId.toString());
                });

        Long problemId = submission.getProblem().getId();
        log.info("Submission judging started: submissionId={} problemId={}", submissionId, problemId);

        submission.setStatus(SubmissionStatus.EVALUATING);

        try {
            var result = judgeService.judge(submission.getProblem(), submission.getAnswer());
            submission.setStatus(result.status());
            log.info("Submission judging finished: submissionId={} problemId={} status={}",
                    submissionId, problemId, result.status());
        }
        catch (RuntimeException e) {
            submission.setStatus(SubmissionStatus.ERROR);
            log.error("Submission judging failed: submissionId={} problemId={} status={} error={}",
                    submissionId, problemId, SubmissionStatus.ERROR, e.getMessage());
        }
    }
}
