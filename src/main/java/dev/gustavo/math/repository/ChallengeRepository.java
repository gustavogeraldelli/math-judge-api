package dev.gustavo.math.repository;

import dev.gustavo.math.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT c FROM Challenge c LEFT JOIN FETCH c.testCases WHERE c.id = :id")
    Optional<Challenge> findByIdWithTestCases(Long id);

}
