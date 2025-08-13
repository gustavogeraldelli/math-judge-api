package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.PageableResponse;
import dev.gustavo.math.controller.dto.challenge.ChallengeRequestDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.mapper.ChallengeMapper;
import dev.gustavo.math.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponse<ChallengeResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "10") Integer size) {
        var challengesPage = challengeService.findAll(PageRequest.of(page, size))
                .map(ChallengeMapper.INSTANCE::toChallengeResponseDTO);
        return new PageableResponse<>(challengesPage);
    }

    @GetMapping("/{id}")
    public Challenge findById(@PathVariable Long id) {
        return challengeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChallengeResponseDTO save(@RequestBody ChallengeRequestDTO challengeCreateRequest) {
        var challenge = challengeService.create(ChallengeMapper.INSTANCE.toChallenge(challengeCreateRequest));
        return ChallengeMapper.INSTANCE.toChallengeResponseDTO(challenge);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChallengeResponseDTO update(@PathVariable Long id, @RequestBody ChallengeRequestDTO challengeUpdateRequest) {
        var updatedChallenge = challengeService.update(id, ChallengeMapper.INSTANCE.toChallenge(challengeUpdateRequest));
        return ChallengeMapper.INSTANCE.toChallengeResponseDTO(updatedChallenge);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        challengeService.delete(id);
    }

}
