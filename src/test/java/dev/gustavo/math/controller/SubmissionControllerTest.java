package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.exception.ForbiddenOperationException;
import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.infra.security.SecurityConfig;
import dev.gustavo.math.infra.security.SecurityFilter;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.service.SubmissionService;
import dev.gustavo.math.service.auth.AccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubmissionController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class SubmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private SubmissionService submissionService;

    @MockitoBean
    private SubmissionMapper submissionMapper;

    @Nested
    @DisplayName("Endpoints")
    class Endpoints {

        @Test
        @DisplayName("Should create submission for authenticated user")
        void shouldCreateSubmissionForAuthenticatedUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var problem = new Problem();
            problem.setId(10L);
            var submission = new Submission();
            submission.setProblem(problem);
            submission.setAnswer("2*x");
            var createdSubmission = new Submission();
            createdSubmission.setId(42L);
            createdSubmission.setProblem(problem);
            createdSubmission.setStatus(SubmissionStatus.PENDING);
            createdSubmission.setSubmittedAt(LocalDateTime.of(2026, 7, 15, 20, 0));
            var response = new SubmissionResponseDTO(42L, 10L, SubmissionStatus.PENDING, createdSubmission.getSubmittedAt());

            authenticate("user-token", userId, "ROLE_USER");
            when(submissionMapper.toSubmission(any())).thenReturn(submission);
            when(submissionService.create(submission, 10L, userId)).thenReturn(createdSubmission);
            when(submissionMapper.toSubmissionResponseDTO(createdSubmission)).thenReturn(response);

            mockMvc.perform(post("/api/v1/problems/10/submissions")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "answer": "2*x"
                                    }
                                    """))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.problem").value(10))
                    .andExpect(jsonPath("$.status").value("PENDING"));

            verify(submissionService).create(submission, 10L, userId);
        }

        @Test
        @DisplayName("Should allow admin to list submissions with filters")
        void shouldAllowAdminToListSubmissionsWithFilters() throws Exception {
            UUID adminId = UUID.randomUUID();
            UUID userId = UUID.randomUUID();
            var problem = new Problem();
            problem.setId(10L);
            var submission = new Submission();
            submission.setId(42L);
            submission.setProblem(problem);
            submission.setStatus(SubmissionStatus.ACCEPTED);
            submission.setSubmittedAt(LocalDateTime.of(2026, 7, 15, 20, 0));
            var response = new SubmissionResponseDTO(42L, 10L, SubmissionStatus.ACCEPTED, submission.getSubmittedAt());

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(submissionService.findByFilters(userId, 10L, SubmissionStatus.ACCEPTED, adminId, true, PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(submission), PageRequest.of(0, 10), 1));
            when(submissionMapper.toSubmissionResponseDTO(submission)).thenReturn(response);

            mockMvc.perform(get("/api/v1/submissions")
                            .param("userId", userId.toString())
                            .param("problemId", "10")
                            .param("status", "ACCEPTED")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].id").value(42))
                    .andExpect(jsonPath("$.items[0].problem").value(10))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(submissionService).findByFilters(userId, 10L, SubmissionStatus.ACCEPTED, adminId, true, PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should allow user to list own submissions")
        void shouldAllowUserToListOwnSubmissions() throws Exception {
            UUID userId = UUID.randomUUID();
            var problem = new Problem();
            problem.setId(10L);
            var submission = new Submission();
            submission.setId(42L);
            submission.setProblem(problem);
            submission.setStatus(SubmissionStatus.ACCEPTED);
            submission.setSubmittedAt(LocalDateTime.of(2026, 7, 15, 20, 0));
            var response = new SubmissionResponseDTO(42L, 10L, SubmissionStatus.ACCEPTED, submission.getSubmittedAt());

            authenticate("user-token", userId, "ROLE_USER");
            when(submissionService.findByFilters(null, null, null, userId, false, PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(submission), PageRequest.of(0, 10), 1));
            when(submissionMapper.toSubmissionResponseDTO(submission)).thenReturn(response);

            mockMvc.perform(get("/api/v1/submissions")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].id").value(42))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(submissionService).findByFilters(null, null, null, userId, false, PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should allow admin to delete submission")
        void shouldAllowAdminToDeleteSubmission() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(delete("/api/v1/submissions/42")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isNoContent());

            verify(submissionService).delete(42L);
        }
    }

    @Nested
    @DisplayName("Security")
    class Security {

        @Test
        @DisplayName("Should ignore user field from submission payload")
        void shouldIgnoreUserFieldFromSubmissionPayload() throws Exception {
            UUID userId = UUID.randomUUID();
            var submission = new Submission();
            submission.setAnswer("2*x");
            var createdSubmission = new Submission();
            createdSubmission.setId(42L);
            var response = new SubmissionResponseDTO(42L, 10L, SubmissionStatus.PENDING, null);

            authenticate("user-token", userId, "ROLE_USER");
            when(submissionMapper.toSubmission(any())).thenReturn(submission);
            when(submissionService.create(any(Submission.class), eq(10L), eq(userId))).thenReturn(createdSubmission);
            when(submissionMapper.toSubmissionResponseDTO(createdSubmission)).thenReturn(response);

            mockMvc.perform(post("/api/v1/problems/10/submissions")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "answer": "2*x",
                                      "user": "00000000-0000-0000-0000-000000000000"
                                    }
                                    """))
                    .andExpect(status().isCreated());

            ArgumentCaptor<Submission> submissionCaptor = ArgumentCaptor.forClass(Submission.class);
            verify(submissionService).create(submissionCaptor.capture(), eq(10L), eq(userId));
            assertNull(submissionCaptor.getValue().getUser());
        }

        @Test
        @DisplayName("Should reject another user's submissions list")
        void shouldRejectAnotherUsersSubmissionsList() throws Exception {
            UUID userId = UUID.randomUUID();
            UUID anotherUserId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");
            when(submissionService.findByFilters(anotherUserId, null, null, userId, false, PageRequest.of(0, 10)))
                    .thenThrow(new ForbiddenOperationException("You cannot access another user's submissions"));

            mockMvc.perform(get("/api/v1/submissions")
                            .param("userId", anotherUserId.toString())
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(submissionService).findByFilters(anotherUserId, null, null, userId, false, PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should allow owner to access own submission")
        void shouldAllowOwnerToAccessOwnSubmission() throws Exception {
            UUID userId = UUID.randomUUID();
            var submission = new Submission();
            submission.setId(42L);
            var response = new SubmissionResponseDTO(42L, 10L, SubmissionStatus.ACCEPTED, null);

            authenticate("user-token", userId, "ROLE_USER");
            when(submissionService.findByIdForUser(42L, userId, false)).thenReturn(submission);
            when(submissionMapper.toSubmissionResponseDTO(submission)).thenReturn(response);

            mockMvc.perform(get("/api/v1/submissions/42")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42));

            verify(submissionService).findByIdForUser(42L, userId, false);
        }

        @Test
        @DisplayName("Should reject another user's submission access")
        void shouldRejectAnotherUserSubmissionAccess() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");
            when(submissionService.findByIdForUser(42L, userId, false))
                    .thenThrow(new ForbiddenOperationException("You cannot access this submission"));

            mockMvc.perform(get("/api/v1/submissions/42")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should allow admin to access any submission")
        void shouldAllowAdminToAccessAnySubmission() throws Exception {
            UUID adminId = UUID.randomUUID();
            var submission = new Submission();
            submission.setId(42L);
            var response = new SubmissionResponseDTO(42L, 10L, SubmissionStatus.ACCEPTED, null);

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(submissionService.findByIdForUser(42L, adminId, true)).thenReturn(submission);
            when(submissionMapper.toSubmissionResponseDTO(submission)).thenReturn(response);

            mockMvc.perform(get("/api/v1/submissions/42")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42));

            verify(submissionService).findByIdForUser(42L, adminId, true);
        }

        @Test
        @DisplayName("Should reject submission deletion by regular user")
        void shouldRejectSubmissionDeletionByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(delete("/api/v1/submissions/42")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(submissionService, never()).delete(any());
        }

        @Test
        @DisplayName("Should return unauthorized when access token is missing")
        void shouldReturnUnauthorizedWhenAccessTokenIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/submissions/42"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid access token")
        void shouldReturnUnauthorizedForInvalidAccessToken() throws Exception {
            when(accessTokenService.validate("invalid-token"))
                    .thenThrow(new TokenDecodingException("failed to decode"));

            mockMvc.perform(get("/api/v1/submissions/42")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject submission creation without answer")
        void shouldRejectSubmissionCreationWithoutAnswer() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(post("/api/v1/problems/10/submissions")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.answer").value("Answer is required"));

            verify(submissionService, never()).create(any(), any(), any());
        }

        @Test
        @DisplayName("Should reject submission creation with blank answer")
        void shouldRejectSubmissionCreationWithBlankAnswer() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(post("/api/v1/problems/10/submissions")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "answer": ""
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.answer").value("Answer is required"));

            verify(submissionService, never()).create(any(), any(), any());
        }
    }

    private void authenticate(String token, UUID userId, String role) {
        when(accessTokenService.validate(token)).thenReturn(true);
        when(accessTokenService.getUserId(token)).thenReturn(userId);
        when(accessTokenService.getUserRole(token)).thenReturn(role);
    }
}
