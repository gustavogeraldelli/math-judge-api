package dev.gustavo.math.controller.dto.challenge;

import dev.gustavo.math.entity.enums.ChallengeDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChallengeRequestDTO(
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 64, message = "Title can have up to 64 characters")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Difficulty is required")
        //@Pattern(regexp = "EASY|MEDIUM|HARD", message = "Difficulty must be EASY, MEDIUM or HARD")
        ChallengeDifficulty difficulty
) {
}
