package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.exception.InvalidProblemVariablesException;
import dev.gustavo.math.repository.ProblemRepository;
import dev.gustavo.math.util.ProblemVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    @Cacheable(value = "problems", key = "{#pageable.pageNumber, #pageable.pageSize, #pageable.sort.toString()}")
    public Page<Problem> findAll(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }

    @Cacheable(value = "problemById", key = "#id")
    public Problem findById(Long id) {
        return findProblemById(id);
    }

    private Problem findProblemById(Long id) {
        return problemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Problem", id.toString()));
    }

    @CacheEvict(value = {"problems", "problemById", "ranking"}, allEntries = true)
    public Problem create(Problem problem) {
        validateVariables(problem);
        return problemRepository.save(problem);
    }

    @CacheEvict(value = {"problems", "problemById", "ranking"}, allEntries = true)
    public Problem update(Long id, Problem problem) {
        var existingProblem = findProblemById(id);

        if (problem.getTitle() != null && !problem.getTitle().isBlank())
            existingProblem.setTitle(problem.getTitle());

        if (problem.getDescription() != null && !problem.getDescription().isBlank())
            existingProblem.setDescription(problem.getDescription());

        if (problem.getDifficulty() != null)
            existingProblem.setDifficulty(problem.getDifficulty());

        if (problem.getType() != null)
            existingProblem.setType(problem.getType());

        if (problem.getVariables() != null)
            existingProblem.setVariables(problem.getVariables());

        validateVariables(existingProblem);
        return problemRepository.save(existingProblem);
    }

    @CacheEvict(value = {"problems", "problemById", "ranking"}, allEntries = true)
    public void delete(Long id) {
        if (!problemRepository.existsById(id))
            throw new EntityNotFoundException("Problem", id.toString());
        problemRepository.deleteById(id);
    }

    public Problem findByIdWithTestCases(Long id) {
        return problemRepository.findByIdWithTestCases(id).orElseThrow(
                () -> new EntityNotFoundException("Problem", id.toString())
        );
    }

    public void existsById(Long id) {
        if (!problemRepository.existsById(id))
            throw new EntityNotFoundException("Problem", id.toString());
    }

    private void validateVariables(Problem problem) {
        var variables = parseVariables(problem);

        if (problem.getType() == ProblemType.EXPRESSION && variables.isEmpty())
            throw new InvalidProblemVariablesException("Expression problems must declare at least one variable");

        if (problem.getType() == ProblemType.NUMERIC && !variables.isEmpty())
            throw new InvalidProblemVariablesException("Numeric problems cannot declare variables");

        if (ProblemVariables.hasDuplicates(variables))
            throw new InvalidProblemVariablesException("Problem variables cannot contain duplicates");

        boolean hasInvalidVariable = variables.stream().anyMatch(variable -> !ProblemVariables.isValidName(variable));
        if (hasInvalidVariable)
            throw new InvalidProblemVariablesException("Problem variables must match [a-zA-Z][a-zA-Z0-9_]*");
    }

    private List<String> parseVariables(Problem problem) {
        try {
            return ProblemVariables.fromJson(problem.getVariables());
        }
        catch (IllegalArgumentException e) {
            throw new InvalidProblemVariablesException("Problem variables must be a valid JSON array");
        }
    }

}
