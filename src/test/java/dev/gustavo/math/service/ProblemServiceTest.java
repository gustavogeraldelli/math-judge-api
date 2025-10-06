package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
import dev.gustavo.math.entity.enums.ProblemType;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.ProblemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemService problemService;

    private Problem problem;

    @BeforeEach
    void setUp() {
        problem = new Problem();
        problem.setId(1L);
        problem.setTitle("Derivative Problem");
        problem.setDescription("Find the derivative of x^2");
        problem.setDifficulty(ProblemDifficulty.EASY);
        problem.setType(ProblemType.EXPRESSION);
    }

    @Nested
    @DisplayName("Find Problems")
    class FindProblem {
        @Test
        @DisplayName("Should return a paginated list of problems")
        void findAllShouldReturnPaginatedProblems() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Problem> problemPage = new PageImpl<>(List.of(problem));
            when(problemRepository.findAll(pageable)).thenReturn(problemPage);

            Page<Problem> problems = problemService.findAll(pageable);

            assertFalse(problems.getContent().isEmpty());
            assertEquals(1, problems.getTotalElements());
            verify(problemRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should return a problem when ID exists")
        void findByIdShouldReturnProblemWhenIdExists() {
            when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));

            Problem foundProblem = problemService.findById(1L);

            assertNotNull(foundProblem);
            assertEquals(1L, foundProblem.getId());
            verify(problemRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when ID does not exist")
        void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExist() {
            when(problemRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> problemService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Create Problem")
    class CreateProblem {
        @Test
        @DisplayName("Should save and return a new problem with valid data")
        void createShouldSaveAndReturnProblemWhenDataIsValid() {
            when(problemRepository.save(any(Problem.class))).thenReturn(problem);

            Problem createdProblem = problemService.create(problem);

            assertNotNull(createdProblem);
            assertEquals("Derivative Problem", createdProblem.getTitle());
            verify(problemRepository, times(1)).save(problem);
        }
    }

    @Nested
    @DisplayName("Update Problem")
    class UpdateProblem {
        @Test
        @DisplayName("Should update and return problem when ID exists")
        void updateShouldUpdateAndReturnProblemWhenIdExists() {
            Problem updatedDetails = new Problem();
            updatedDetails.setTitle("Advanced Derivative Problem");
            updatedDetails.setDifficulty(ProblemDifficulty.MEDIUM);

            when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
            when(problemRepository.save(any(Problem.class))).thenReturn(problem);

            Problem updatedProblem = problemService.update(1L, updatedDetails);

            assertNotNull(updatedProblem);
            assertEquals("Advanced Derivative Problem", updatedProblem.getTitle());
            assertEquals(ProblemDifficulty.MEDIUM, updatedProblem.getDifficulty());
            verify(problemRepository, times(1)).findById(1L);
            verify(problemRepository, times(1)).save(problem);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when ID does not exist")
        void updateShouldThrowExceptionOnUpdateWhenIdDoesNotExist() {
            Problem updatedDetails = new Problem();
            when(problemRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> problemService.update(1L, updatedDetails));
            verify(problemRepository, never()).save(any(Problem.class));
        }
    }

    @Nested
    @DisplayName("Delete Problem")
    class DeleteProblem {
        @Test
        @DisplayName("Should delete problem when ID exists")
        void deleteShouldDeleteProblemWhenIdExists() {
            when(problemRepository.existsById(1L)).thenReturn(true);
            doNothing().when(problemRepository).deleteById(1L);

            problemService.delete(1L);

            verify(problemRepository, times(1)).existsById(1L);
            verify(problemRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when ID does not exist")
        void deleteShouldThrowExceptionOnDeleteWhenIdDoesNotExist() {
            when(problemRepository.existsById(1L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> problemService.delete(1L));
            verify(problemRepository, never()).deleteById(anyLong());
        }
    }
}
