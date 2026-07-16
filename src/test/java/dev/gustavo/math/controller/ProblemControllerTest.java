package dev.gustavo.math.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gustavo.math.controller.dto.problem.ProblemCreateRequestDTO;
import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.controller.dto.problem.ProblemUpdateRequestDTO;
import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.infra.security.SecurityConfig;
import dev.gustavo.math.infra.security.SecurityFilter;
import dev.gustavo.math.mapper.ProblemMapper;
import dev.gustavo.math.service.ProblemService;
import dev.gustavo.math.service.auth.AccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProblemController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class ProblemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private ProblemService problemService;

    @MockitoBean
    private ProblemMapper problemMapper;

    @Nested
    @DisplayName("Endpoints")
    class Endpoints {

        @Test
        @DisplayName("Should list problems for authenticated user")
        void shouldListProblemsForAuthenticatedUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var problem = new Problem();
            problem.setId(10L);
            problem.setTitle("Equation");
            problem.setDescription("Solve 2x = 4");
            problem.setDifficulty(ProblemDifficulty.EASY);
            problem.setType(ProblemType.NUMERIC);
            var response = new ProblemResponseDTO(10L, "Equation", "Solve 2x = 4", ProblemDifficulty.EASY, ProblemType.NUMERIC);

            authenticate("user-token", userId, "ROLE_USER");
            when(problemService.findAll(PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(problem), PageRequest.of(0, 10), 1));
            when(problemMapper.toProblemResponseDTO(problem)).thenReturn(response);

            mockMvc.perform(get("/api/v1/problems")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].id").value(10))
                    .andExpect(jsonPath("$.items[0].type").value("NUMERIC"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(problemService).findAll(PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should find problem by id for authenticated user")
        void shouldFindProblemByIdForAuthenticatedUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var problem = new Problem();
            problem.setId(10L);
            problem.setTitle("Equation");
            problem.setDescription("Solve 2x = 4");
            problem.setDifficulty(ProblemDifficulty.EASY);
            problem.setType(ProblemType.NUMERIC);
            var response = new ProblemResponseDTO(10L, "Equation", "Solve 2x = 4", ProblemDifficulty.EASY, ProblemType.NUMERIC);

            authenticate("user-token", userId, "ROLE_USER");
            when(problemService.findById(10L)).thenReturn(problem);
            when(problemMapper.toProblemResponseDTO(problem)).thenReturn(response);

            mockMvc.perform(get("/api/v1/problems/10")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.type").value("NUMERIC"));

            verify(problemService).findById(10L);
        }

        @Test
        @DisplayName("Should allow problem creation by admin")
        void shouldAllowProblemCreationByAdmin() throws Exception {
            UUID adminId = UUID.randomUUID();
            var request = new ProblemCreateRequestDTO(
                    "Equation",
                    "Solve 2x = 4",
                    ProblemDifficulty.EASY,
                    ProblemType.NUMERIC);
            var problem = new Problem();
            problem.setTitle("Equation");
            problem.setDescription("Solve 2x = 4");
            problem.setDifficulty(ProblemDifficulty.EASY);
            problem.setType(ProblemType.NUMERIC);
            var createdProblem = new Problem();
            createdProblem.setId(10L);
            createdProblem.setTitle("Equation");
            createdProblem.setDescription("Solve 2x = 4");
            createdProblem.setDifficulty(ProblemDifficulty.EASY);
            createdProblem.setType(ProblemType.NUMERIC);
            var response = new ProblemResponseDTO(10L, "Equation", "Solve 2x = 4", ProblemDifficulty.EASY, ProblemType.NUMERIC);

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(problemMapper.toProblem(any(ProblemCreateRequestDTO.class))).thenReturn(problem);
            when(problemService.create(problem)).thenReturn(createdProblem);
            when(problemMapper.toProblemResponseDTO(createdProblem)).thenReturn(response);

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.type").value("NUMERIC"));

            verify(problemService).create(problem);
        }

        @Test
        @DisplayName("Should allow problem update by admin")
        void shouldAllowProblemUpdateByAdmin() throws Exception {
            UUID adminId = UUID.randomUUID();
            var request = new ProblemUpdateRequestDTO(
                    "Updated equation",
                    "Solve 3x = 9",
                    ProblemDifficulty.MEDIUM,
                    ProblemType.NUMERIC);
            var problem = new Problem();
            problem.setTitle("Updated equation");
            problem.setDescription("Solve 3x = 9");
            problem.setDifficulty(ProblemDifficulty.MEDIUM);
            problem.setType(ProblemType.NUMERIC);
            var updatedProblem = new Problem();
            updatedProblem.setId(10L);
            updatedProblem.setTitle("Updated equation");
            updatedProblem.setDescription("Solve 3x = 9");
            updatedProblem.setDifficulty(ProblemDifficulty.MEDIUM);
            updatedProblem.setType(ProblemType.NUMERIC);
            var response = new ProblemResponseDTO(10L, "Updated equation", "Solve 3x = 9", ProblemDifficulty.MEDIUM, ProblemType.NUMERIC);

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(problemMapper.toProblem(any(ProblemUpdateRequestDTO.class))).thenReturn(problem);
            when(problemService.update(10L, problem)).thenReturn(updatedProblem);
            when(problemMapper.toProblemResponseDTO(updatedProblem)).thenReturn(response);

            mockMvc.perform(put("/api/v1/problems/10")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(10))
                    .andExpect(jsonPath("$.difficulty").value("MEDIUM"));

            verify(problemService).update(10L, problem);
        }

        @Test
        @DisplayName("Should allow problem deletion by admin")
        void shouldAllowProblemDeletionByAdmin() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(delete("/api/v1/problems/10")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isNoContent());

            verify(problemService).delete(10L);
        }

    }

    @Nested
    @DisplayName("Security")
    class Security {

        @Test
        @DisplayName("Should reject problem creation by regular user")
        void shouldRejectProblemCreationByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new ProblemCreateRequestDTO(
                    "Equation",
                    "Solve 2x = 4",
                    ProblemDifficulty.EASY,
                    ProblemType.NUMERIC);

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(problemService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject problem update by regular user")
        void shouldRejectProblemUpdateByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new ProblemUpdateRequestDTO(
                    "Updated equation",
                    "Solve 3x = 9",
                    ProblemDifficulty.MEDIUM,
                    ProblemType.NUMERIC);

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(put("/api/v1/problems/10")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(problemService, never()).update(any(), any());
        }

        @Test
        @DisplayName("Should reject problem deletion by regular user")
        void shouldRejectProblemDeletionByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(delete("/api/v1/problems/10")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(problemService, never()).delete(any());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid access token")
        void shouldReturnUnauthorizedForInvalidAccessToken() throws Exception {
            when(accessTokenService.validate("invalid-token"))
                    .thenThrow(new TokenDecodingException("failed to decode"));

            mockMvc.perform(get("/api/v1/problems")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized when access token is missing")
        void shouldReturnUnauthorizedWhenAccessTokenIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/problems"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject problem creation without title")
        void shouldRejectProblemCreationWithoutTitle() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "description": "Solve 2x = 4",
                                      "difficulty": "EASY",
                                      "type": "NUMERIC"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").value("Title is required"));

            verify(problemService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject problem creation without description")
        void shouldRejectProblemCreationWithoutDescription() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "Equation",
                                      "difficulty": "EASY",
                                      "type": "NUMERIC"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.description").value("Description is required"));

            verify(problemService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject problem creation without difficulty")
        void shouldRejectProblemCreationWithoutDifficulty() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "Equation",
                                      "description": "Solve 2x = 4",
                                      "type": "NUMERIC"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.difficulty").value("Difficulty is required"));

            verify(problemService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject problem creation without type")
        void shouldRejectProblemCreationWithoutType() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "Equation",
                                      "description": "Solve 2x = 4",
                                      "difficulty": "EASY"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.type").value("Type is required"));

            verify(problemService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject problem creation with title longer than 64 characters")
        void shouldRejectProblemCreationWithTitleLongerThan64Characters() throws Exception {
            UUID adminId = UUID.randomUUID();
            var request = new ProblemCreateRequestDTO(
                    "a".repeat(65),
                    "Solve 2x = 4",
                    ProblemDifficulty.EASY,
                    ProblemType.NUMERIC);

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/problems")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").value("Title can have up to 64 characters"));

            verify(problemService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject problem update with empty title")
        void shouldRejectProblemUpdateWithEmptyTitle() throws Exception {
            UUID adminId = UUID.randomUUID();
            var request = new ProblemUpdateRequestDTO(
                    "",
                    "Solve 3x = 9",
                    ProblemDifficulty.MEDIUM,
                    ProblemType.NUMERIC);

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(put("/api/v1/problems/10")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.title").value("Title can have up to 64 characters"));

            verify(problemService, never()).update(any(), any());
        }
    }

    private void authenticate(String token, UUID userId, String role) {
        when(accessTokenService.validate(token)).thenReturn(true);
        when(accessTokenService.getUserId(token)).thenReturn(userId);
        when(accessTokenService.getUserRole(token)).thenReturn(role);
    }
}
