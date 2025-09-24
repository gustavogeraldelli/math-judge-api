package dev.gustavo.math.service;

import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProblemService problemService;

    public TestCase findById(Long id) {
        return testCaseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Test Case", id.toString()));
    }

    public TestCase create(TestCase testCase) {
        problemService.existsById(testCase.getProblem().getId());
        return testCaseRepository.save(testCase);
    }

    public TestCase update(Long id, TestCase testCase) {
        var existingTestCase = findById(id);

        if (testCase.getProblem() != null) {
            problemService.existsById(testCase.getProblem().getId());
            existingTestCase.setProblem(testCase.getProblem());
        }

        if (testCase.getInput() != null && !testCase.getInput().isBlank())
            existingTestCase.setInput(testCase.getInput());

        if (testCase.getExpectedOutput() != null && !testCase.getExpectedOutput().isBlank())
            existingTestCase.setExpectedOutput(testCase.getExpectedOutput());

        return testCaseRepository.save(existingTestCase);
    }

    public void delete(Long id) {
        if (!testCaseRepository.existsById(id))
            throw new EntityNotFoundException("Test Case", id.toString());
        testCaseRepository.deleteById(id);
    }

}
