package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.ForbiddenOperationException;
import dev.gustavo.math.repository.SubmissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProblemService problemService;

    @Mock
    private JudgeService judgeService;

    @InjectMocks
    private SubmissionService submissionService;

    private Problem problem;
    private Submission submission;
    private User user;
    private UUID userId;
    private final Long problemId = 1L;
    private final Long submissionId = 1L;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        problem = new Problem();
        problem.setId(problemId);

        submission = new Submission();
        submission.setId(submissionId);
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setAnswer("2x");
    }

    @Nested
    @DisplayName("Find Submissions")
    class FindSubmissions {
        @Test
        @DisplayName("Should return a paginated list of all submissions")
        void findAllShouldReturnPaginatedSubmissions() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            when(submissionRepository.findAll(pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.findAll(pageable);

            assertFalse(submissions.isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(submissionRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should find a submission by its ID successfully")
        void findByIdWithUserShouldReturnSubmissionWhenFound() {
            when(submissionRepository.findByIdWithUser(submissionId)).thenReturn(Optional.of(submission));

            Submission foundSubmission = submissionService.findByIdWithUser(submissionId);

            assertNotNull(foundSubmission);
            assertEquals(submissionId, foundSubmission.getId());
            verify(submissionRepository, times(1)).findByIdWithUser(submissionId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when submission does not exist")
        void findByIdWithUserShouldThrowExceptionWhenNotFound() {
            when(submissionRepository.findByIdWithUser(submissionId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> submissionService.findByIdWithUser(submissionId));
        }

        @Test
        @DisplayName("Should allow owner to find a submission by ID")
        void findByIdForUserShouldReturnSubmissionForOwner() {
            when(submissionRepository.findByIdWithUser(submissionId)).thenReturn(Optional.of(submission));

            Submission foundSubmission = submissionService.findByIdForUser(submissionId, userId, false);

            assertNotNull(foundSubmission);
            assertEquals(submissionId, foundSubmission.getId());
            verify(submissionRepository, times(1)).findByIdWithUser(submissionId);
        }

        @Test
        @DisplayName("Should allow admin to find a submission by ID")
        void findByIdForUserShouldReturnSubmissionForAdmin() {
            UUID anotherUserId = UUID.randomUUID();
            when(submissionRepository.findByIdWithUser(submissionId)).thenReturn(Optional.of(submission));

            Submission foundSubmission = submissionService.findByIdForUser(submissionId, anotherUserId, true);

            assertNotNull(foundSubmission);
            assertEquals(submissionId, foundSubmission.getId());
            verify(submissionRepository, times(1)).findByIdWithUser(submissionId);
        }

        @Test
        @DisplayName("Should reject access when user does not own the submission")
        void findByIdForUserShouldThrowForbiddenWhenUserIsNotOwner() {
            UUID anotherUserId = UUID.randomUUID();
            when(submissionRepository.findByIdWithUser(submissionId)).thenReturn(Optional.of(submission));

            assertThrows(ForbiddenOperationException.class,
                    () -> submissionService.findByIdForUser(submissionId, anotherUserId, false));
        }
    }

    @Nested
    @DisplayName("List Submission")
    class ListSubmission {
        @Test
        @DisplayName("Should return a paginated list of all submissions from a user")
        void listShouldReturnPagedSubmissionsFromUserWhenUserExists() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            doNothing().when(userService).existsById(userId);
            when(submissionRepository.findByUserIdWithProblem(userId, pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.listFromUser(user, pageable);

            assertFalse(submissions.getContent().isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(userService, times(1)).existsById(userId);
            verify(submissionRepository, times(1)).findByUserIdWithProblem(userId, pageable);
        }

        @Test
        @DisplayName("Should return a paginated list of all submissions in a problem")
        void listShouldReturnPagedSubmissionsInProblemWhenProblemExists() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            doNothing().when(problemService).existsById(problemId);
            when(submissionRepository.findByProblemIdWithUser(problemId, pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.listInProblem(problem, pageable);

            assertFalse(submissions.getContent().isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(problemService, times(1)).existsById(problemId);
            verify(submissionRepository, times(1)).findByProblemIdWithUser(problemId, pageable);
        }

        @Test
        @DisplayName("Should return a paginated list of all submissions from a user in a problem")
        void listShouldReturnPagedSubmissionsFromUserInProblemWhenBothExists() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            doNothing().when(userService).existsById(userId);
            doNothing().when(problemService).existsById(problemId);
            when(submissionRepository.findByUserAndProblem(user, problem, pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.listFromUserInProblem(user, problem, pageable);

            assertFalse(submissions.isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(userService, times(1)).existsById(userId);
            verify(problemService, times(1)).existsById(problemId);
            verify(submissionRepository, times(1)).findByUserAndProblem(user, problem, pageable);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void listFromUserShouldThrowExceptionWhenUserNotFound() {
            PageRequest pageable = PageRequest.of(0, 10);
            doThrow(new EntityNotFoundException("User", userId.toString())).when(userService).existsById(userId);

            assertThrows(EntityNotFoundException.class, () -> submissionService.listFromUser(user, pageable));
            verify(submissionRepository, never()).findByUserIdWithProblem(any(UUID.class), any(PageRequest.class));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when problem does not exist")
        void listInProblemShouldThrowExceptionWhenProblemNotFound() {
            PageRequest pageable = PageRequest.of(0, 10);
            doThrow(new EntityNotFoundException("Problem", problemId.toString())).when(problemService).existsById(problemId);

            assertThrows(EntityNotFoundException.class, () -> submissionService.listInProblem(problem, pageable));
            verify(submissionRepository, never()).findByProblemIdWithUser(anyLong(), any(PageRequest.class));
        }
    }

    @Nested
    @DisplayName("Create Submission")
    class CreateSubmission {
        @Test
        @DisplayName("Should create and return submission with ACCEPTED status for correct answer")
        void createShouldReturnAcceptedWhenAnswerIsCorrect() {
            mockProblemWithTestCases();
            mockSubmissionSave();
            mockJudgingStatus(SubmissionStatus.ACCEPTED);

            Submission createdSubmission = submissionService.create(submission, userId);

            assertCreatedSubmission(createdSubmission, SubmissionStatus.ACCEPTED);
        }

        @Test
        @DisplayName("Should create and return submission with WRONG_ANSWER status for incorrect answer")
        void createShouldReturnWrongAnswerWhenAnswerIsIncorrect() {
            mockProblemWithTestCases();
            mockSubmissionSave();
            mockJudgingStatus(SubmissionStatus.WRONG_ANSWER);

            Submission createdSubmission = submissionService.create(submission, userId);

            assertCreatedSubmission(createdSubmission, SubmissionStatus.WRONG_ANSWER);
        }

        @Test
        @DisplayName("Should create and return submission with WRONG_ANSWER for invalid answer syntax")
        void createShouldReturnWrongAnswer_forInvalidAnswer() {
            submission.setAnswer("2*x+");
            mockProblemWithTestCases(new TestCase(1L, problem, "2", "4.0"));
            mockSubmissionSave();
            mockJudgingStatus(SubmissionStatus.WRONG_ANSWER);

            Submission createdSubmission = submissionService.create(submission, userId);

            assertCreatedSubmission(createdSubmission, SubmissionStatus.WRONG_ANSWER);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void createShouldThrowExceptionWhenUserNotFound() {
            doThrow(new EntityNotFoundException("User", userId.toString())).when(userService).existsById(userId);

            assertThrows(EntityNotFoundException.class, () -> submissionService.create(submission, userId));
            verify(problemService, never()).findByIdWithTestCases(anyLong());
            verify(submissionRepository, never()).save(any(Submission.class));
        }

        private void mockProblemWithTestCases(TestCase... testCases) {
            if (testCases.length == 0) {
                testCases = new TestCase[]{
                        new TestCase(1L, problem, "2", "4.0"),
                        new TestCase(2L, problem, "5", "10.0")
                };
            }
            problem.setTestCases(List.of(testCases));
            doNothing().when(userService).existsById(userId);
            when(problemService.findByIdWithTestCases(problemId)).thenReturn(problem);
        }

        private void mockSubmissionSave() {
            when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        }

        private void mockJudgingStatus(SubmissionStatus status) {
            doAnswer(call -> {
                Submission sub = call.getArgument(1);
                sub.setStatus(status);
                return null;
            }).when(judgeService).judge(any(Problem.class), any(Submission.class));
        }

        private void assertCreatedSubmission(Submission createdSubmission, SubmissionStatus expectedStatus) {
            assertNotNull(createdSubmission);
            assertEquals(expectedStatus, createdSubmission.getStatus());
            assertEquals(userId, createdSubmission.getUser().getId());
            verify(submissionRepository, times(1)).save(submission);
            verify(judgeService, times(1)).judge(problem, submission);
        }
    }

    @Nested
    @DisplayName("Delete Submission")
    class DeleteSubmission {
        @Test
        @DisplayName("Should delete a submission successfully")
        void deleteShouldRemoveSubmissionWhenSubmissionExists() {
            when(submissionRepository.existsById(submissionId)).thenReturn(true);
            doNothing().when(submissionRepository).deleteById(submissionId);

            submissionService.delete(submissionId);

            verify(submissionRepository, times(1)).existsById(submissionId);
            verify(submissionRepository, times(1)).deleteById(submissionId);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when submission does not exist")
        void deleteShouldThrowExceptionWhenSubmissionNotFound() {
            when(submissionRepository.existsById(submissionId)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> submissionService.delete(submissionId));
            verify(submissionRepository, never()).deleteById(anyLong());
        }
    }
}
