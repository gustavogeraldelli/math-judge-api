package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.ITestCaseController;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseCreateRequestDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseResponseDTO;
import dev.gustavo.math.controller.dto.testcase.TestCaseUpdateRequestDTO;
import dev.gustavo.math.mapper.TestCaseMapper;
import dev.gustavo.math.service.TestCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestCaseController implements ITestCaseController {

    private final TestCaseService testCaseService;
    private final TestCaseMapper testCaseMapper;

    @GetMapping("/problems/{problemId}/testcases")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<TestCaseResponseDTO> listByProblem(@PathVariable Long problemId,
                                                                  @RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "10") Integer size) {
        var testCases = testCaseService.listByProblem(problemId, PageRequest.of(page, size))
                .map(testCaseMapper::toTestCaseResponseDTO);
        return new PageableResponseDTO<>(testCases);
    }

    @PostMapping("/problems/{problemId}/testcases")
    @ResponseStatus(HttpStatus.CREATED)
    public TestCaseResponseDTO create(@PathVariable Long problemId,
                                      @Valid @RequestBody TestCaseCreateRequestDTO testCaseCreateRequest) {
        var testCase = testCaseService.create(
                testCaseMapper.toTestCase(testCaseCreateRequest),
                problemId);
        return testCaseMapper.toTestCaseResponseDTO(testCase);
    }

    @PutMapping("/testcases/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TestCaseResponseDTO update(@PathVariable Long id, @Valid @RequestBody TestCaseUpdateRequestDTO testCaseUpdateRequest) {
        var testCase = testCaseService.update(
                id, testCaseMapper.toTestCase(testCaseUpdateRequest));
        return testCaseMapper.toTestCaseResponseDTO(testCase);
    }

    @DeleteMapping("/testcases/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        testCaseService.delete(id);
    }
}
