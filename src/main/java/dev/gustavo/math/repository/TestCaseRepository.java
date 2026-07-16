package dev.gustavo.math.repository;

import dev.gustavo.math.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    Page<TestCase> findByProblemId(Long problemId, Pageable pageable);
}
