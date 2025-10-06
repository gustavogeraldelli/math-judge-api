package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.TestCase;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestCaseServiceTest {
    @Mock
    private TestCaseRepository testCaseRepository;

    @Mock
    private ProblemService problemService;

    @InjectMocks
    private TestCaseService testCaseService;

    private TestCase testCase;

    @BeforeEach
    void setUp() {
        Problem problem = new Problem();
        problem.setId(1L);

        testCase = new TestCase();
        testCase.setId(1L);
        testCase.setProblem(problem);
        testCase.setVariableValues("2");
        testCase.setExpectedAnswer("4");
    }

    @Nested
    @DisplayName("Find Test Case")
    class FindTestCase {
        @Test
        @DisplayName("Should find a test case by its ID successfully")
        void findByIdShouldReturnTestCaseWhenFound() {
            when(testCaseRepository.findById(1L)).thenReturn(Optional.of(testCase));

            TestCase foundTestCase = testCaseService.findById(1L);

            assertNotNull(foundTestCase);
            assertEquals(1L, foundTestCase.getId());
            verify(testCaseRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when test case does not exist")
        void findByIdShouldThrowExceptionWhenNotFound() {
            when(testCaseRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> testCaseService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Create Test Case")
    class CreateTestCase {
        @Test
        @DisplayName("Should create a new test case successfully")
        void createShouldSaveAndReturnTestCaseWhenChallengeExists() {
            doNothing().when(problemService).existsById(1L);
            when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

            TestCase createdTestCase = testCaseService.create(testCase);

            assertNotNull(createdTestCase);
            assertEquals("2", createdTestCase.getVariableValues());
            verify(problemService, times(1)).existsById(1L);
            verify(testCaseRepository, times(1)).save(testCase);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when the associated challenge does not exist")
        void createShouldThrowExceptionWhenChallengeNotFound() {
            doThrow(new EntityNotFoundException("Challenge", "1")).when(problemService).existsById(1L);

            assertThrows(EntityNotFoundException.class, () -> testCaseService.create(testCase));
            verify(testCaseRepository, never()).save(any(TestCase.class));
        }
    }

    @Nested
    @DisplayName("Update Test Case")
    class UpdateTestCase {
        @Test
        @DisplayName("Should update test case successfully")
        void updateShouldUpdateAndReturnTestCaseWhenSuccessful() {
            TestCase updatedDetails = new TestCase();
            updatedDetails.setVariableValues("3");
            updatedDetails.setExpectedAnswer("9");

            when(testCaseRepository.findById(1L)).thenReturn(Optional.of(testCase));
            when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

            TestCase updatedTestCase = testCaseService.update(1L, updatedDetails);

            assertNotNull(updatedTestCase);
            assertEquals("3", updatedTestCase.getVariableValues());
            assertEquals("9", updatedTestCase.getExpectedAnswer());
            verify(testCaseRepository, times(1)).findById(1L);
            verify(testCaseRepository, times(1)).save(testCase);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when test case does not exist")
        void updateShouldThrowExceptionWhenTestCaseNotFound() {
            TestCase updatedDetails = new TestCase();
            when(testCaseRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> testCaseService.update(1L, updatedDetails));
            verify(testCaseRepository, never()).save(any(TestCase.class));
        }
    }

    @Nested
    @DisplayName("Delete Test Case")
    class DeleteTestCase {
        @Test
        @DisplayName("Should delete a test case successfully")
        void deleteShouldRemoveTestCaseWhenTestCaseExists() {
            when(testCaseRepository.existsById(1L)).thenReturn(true);
            doNothing().when(testCaseRepository).deleteById(1L);

            testCaseService.delete(1L);

            verify(testCaseRepository, times(1)).existsById(1L);
            verify(testCaseRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when test case does not exist")
        void deleteShouldThrowExceptionWhenTestCaseNotFound() {
            when(testCaseRepository.existsById(1L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> testCaseService.delete(1L));
            verify(testCaseRepository, never()).deleteById(anyLong());
        }
    }

}