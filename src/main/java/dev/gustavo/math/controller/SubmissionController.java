package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.ISubmissionController;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController implements ISubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<SubmissionResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        var submissionsPage = submissionService.findAll(PageRequest.of(page, size))
                .map(submissionMapper::toSubmissionResponseDTO);
        return new PageableResponseDTO<>(submissionsPage);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubmissionResponseDTO findById(@PathVariable Long id) {
        var userSubmission = submissionService.findByIdWithUser(id);

        return submissionMapper.toSubmissionResponseDTO(userSubmission);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDTO create(@Valid @RequestBody SubmissionRequestDTO submissionCreateRequest) {
        var submission = submissionService.create(
                submissionMapper.toSubmission(submissionCreateRequest));
        return submissionMapper.toSubmissionResponseDTO(submission);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }

}
