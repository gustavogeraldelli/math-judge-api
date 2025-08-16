package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.IChallengeController;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeRequestDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeSubmissionsResponseDTO;
import dev.gustavo.math.mapper.ChallengeMapper;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.service.ChallengeService;
import dev.gustavo.math.service.SubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/challenges")
@RequiredArgsConstructor
public class ChallengeController implements IChallengeController {

    private final ChallengeService challengeService;
    private final SubmissionService submissionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<ChallengeResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        var challengesPage = challengeService.findAll(PageRequest.of(page, size))
                .map(ChallengeMapper.INSTANCE::toChallengeResponseDTO);
        return new PageableResponseDTO<>(challengesPage);
    }

    @GetMapping("/{id}")
    public ChallengeResponseDTO findById(@PathVariable Long id) {
        return ChallengeMapper.INSTANCE.toChallengeResponseDTO(challengeService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChallengeResponseDTO create(@Valid @RequestBody ChallengeRequestDTO challengeCreateRequest) {
        var challenge = challengeService.create(ChallengeMapper.INSTANCE.toChallenge(challengeCreateRequest));
        return ChallengeMapper.INSTANCE.toChallengeResponseDTO(challenge);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChallengeResponseDTO update(@PathVariable Long id, @RequestBody ChallengeRequestDTO challengeUpdateRequest) {
        var updatedChallenge = challengeService.update(id,
                ChallengeMapper.INSTANCE.toChallenge(challengeUpdateRequest));
        return ChallengeMapper.INSTANCE.toChallengeResponseDTO(updatedChallenge);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        challengeService.delete(id);
    }

    @GetMapping("/{id}/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<ChallengeSubmissionsResponseDTO> listChallengeSubmissions(@PathVariable Long id,
                                                                                         @RequestParam(defaultValue = "0") Integer page,
                                                                                         @RequestParam(defaultValue = "10") Integer size) {
        var challengeSubmissions = submissionService.listInChallenge(
                        ChallengeMapper.INSTANCE.toChallenge(id),
                        PageRequest.of(page, size))
                .map(SubmissionMapper.INSTANCE::toChallengeSubmissionsResponseDTO);
        return new PageableResponseDTO<>(challengeSubmissions);
    }

}
