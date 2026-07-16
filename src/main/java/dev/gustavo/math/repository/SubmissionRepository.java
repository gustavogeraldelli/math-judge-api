package dev.gustavo.math.repository;

import dev.gustavo.math.controller.dto.ranking.RankingResponseDTO;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query(value = """
            SELECT s FROM Submission s
            JOIN FETCH s.problem
            WHERE (:userId IS NULL OR s.user.id = :userId)
              AND (:problemId IS NULL OR s.problem.id = :problemId)
              AND (:status IS NULL OR s.status = :status)
            """,
            countQuery = """
            SELECT COUNT(s) FROM Submission s
            WHERE (:userId IS NULL OR s.user.id = :userId)
              AND (:problemId IS NULL OR s.problem.id = :problemId)
              AND (:status IS NULL OR s.status = :status)
            """)
    Page<Submission> findByFilters(UUID userId, Long problemId, SubmissionStatus status, Pageable pageable);

    @Query("SELECT s FROM Submission s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Submission> findByIdWithUser(Long id);

    @Query("SELECT DISTINCT s FROM Submission s JOIN FETCH s.problem p LEFT JOIN FETCH p.testCases WHERE s.id = :id")
    Optional<Submission> findByIdWithProblemAndTestCases(Long id);

    @Query(value = """
            SELECT new dev.gustavo.math.controller.dto.ranking.RankingResponseDTO(
                u.id,
                u.username,
                u.nickname,
                COUNT(DISTINCT p.id)
            )
            FROM Submission s
            JOIN s.user u
            JOIN s.problem p
            WHERE s.status = :status
              AND (:difficulty IS NULL OR p.difficulty = :difficulty)
            GROUP BY u.id, u.username, u.nickname
            ORDER BY COUNT(DISTINCT p.id) DESC, u.username ASC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT u.id)
            FROM Submission s
            JOIN s.user u
            JOIN s.problem p
            WHERE s.status = :status
              AND (:difficulty IS NULL OR p.difficulty = :difficulty)
            """)
    Page<RankingResponseDTO> findRanking(SubmissionStatus status, ProblemDifficulty difficulty, Pageable pageable);
}
