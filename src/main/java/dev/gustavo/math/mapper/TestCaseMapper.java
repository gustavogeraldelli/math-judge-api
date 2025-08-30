package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.testcase.TestCaseRequestDTO;
import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.TestCase;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TestCaseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "challenge.id", source = "challenge")
    TestCase toTestCase(TestCaseRequestDTO testCase);

}
