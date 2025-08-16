package dev.gustavo.math.repository;

import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s JOIN FETCH s.user WHERE s.challenge.id = :id")
    Page<Submission> findByChallengeIdWithUser(Long id, Pageable pageable);

    @Query("SELECT s FROM Submission s JOIN FETCH s.challenge WHERE s.user.id = :id")
    Page<Submission> findByUserIdWithChallenge(UUID id, Pageable pageable);

    Page<Submission> findByUserAndChallenge(User user, Challenge challenge, Pageable pageable);

    @Query("SELECT s FROM Submission s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Submission> findByIdWithUser(Long id);
}
