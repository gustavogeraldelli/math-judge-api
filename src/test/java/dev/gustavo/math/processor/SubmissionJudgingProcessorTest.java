package dev.gustavo.math.processor;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.SubmissionRepository;
import dev.gustavo.math.service.judge.EvaluationResult;
import dev.gustavo.math.service.judge.JudgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionJudgingProcessorTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private JudgeService judgeService;

    @InjectMocks
    private SubmissionJudgingProcessor submissionJudgingProcessor;

    private Submission submission;
    private Problem problem;
    private final Long submissionId = 1L;

    @BeforeEach
    void setUp() {
        problem = new Problem();
        problem.setId(10L);

        submission = new Submission();
        submission.setId(submissionId);
        submission.setProblem(problem);
        submission.setAnswer("4");
        submission.setStatus(SubmissionStatus.PENDING);
    }

    @Test
    @DisplayName("Should evaluate submission and store final status")
    void judgeShouldEvaluateSubmissionAndStoreFinalStatus() {
        when(submissionRepository.findByIdWithProblemAndTestCases(submissionId)).thenReturn(Optional.of(submission));
        when(judgeService.judge(problem, submission.getAnswer()))
                .thenReturn(new EvaluationResult(SubmissionStatus.ACCEPTED));

        submissionJudgingProcessor.judge(submissionId);

        assertEquals(SubmissionStatus.ACCEPTED, submission.getStatus());
        verify(judgeService, times(1)).judge(problem, submission.getAnswer());
    }

    @Test
    @DisplayName("Should mark submission as error when judging fails")
    void judgeShouldMarkSubmissionAsErrorWhenJudgingFails() {
        when(submissionRepository.findByIdWithProblemAndTestCases(submissionId)).thenReturn(Optional.of(submission));
        when(judgeService.judge(problem, submission.getAnswer())).thenThrow(new IllegalArgumentException("Unsupported problem type"));

        submissionJudgingProcessor.judge(submissionId);

        assertEquals(SubmissionStatus.ERROR, submission.getStatus());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when submission does not exist")
    void judgeShouldThrowExceptionWhenSubmissionDoesNotExist() {
        when(submissionRepository.findByIdWithProblemAndTestCases(submissionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> submissionJudgingProcessor.judge(submissionId));
        verify(judgeService, never()).judge(any(), anyString());
    }
}
