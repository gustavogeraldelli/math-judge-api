package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.problem.ProblemRequestDTO;
import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.controller.dto.problem.ProblemSubmissionsResponseDTO;
import dev.gustavo.math.mapper.ProblemMapper;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.service.ProblemService;
import dev.gustavo.math.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController implements dev.gustavo.math.controller.doc.ProblemController {

    private final ProblemService problemService;
    private final SubmissionService submissionService;
    private final ProblemMapper problemMapper;
    private final SubmissionMapper submissionMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<ProblemResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        var problemsPage = problemService.findAll(PageRequest.of(page, size))
                .map(problemMapper::toProblemResponseDTO);
        return new PageableResponseDTO<>(problemsPage);
    }

    @GetMapping("/{id}")
    public ProblemResponseDTO findById(@PathVariable Long id) {
        return problemMapper.toProblemResponseDTO(problemService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProblemResponseDTO create(@Valid @RequestBody ProblemRequestDTO problemCreateRequest) {
        var problem = problemService.create(problemMapper.toProblem(problemCreateRequest));
        return problemMapper.toProblemResponseDTO(problem);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProblemResponseDTO update(@PathVariable Long id, @RequestBody ProblemRequestDTO problemUpdateRequest) {
        var updatedProblem = problemService.update(id,
                problemMapper.toProblem(problemUpdateRequest));
        return problemMapper.toProblemResponseDTO(updatedProblem);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        problemService.delete(id);
    }

    @GetMapping("/{id}/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<ProblemSubmissionsResponseDTO> listProblemSubmissions(@PathVariable Long id,
                                                                                       @RequestParam(defaultValue = "0") Integer page,
                                                                                       @RequestParam(defaultValue = "10") Integer size) {
        var problemSubmissions = submissionService.listInProblem(
                        problemMapper.toProblem(id),
                        PageRequest.of(page, size))
                .map(submissionMapper::toProblemSubmissionsResponseDTO);
        return new PageableResponseDTO<>(problemSubmissions);
    }

}
