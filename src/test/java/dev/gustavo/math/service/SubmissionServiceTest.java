package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.User;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.event.SubmissionCreatedEvent;
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
import org.springframework.context.ApplicationEventPublisher;
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
    private ApplicationEventPublisher eventPublisher;

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
        @DisplayName("Should allow admin to list submissions without filters")
        void findByFiltersShouldAllowAdminWithoutFilters() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            when(submissionRepository.findByFilters(null, null, null, pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.findByFilters(null, null, null, UUID.randomUUID(), true, pageable);

            assertFalse(submissions.isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(submissionRepository, times(1)).findByFilters(null, null, null, pageable);
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
        @DisplayName("Should allow user to list own submissions")
        void findByFiltersShouldAllowUserToListOwnSubmissions() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            doNothing().when(userService).existsById(userId);
            when(submissionRepository.findByFilters(userId, null, null, pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.findByFilters(null, null, null, userId, false, pageable);

            assertFalse(submissions.getContent().isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(userService, times(1)).existsById(userId);
            verify(submissionRepository, times(1)).findByFilters(userId, null, null, pageable);
        }

        @Test
        @DisplayName("Should allow admin to filter submissions by user, problem and status")
        void findByFiltersShouldAllowAdminWithFilters() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Submission> submissionPage = new PageImpl<>(List.of(submission));
            doNothing().when(userService).existsById(userId);
            doNothing().when(problemService).existsById(problemId);
            when(submissionRepository.findByFilters(userId, problemId, SubmissionStatus.ACCEPTED, pageable)).thenReturn(submissionPage);

            Page<Submission> submissions = submissionService.findByFilters(userId, problemId, SubmissionStatus.ACCEPTED, UUID.randomUUID(), true, pageable);

            assertFalse(submissions.getContent().isEmpty());
            assertEquals(1, submissions.getTotalElements());
            verify(userService, times(1)).existsById(userId);
            verify(problemService, times(1)).existsById(problemId);
            verify(submissionRepository, times(1)).findByFilters(userId, problemId, SubmissionStatus.ACCEPTED, pageable);
        }

        @Test
        @DisplayName("Should reject regular user filtering by another user")
        void findByFiltersShouldRejectRegularUserFilteringByAnotherUser() {
            PageRequest pageable = PageRequest.of(0, 10);
            UUID anotherUserId = UUID.randomUUID();

            assertThrows(ForbiddenOperationException.class,
                    () -> submissionService.findByFilters(anotherUserId, null, null, userId, false, pageable));

            verify(submissionRepository, never()).findByFilters(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void findByFiltersShouldThrowExceptionWhenUserNotFound() {
            PageRequest pageable = PageRequest.of(0, 10);
            doThrow(new EntityNotFoundException("User", userId.toString())).when(userService).existsById(userId);

            assertThrows(EntityNotFoundException.class,
                    () -> submissionService.findByFilters(userId, null, null, UUID.randomUUID(), true, pageable));
            verify(submissionRepository, never()).findByFilters(any(), any(), any(), any());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when problem does not exist")
        void findByFiltersShouldThrowExceptionWhenProblemNotFound() {
            PageRequest pageable = PageRequest.of(0, 10);
            doThrow(new EntityNotFoundException("Problem", problemId.toString())).when(problemService).existsById(problemId);

            assertThrows(EntityNotFoundException.class,
                    () -> submissionService.findByFilters(null, problemId, null, UUID.randomUUID(), true, pageable));
            verify(submissionRepository, never()).findByFilters(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("Create Submission")
    class CreateSubmission {
        @Test
        @DisplayName("Should create a pending submission and publish event")
        void createShouldReturnPendingAndPublishEvent() {
            mockProblem();
            mockSubmissionSave();

            Submission createdSubmission = submissionService.create(submission, userId);

            assertCreatedSubmission(createdSubmission);
            verify(eventPublisher, times(1)).publishEvent(new SubmissionCreatedEvent(submissionId));
        }

        @Test
        @DisplayName("Should create pending submission using problem ID from path")
        void createShouldUseProblemIdArgument() {
            Long pathProblemId = 99L;
            problem.setId(pathProblemId);
            submission.setProblem(null);
            mockProblem(pathProblemId);
            mockSubmissionSave();

            Submission createdSubmission = submissionService.create(submission, pathProblemId, userId);

            assertCreatedSubmission(createdSubmission);
            assertEquals(pathProblemId, createdSubmission.getProblem().getId());
            verify(problemService, times(1)).findById(pathProblemId);
            verify(eventPublisher, times(1)).publishEvent(new SubmissionCreatedEvent(submissionId));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when user does not exist")
        void createShouldThrowExceptionWhenUserNotFound() {
            doThrow(new EntityNotFoundException("User", userId.toString())).when(userService).existsById(userId);

            assertThrows(EntityNotFoundException.class, () -> submissionService.create(submission, userId));
            verify(problemService, never()).findById(anyLong());
            verify(submissionRepository, never()).save(any(Submission.class));
            verify(eventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when problem does not exist")
        void createShouldThrowExceptionWhenProblemNotFound() {
            doNothing().when(userService).existsById(userId);
            doThrow(new EntityNotFoundException("Problem", problemId.toString())).when(problemService).findById(problemId);

            assertThrows(EntityNotFoundException.class, () -> submissionService.create(submission, userId));
            verify(submissionRepository, never()).save(any(Submission.class));
            verify(eventPublisher, never()).publishEvent(any());
        }

        private void mockProblem() {
            mockProblem(problemId);
        }

        private void mockProblem(Long id) {
            doNothing().when(userService).existsById(userId);
            when(problemService.findById(id)).thenReturn(problem);
        }

        private void mockSubmissionSave() {
            when(submissionRepository.save(any(Submission.class))).thenReturn(submission);
        }

        private void assertCreatedSubmission(Submission createdSubmission) {
            assertNotNull(createdSubmission);
            assertEquals(SubmissionStatus.PENDING, createdSubmission.getStatus());
            assertEquals(userId, createdSubmission.getUser().getId());
            assertEquals(problem, createdSubmission.getProblem());
            verify(submissionRepository, times(1)).save(submission);
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
