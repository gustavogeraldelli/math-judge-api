package dev.gustavo.math.service;

import dev.gustavo.math.controller.dto.ranking.RankingResponseDTO;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.repository.SubmissionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @InjectMocks
    private RankingService rankingService;

    @Test
    @DisplayName("Should find ranking without difficulty filter")
    void findRankingShouldReturnRankingWithoutDifficultyFilter() {
        var pageable = PageRequest.of(0, 10);
        Page<RankingResponseDTO> rankingPage = new PageImpl<>(
                List.of(new RankingResponseDTO(UUID.randomUUID(), "john", "Johnny", 3L)),
                pageable,
                1);
        when(submissionRepository.findRanking(SubmissionStatus.ACCEPTED, null, pageable)).thenReturn(rankingPage);

        var ranking = rankingService.findRanking(null, pageable);

        assertFalse(ranking.isEmpty());
        assertEquals(3L, ranking.getContent().getFirst().resolvedProblems());
        verify(submissionRepository, times(1)).findRanking(SubmissionStatus.ACCEPTED, null, pageable);
    }

    @Test
    @DisplayName("Should find ranking filtered by difficulty")
    void findRankingShouldReturnRankingFilteredByDifficulty() {
        var pageable = PageRequest.of(0, 10);
        Page<RankingResponseDTO> rankingPage = new PageImpl<>(
                List.of(new RankingResponseDTO(UUID.randomUUID(), "mary", "Mary", 2L)),
                pageable,
                1);
        when(submissionRepository.findRanking(SubmissionStatus.ACCEPTED, ProblemDifficulty.HARD, pageable)).thenReturn(rankingPage);

        var ranking = rankingService.findRanking(ProblemDifficulty.HARD, pageable);

        assertFalse(ranking.isEmpty());
        assertEquals(2L, ranking.getContent().getFirst().resolvedProblems());
        verify(submissionRepository, times(1)).findRanking(SubmissionStatus.ACCEPTED, ProblemDifficulty.HARD, pageable);
    }
}
