package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.ITestCaseController;
import dev.gustavo.math.controller.dto.testcase.TestCaseRequestDTO;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.mapper.TestCaseMapper;
import dev.gustavo.math.service.TestCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/testcases")
@RequiredArgsConstructor
public class TestCaseController implements ITestCaseController {

    private final TestCaseService testCaseService;
    private final TestCaseMapper testCaseMapper;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestCase create(@Valid @RequestBody TestCaseRequestDTO testCaseCreateRequest) {
        return testCaseService.create(
                testCaseMapper.toTestCase(testCaseCreateRequest));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TestCase update(@PathVariable Long id, @RequestBody TestCaseRequestDTO testCaseUpdateRequest) {
        return testCaseService.update(
                id, testCaseMapper.toTestCase(testCaseUpdateRequest));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        testCaseService.delete(id);
    }
}
