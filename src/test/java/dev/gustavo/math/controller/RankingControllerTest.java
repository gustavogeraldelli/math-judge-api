package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.ranking.RankingResponseDTO;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.exception.TokenDecodingException;
import dev.gustavo.math.infra.security.SecurityConfig;
import dev.gustavo.math.infra.security.SecurityFilter;
import dev.gustavo.math.service.RankingService;
import dev.gustavo.math.service.auth.AccessTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RankingController.class)
@Import({SecurityConfig.class, SecurityFilter.class})
class RankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccessTokenService accessTokenService;

    @MockitoBean
    private RankingService rankingService;

    @Nested
    @DisplayName("Endpoints")
    class Endpoints {

        @Test
        @DisplayName("Should list ranking for authenticated user")
        void shouldListRankingForAuthenticatedUser() throws Exception {
            UUID userId = UUID.randomUUID();
            var ranking = new RankingResponseDTO(UUID.randomUUID(), "john", "Johnny", 3L);

            authenticate("user-token", userId, "ROLE_USER");
            when(rankingService.findRanking(null, PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(ranking), PageRequest.of(0, 10), 1));

            mockMvc.perform(get("/api/v1/ranking")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].username").value("john"))
                    .andExpect(jsonPath("$.items[0].nickname").value("Johnny"))
                    .andExpect(jsonPath("$.items[0].resolvedProblems").value(3))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(rankingService).findRanking(null, PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should list ranking filtered by difficulty")
        void shouldListRankingFilteredByDifficulty() throws Exception {
            UUID userId = UUID.randomUUID();
            var ranking = new RankingResponseDTO(UUID.randomUUID(), "mary", "Mary", 2L);

            authenticate("user-token", userId, "ROLE_USER");
            when(rankingService.findRanking(ProblemDifficulty.HARD, PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(ranking), PageRequest.of(0, 10), 1));

            mockMvc.perform(get("/api/v1/ranking")
                            .param("difficulty", "HARD")
                            .header("Authorization", "Bearer user-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.items[0].username").value("mary"))
                    .andExpect(jsonPath("$.items[0].resolvedProblems").value(2));

            verify(rankingService).findRanking(ProblemDifficulty.HARD, PageRequest.of(0, 10));
        }
    }

    @Nested
    @DisplayName("Security")
    class Security {

        @Test
        @DisplayName("Should allow admin to list ranking")
        void shouldAllowAdminToListRanking() throws Exception {
            UUID adminId = UUID.randomUUID();

            authenticate("admin-token", adminId, "ROLE_ADMIN");
            when(rankingService.findRanking(null, PageRequest.of(0, 10)))
                    .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

            mockMvc.perform(get("/api/v1/ranking")
                            .header("Authorization", "Bearer admin-token"))
                    .andExpect(status().isOk());

            verify(rankingService).findRanking(null, PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("Should return unauthorized when access token is missing")
        void shouldReturnUnauthorizedWhenAccessTokenIsMissing() throws Exception {
            mockMvc.perform(get("/api/v1/ranking"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return unauthorized for invalid access token")
        void shouldReturnUnauthorizedForInvalidAccessToken() throws Exception {
            when(accessTokenService.validate("invalid-token"))
                    .thenThrow(new TokenDecodingException("failed to decode"));

            mockMvc.perform(get("/api/v1/ranking")
                            .header("Authorization", "Bearer invalid-token"))
                    .andExpect(status().isUnauthorized());
        }
    }

    private void authenticate(String token, UUID userId, String role) {
        when(accessTokenService.validate(token)).thenReturn(true);
        when(accessTokenService.getUserId(token)).thenReturn(userId);
        when(accessTokenService.getUserRole(token)).thenReturn(role);
    }
}
