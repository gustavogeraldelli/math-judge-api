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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public SubmissionResponseDTO findById(@PathVariable Long id, Authentication authentication) {
        var userSubmission = submissionService.findByIdForUser(
                id,
                currentUserId(authentication),
                isAdmin(authentication));

        return submissionMapper.toSubmissionResponseDTO(userSubmission);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDTO create(@Valid @RequestBody SubmissionRequestDTO submissionCreateRequest,
                                        Authentication authentication) {
        var submission = submissionService.create(
                submissionMapper.toSubmission(submissionCreateRequest),
                currentUserId(authentication));
        return submissionMapper.toSubmissionResponseDTO(submission);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }

    private UUID currentUserId(Authentication authentication) {
        return (UUID) authentication.getPrincipal();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
