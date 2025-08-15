package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.challenge.ChallengeRequestDTO;
import dev.gustavo.math.controller.dto.challenge.ChallengeResponseDTO;
import dev.gustavo.math.entity.Challenge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ChallengeMapper {

    ChallengeMapper INSTANCE = Mappers.getMapper(ChallengeMapper.class);

    @Mapping(target = "id", ignore = true)
    Challenge toChallenge(ChallengeRequestDTO challenge);

    ChallengeResponseDTO toChallengeResponseDTO(Challenge challenge);

    default Challenge toChallenge(Long id) {
        Challenge challenge = new Challenge();
        challenge.setId(id);
        return challenge;
    }
}
