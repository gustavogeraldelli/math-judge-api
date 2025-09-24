package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.problem.ProblemSubmissionsResponseDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionRequestDTO;
import dev.gustavo.math.controller.dto.submission.SubmissionResponseDTO;
import dev.gustavo.math.controller.dto.user.UserSubmissionsResponseDTO;
import dev.gustavo.math.entity.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "problem.id", source = "problem")
    @Mapping(target = "user.id", source = "user")
    Submission toSubmission(SubmissionRequestDTO submission);

    @Mapping(target = "problem", source = "problem.id")
    SubmissionResponseDTO toSubmissionResponseDTO(Submission submission);

    UserSubmissionsResponseDTO toUserSubmissionsResponseDTO(Submission submission);

    ProblemSubmissionsResponseDTO toProblemSubmissionsResponseDTO(Submission submission);

}
