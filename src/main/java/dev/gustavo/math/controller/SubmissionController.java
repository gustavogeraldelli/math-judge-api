package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public SubmissionResponseDTO findById(@PathVariable Long id) {
        return SubmissionMapper.INSTANCE.toSubmissionResponseDTO(submissionService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponseDTO save(@RequestBody SubmissionRequestDTO submissionCreateRequest) {
        var submission = submissionService.create(
                SubmissionMapper.INSTANCE.toSubmission(submissionCreateRequest));
        return SubmissionMapper.INSTANCE.toSubmissionResponseDTO(submission);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }

    @GetMapping("/{userId}/{challengeId}")
    @ResponseStatus(HttpStatus.OK)
    public List<SubmissionResponseDTO> listUserSubmissionsOnChallenge(@PathVariable UUID userId,
                                                                      @PathVariable Long challengeId) {
        var submissions = submissionService.listFromUserInChallenge(
                SubmissionMapper.INSTANCE.userFromId(userId),
                SubmissionMapper.INSTANCE.challengeFromId(challengeId));
        return submissions.stream()
                .map(SubmissionMapper.INSTANCE::toSubmissionResponseDTO).toList();
    }

}
