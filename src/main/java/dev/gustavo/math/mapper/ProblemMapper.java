package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.problem.ProblemCreateRequestDTO;
import dev.gustavo.math.controller.dto.problem.ProblemResponseDTO;
import dev.gustavo.math.controller.dto.problem.ProblemUpdateRequestDTO;
import dev.gustavo.math.entity.Problem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProblemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    Problem toProblem(ProblemCreateRequestDTO problem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    @Mapping(target = "submissions", ignore = true)
    Problem toProblem(ProblemUpdateRequestDTO problem);

    ProblemResponseDTO toProblemResponseDTO(Problem problem);

    default Problem toProblem(Long id) {
        Problem problem = new Problem();
        problem.setId(id);
        return problem;
    }

}
