package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.testcase.TestCaseRequestDTO;
import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {

    TestCaseMapper INSTANCE = Mappers.getMapper(TestCaseMapper.class);

    @Mapping(target = "id", ignore = true)
    TestCase toTestCase(TestCaseRequestDTO testCase);

    default Challenge challengeFromId(Long challengeId) {
        var c = new Challenge();
        c.setId(challengeId);
        return c;
    }
}
