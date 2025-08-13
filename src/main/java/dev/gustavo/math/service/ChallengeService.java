package dev.gustavo.math.service;

import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.repository.ChallengeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public Page<Challenge> findAll(Pageable pageable) {
        return challengeRepository.findAll(pageable);
    }

    public Challenge findById(Long id) {
        return challengeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Challenge with id %s not found", id)));
    }

    public Challenge create(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    public Challenge update(Long id, Challenge challenge) {
        var existingChallenge = findById(id);

        if (challenge.getTitle() != null && !challenge.getTitle().isBlank())
            existingChallenge.setTitle(challenge.getTitle());

        if (challenge.getDescription() != null && !challenge.getDescription().isBlank())
            existingChallenge.setDescription(challenge.getDescription());

        if (challenge.getDifficulty() != null)
            existingChallenge.setDifficulty(challenge.getDifficulty());

        return challengeRepository.save(existingChallenge);
    }

    public void delete(Long id) {
        if (!challengeRepository.existsById(id))
            throw new EntityNotFoundException(String.format("Challenge with id %s not found", id));
        challengeRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return challengeRepository.existsById(id);
    }

}
