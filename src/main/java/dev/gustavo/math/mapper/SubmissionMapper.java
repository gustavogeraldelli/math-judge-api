package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.Submission;
import dev.gustavo.math.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    SubmissionMapper INSTANCE = Mappers.getMapper(SubmissionMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Submission toSubmission(SubmissionRequestDTO submission);

    SubmissionResponseDTO toSubmissionResponseDTO(Submission submission);

    default Challenge challengeFromId(Long challenge) {
        var c = new Challenge();
        c.setId(challenge);
        return c;
    }

    default User userFromId(UUID user) {
        var u = new User();
        u.setId(user);
        return u;
    }

    default Long idFromChallenge(Challenge challenge) {
        return challenge.getId();
    }

    default UUID idFromUser(User user) {
        return user.getId();
    }
}
