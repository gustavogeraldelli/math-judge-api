package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.ISubmissionController;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.entity.enums.SubmissionStatus;
import dev.gustavo.math.infra.ratelimit.SubmissionRateLimiter;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubmissionController implements ISubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionMapper submissionMapper;
    private final SubmissionRateLimiter submissionRateLimiter;

    @GetMapping("/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<SubmissionResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer size,
                                                              @RequestParam(required = false) UUID userId,
                                                              @RequestParam(required = false) Long problemId,
                                                              @RequestParam(required = false) SubmissionStatus status,
                                                              Authentication authentication) {
        var submissionsPage = submissionService.findByFilters(
                        userId,
                        problemId,
                        status,
                        (UUID) authentication.getPrincipal(),
                        isAdmin(authentication),
                        PageRequest.of(page, size))
                .map(submissionMapper::toSubmissionResponseDTO);
        return new PageableResponseDTO<>(submissionsPage);
    }

    @GetMapping("/submissions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SubmissionResponseDTO findById(@PathVariable Long id, Authentication authentication) {
        var userSubmission = submissionService.findByIdForUser(
                id,
                (UUID) authentication.getPrincipal(),
                isAdmin(authentication));

        return submissionMapper.toSubmissionResponseDTO(userSubmission);
    }

    @PostMapping("/problems/{problemId}/submissions")
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDTO create(@PathVariable Long problemId,
                                        @Valid @RequestBody SubmissionRequestDTO submissionCreateRequest,
                                        Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        submissionRateLimiter.checkSubmissionAllowed(userId);

        var submission = submissionService.create(
                submissionMapper.toSubmission(submissionCreateRequest),
                problemId,
                userId);
        return submissionMapper.toSubmissionResponseDTO(submission);
    }

    @DeleteMapping("/submissions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
