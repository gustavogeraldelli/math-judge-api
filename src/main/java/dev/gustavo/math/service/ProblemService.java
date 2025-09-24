package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    public Page<Problem> findAll(Pageable pageable) {
        return problemRepository.findAll(pageable);
    }

    public Problem findById(Long id) {
        return problemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Problem", id.toString()));
    }

    public Problem create(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem update(Long id, Problem problem) {
        var existingProblem = findById(id);

        if (problem.getTitle() != null && !problem.getTitle().isBlank())
            existingProblem.setTitle(problem.getTitle());

        if (problem.getDescription() != null && !problem.getDescription().isBlank())
            existingProblem.setDescription(problem.getDescription());

        if (problem.getDifficulty() != null)
            existingProblem.setDifficulty(problem.getDifficulty());

        return problemRepository.save(existingProblem);
    }

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

}
