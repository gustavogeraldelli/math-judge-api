package dev.gustavo.math.repository;

import dev.gustavo.math.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    @Query("SELECT c FROM Problem c LEFT JOIN FETCH c.testCases WHERE c.id = :id")
    Optional<Problem> findByIdWithTestCases(Long id);

}
