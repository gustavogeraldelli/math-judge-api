package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.problem.ProblemRequestDTO;
import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.entity.Problem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    @Mapping(target = "id", ignore = true)
    Problem toProblem(ProblemRequestDTO problem);

    ProblemResponseDTO toProblemResponseDTO(Problem problem);

    Problem toProblem(Long id);

}
