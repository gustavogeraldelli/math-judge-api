package dev.gustavo.math.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.gustavo.math.controller.dto.testcase.TestCaseCreateRequestDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseResponseDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseUpdateRequestDTO;
import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.infra.security.SecurityConfig;
import dev.gustavo.math.infra.security.SecurityFilter;
import dev.gustavo.math.mapper.TestCaseMapper;
import dev.gustavo.math.service.TestCaseService;
import dev.gustavo.math.service.auth.AccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TestCaseController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class TestCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private TestCaseService testCaseService;

    @MockitoBean
    private TestCaseMapper testCaseMapper;

    @Nested
    @DisplayName("Endpoints")
    class Endpoints {

        @Test
        @DisplayName("Should allow test case creation by admin")
        void shouldAllowTestCaseCreationByAdmin() throws Exception {
            UUID adminId = UUID.randomUUID();
            var request = new TestCaseCreateRequestDTO(10L, "{\"x\":2}", "4");
            var testCase = testCase(10L, "{\"x\":2}", "4");
            var createdTestCase = testCase(10L, "{\"x\":2}", "4");
            createdTestCase.setId(42L);
            var response = new TestCaseResponseDTO(42L, 10L, "{\"x\":2}", "4");

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(testCaseMapper.toTestCase(any(TestCaseCreateRequestDTO.class))).thenReturn(testCase);
            when(testCaseService.create(testCase)).thenReturn(createdTestCase);
            when(testCaseMapper.toTestCaseResponseDTO(createdTestCase)).thenReturn(response);

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.problem").value(10))
                    .andExpect(jsonPath("$.variableValues").value("{\"x\":2}"))
                    .andExpect(jsonPath("$.expectedAnswer").value("4"));

            verify(testCaseService).create(testCase);
        }

        @Test
        @DisplayName("Should allow test case update by admin")
        void shouldAllowTestCaseUpdateByAdmin() throws Exception {
            UUID adminId = UUID.randomUUID();
            var request = new TestCaseUpdateRequestDTO(10L, "{\"x\":3}", "6");
            var testCase = testCase(10L, "{\"x\":3}", "6");
            var updatedTestCase = testCase(10L, "{\"x\":3}", "6");
            updatedTestCase.setId(42L);
            var response = new TestCaseResponseDTO(42L, 10L, "{\"x\":3}", "6");

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(testCaseMapper.toTestCase(any(TestCaseUpdateRequestDTO.class))).thenReturn(testCase);
            when(testCaseService.update(42L, testCase)).thenReturn(updatedTestCase);
            when(testCaseMapper.toTestCaseResponseDTO(updatedTestCase)).thenReturn(response);

            mockMvc.perform(put("/api/v1/testcases/42")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(42))
                    .andExpect(jsonPath("$.problem").value(10))
                    .andExpect(jsonPath("$.variableValues").value("{\"x\":3}"))
                    .andExpect(jsonPath("$.expectedAnswer").value("6"));

            verify(testCaseService).update(42L, testCase);
        }

        @Test
        @DisplayName("Should allow test case deletion by admin")
        void shouldAllowTestCaseDeletionByAdmin() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(delete("/api/v1/testcases/42")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isNoContent());

            verify(testCaseService).delete(42L);
        }
    }

    @Nested
    @DisplayName("Security")
    class Security {

        @Test
        @DisplayName("Should reject test case creation by regular user")
        void shouldRejectTestCaseCreationByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new TestCaseCreateRequestDTO(10L, "{\"x\":2}", "4");

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(testCaseService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject test case update by regular user")
        void shouldRejectTestCaseUpdateByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var request = new TestCaseUpdateRequestDTO(10L, "{\"x\":3}", "6");

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(put("/api/v1/testcases/42")
                            .header("Authorization", "Bearer user-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());

            verify(testCaseService, never()).update(any(), any());
        }

        @Test
        @DisplayName("Should reject test case deletion by regular user")
        void shouldRejectTestCaseDeletionByRegularUser() throws Exception {
            UUID userId = UUID.randomUUID();

            authenticate("user-token", userId, "ROLE_USER");

            mockMvc.perform(delete("/api/v1/testcases/42")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isForbidden());

            verify(testCaseService, never()).delete(any());
        }

        @Test
        @DisplayName("Should return unauthorized when access token is missing")
        void shouldReturnUnauthorizedWhenAccessTokenIsMissing() throws Exception {
            mockMvc.perform(post("/api/v1/testcases")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "problem": 10,
                                      "variableValues": "{\\"x\\":2}",
                                      "expectedAnswer": "4"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid access token")
        void shouldReturnUnauthorizedForInvalidAccessToken() throws Exception {
            when(accessTokenService.validate("invalid-token"))
                    .thenThrow(new TokenDecodingException("failed to decode"));

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer invalid-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "problem": 10,
                                      "variableValues": "{\\"x\\":2}",
                                      "expectedAnswer": "4"
                                    }
                                    """))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("Should reject test case creation without problem")
        void shouldRejectTestCaseCreationWithoutProblem() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "variableValues": "{\\"x\\":2}",
                                      "expectedAnswer": "4"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.problem").value("Problem id is required"));

            verify(testCaseService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject test case creation without variable values")
        void shouldRejectTestCaseCreationWithoutVariableValues() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "problem": 10,
                                      "expectedAnswer": "4"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.variableValues").value("Variable values cannot be null"));

            verify(testCaseService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject test case creation without expected answer")
        void shouldRejectTestCaseCreationWithoutExpectedAnswer() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "problem": 10,
                                      "variableValues": "{\\"x\\":2}"
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.expectedAnswer").value("Expected output is required"));

            verify(testCaseService, never()).create(any());
        }

        @Test
        @DisplayName("Should reject test case creation with blank expected answer")
        void shouldRejectTestCaseCreationWithBlankExpectedAnswer() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");

            mockMvc.perform(post("/api/v1/testcases")
                            .header("Authorization", "Bearer admin-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "problem": 10,
                                      "variableValues": "{\\"x\\":2}",
                                      "expectedAnswer": ""
                                    }
                                    """))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.expectedAnswer").value("Expected output is required"));

            verify(testCaseService, never()).create(any());
        }
    }

    private TestCase testCase(Long problemId, String variableValues, String expectedAnswer) {
        var problem = new Problem();
        problem.setId(problemId);
        var testCase = new TestCase();
        testCase.setProblem(problem);
        testCase.setVariableValues(variableValues);
        testCase.setExpectedAnswer(expectedAnswer);
        return testCase;
    }

    private void authenticate(String token, UUID userId, String role) {
        when(accessTokenService.validate(token)).thenReturn(true);
        when(accessTokenService.getUserId(token)).thenReturn(userId);
        when(accessTokenService.getUserRole(token)).thenReturn(role);
    }
}
