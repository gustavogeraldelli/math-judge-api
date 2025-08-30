package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.challenge.ChallengeRequestDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.entity.Challenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChallengeMapper {

    @Mapping(target = "id", ignore = true)
    Challenge toChallenge(ChallengeRequestDTO challenge);

    ChallengeResponseDTO toChallengeResponseDTO(Challenge challenge);

    Challenge toChallenge(Long id);

}
