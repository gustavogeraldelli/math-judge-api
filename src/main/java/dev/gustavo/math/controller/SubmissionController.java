package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<SubmissionResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        var submissionsPage = submissionService.findAll(PageRequest.of(page, size))
                .map(SubmissionMapper.INSTANCE::toSubmissionResponseDTO);
        return new PageableResponseDTO<>(submissionsPage);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubmissionResponseDTO findById(@PathVariable Long id, Authentication auth) {
        var userSubmission = submissionService.findByIdWithUser(id);

        return SubmissionMapper.INSTANCE.toSubmissionResponseDTO(userSubmission);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDTO create(@Valid @RequestBody SubmissionRequestDTO submissionCreateRequest) {
        var submission = submissionService.create(
                SubmissionMapper.INSTANCE.toSubmission(submissionCreateRequest));
        return SubmissionMapper.INSTANCE.toSubmissionResponseDTO(submission);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }

}
