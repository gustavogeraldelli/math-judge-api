package dev.gustavo.math.controller.dto.challenge;

import dev.gustavo.math.entity.enums.ChallengeDifficulty;

public record ChallengeRequestDTO(
        String title,
        String description,
        ChallengeDifficulty difficulty
) {
}
