package dev.gustavo.math.service;

import dev.gustavo.math.controller.dto.ranking.RankingResponseDTO;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final SubmissionRepository submissionRepository;

    @Cacheable(value = "ranking", key = "{#difficulty == null ? 'ALL' : #difficulty.name(), #pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString()}")
    public Page<RankingResponseDTO> findRanking(ProblemDifficulty difficulty, Pageable pageable) {
        return submissionRepository.findRanking(SubmissionStatus.ACCEPTED, difficulty, pageable);
    }
}
