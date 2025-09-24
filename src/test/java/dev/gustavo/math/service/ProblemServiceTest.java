package dev.gustavo.math.service;

import dev.gustavo.math.entity.Problem;
import dev.gustavo.math.entity.enums.ProblemDifficulty;
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
        problem.setTitle("Derivative Challenge");
        problem.setDescription("Find the derivative of x^2");
        problem.setDifficulty(ProblemDifficulty.EASY);
    }

    @Nested
    @DisplayName("Find Challenges")
    class FindProblem {
        @Test
        @DisplayName("Should return a paginated list of challenges")
        void findAllShouldReturnPaginatedChallenges() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Problem> challengePage = new PageImpl<>(List.of(problem));
            when(problemRepository.findAll(pageable)).thenReturn(challengePage);

            Page<Problem> challenges = problemService.findAll(pageable);

            assertFalse(challenges.getContent().isEmpty());
            assertEquals(1, challenges.getTotalElements());
            verify(problemRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should find a challenge by its ID successfully")
        void findByIdShouldReturnChallengeWhenFound() {
            when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));

            Problem foundProblem = problemService.findById(1L);

            assertNotNull(foundProblem);
            assertEquals(1L, foundProblem.getId());
            verify(problemRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when challenge does not exist")
        void findByIdShouldThrowExceptionWhenNotFound() {
            when(problemRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> problemService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Create Challenge")
    class CreateProblem {
        @Test
        @DisplayName("Should create a new challenge successfully")
        void createShouldSaveAndReturnChallenge() {
            when(problemRepository.save(any(Problem.class))).thenReturn(problem);

            Problem createdProblem = problemService.create(problem);

            assertNotNull(createdProblem);
            assertEquals("Derivative Challenge", createdProblem.getTitle());
            verify(problemRepository, times(1)).save(problem);
        }
    }

    @Nested
    @DisplayName("Update Challenge")
    class UpdateProblem {
        @Test
        @DisplayName("Should update challenge details successfully")
        void updateShouldUpdateAndReturnChallengeWhenSuccessful() {
            Problem updatedDetails = new Problem();
            updatedDetails.setTitle("Advanced Derivative Challenge");
            updatedDetails.setDifficulty(ProblemDifficulty.MEDIUM);

            when(problemRepository.findById(1L)).thenReturn(Optional.of(problem));
            when(problemRepository.save(any(Problem.class))).thenReturn(problem);

            Problem updatedProblem = problemService.update(1L, updatedDetails);

            assertNotNull(updatedProblem);
            assertEquals("Advanced Derivative Challenge", updatedProblem.getTitle());
            assertEquals(ProblemDifficulty.MEDIUM, updatedProblem.getDifficulty());
            verify(problemRepository, times(1)).findById(1L);
            verify(problemRepository, times(1)).save(problem);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when challenge does not exist")
        void updateShouldThrowExceptionWhenChallengeNotFound() {
            Problem updatedDetails = new Problem();
            when(problemRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> problemService.update(1L, updatedDetails));
            verify(problemRepository, never()).save(any(Problem.class));
        }
    }

    @Nested
    @DisplayName("Delete Challenge")
    class DeleteProblem {
        @Test
        @DisplayName("Should delete a challenge successfully")
        void deleteShouldRemoveChallengeWhenChallengeExists() {
            when(problemRepository.existsById(1L)).thenReturn(true);
            doNothing().when(problemRepository).deleteById(1L);

            problemService.delete(1L);

            verify(problemRepository, times(1)).existsById(1L);
            verify(problemRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when challenge does not exist")
        void deleteShouldThrowExceptionWhenChallengeNotFound() {
            when(problemRepository.existsById(1L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> problemService.delete(1L));
            verify(problemRepository, never()).deleteById(anyLong());
        }
    }
}
