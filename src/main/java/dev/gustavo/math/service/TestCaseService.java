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

        if (testCase.getVariableValues() != null && !testCase.getVariableValues().isBlank())
            existingTestCase.setVariableValues(testCase.getVariableValues());

        if (testCase.getExpectedAnswer() != null && !testCase.getExpectedAnswer().isBlank())
            existingTestCase.setExpectedAnswer(testCase.getExpectedAnswer());

        return testCaseRepository.save(existingTestCase);
    }

    public void delete(Long id) {
        if (!testCaseRepository.existsById(id))
            throw new EntityNotFoundException("Test Case", id.toString());
        testCaseRepository.deleteById(id);
    }

}
