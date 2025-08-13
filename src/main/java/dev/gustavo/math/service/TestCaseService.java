package dev.gustavo.math.service;

import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.exception.InvalidForeignKeyException;
import dev.gustavo.math.repository.TestCaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ChallengeService challengeService;

    public Page<TestCase> findAll(Pageable pageable) {
        return testCaseRepository.findAll(pageable);
    }

    public TestCase findById(Long id) {
        return testCaseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Submission with id %s not found", id)));
    }

    public TestCase create(TestCase testCase) {
        if (!challengeService.existsById(testCase.getChallenge().getId()))
            throw new InvalidForeignKeyException("challenge", testCase.getChallenge().getId().toString());
        return testCaseRepository.save(testCase);
    }

    public TestCase update(Long id, TestCase testCase) {
        var existingTestCase = findById(id);

        if (testCase.getChallenge() != null) {
            if (!challengeService.existsById(testCase.getChallenge().getId()))
                throw new InvalidForeignKeyException("challenge", testCase.getChallenge().getId().toString());
            existingTestCase.setChallenge(testCase.getChallenge());
        }

        if (testCase.getInput() != null && !testCase.getInput().isBlank())
            existingTestCase.setInput(testCase.getInput());

        if (testCase.getExpectedOutput() != null && !testCase.getExpectedOutput().isBlank())
            existingTestCase.setExpectedOutput(testCase.getExpectedOutput());

        return testCaseRepository.save(existingTestCase);
    }

    public void delete(Long id) {
        if (!testCaseRepository.existsById(id))
            throw new EntityNotFoundException(String.format("Test case with id %s not found", id));
        testCaseRepository.deleteById(id);
    }

}
