package dev.gustavo.math.controller.dto.challenge;

import dev.gustavo.math.entity.enums.ChallengeDifficulty;

public record ChallengeResponseDTO(
        Long id,
        String title,
        String description,
        ChallengeDifficulty difficulty
) {
}
