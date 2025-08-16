package dev.gustavo.math.service;

import dev.gustavo.math.entity.Challenge;
import dev.gustavo.math.entity.enums.ChallengeDifficulty;
import dev.gustavo.math.exception.EntityNotFoundException;
import dev.gustavo.math.repository.ChallengeRepository;
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
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @InjectMocks
    private ChallengeService challengeService;

    private Challenge challenge;

    @BeforeEach
    void setUp() {
        challenge = new Challenge();
        challenge.setId(1L);
        challenge.setTitle("Derivative Challenge");
        challenge.setDescription("Find the derivative of x^2");
        challenge.setDifficulty(ChallengeDifficulty.EASY);
    }

    @Nested
    @DisplayName("Find Challenges")
    class FindChallenge {
        @Test
        @DisplayName("Should return a paginated list of challenges")
        void findAllShouldReturnPaginatedChallenges() {
            PageRequest pageable = PageRequest.of(0, 10);
            Page<Challenge> challengePage = new PageImpl<>(List.of(challenge));
            when(challengeRepository.findAll(pageable)).thenReturn(challengePage);

            Page<Challenge> challenges = challengeService.findAll(pageable);

            assertFalse(challenges.getContent().isEmpty());
            assertEquals(1, challenges.getTotalElements());
            verify(challengeRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("Should find a challenge by its ID successfully")
        void findByIdShouldReturnChallengeWhenFound() {
            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));

            Challenge foundChallenge = challengeService.findById(1L);

            assertNotNull(foundChallenge);
            assertEquals(1L, foundChallenge.getId());
            verify(challengeRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when challenge does not exist")
        void findByIdShouldThrowExceptionWhenNotFound() {
            when(challengeRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> challengeService.findById(1L));
        }
    }

    @Nested
    @DisplayName("Create Challenge")
    class CreateChallenge {
        @Test
        @DisplayName("Should create a new challenge successfully")
        void createShouldSaveAndReturnChallenge() {
            when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

            Challenge createdChallenge = challengeService.create(challenge);

            assertNotNull(createdChallenge);
            assertEquals("Derivative Challenge", createdChallenge.getTitle());
            verify(challengeRepository, times(1)).save(challenge);
        }
    }

    @Nested
    @DisplayName("Update Challenge")
    class UpdateChallenge {
        @Test
        @DisplayName("Should update challenge details successfully")
        void updateShouldUpdateAndReturnChallengeWhenSuccessful() {
            Challenge updatedDetails = new Challenge();
            updatedDetails.setTitle("Advanced Derivative Challenge");
            updatedDetails.setDifficulty(ChallengeDifficulty.MEDIUM);

            when(challengeRepository.findById(1L)).thenReturn(Optional.of(challenge));
            when(challengeRepository.save(any(Challenge.class))).thenReturn(challenge);

            Challenge updatedChallenge = challengeService.update(1L, updatedDetails);

            assertNotNull(updatedChallenge);
            assertEquals("Advanced Derivative Challenge", updatedChallenge.getTitle());
            assertEquals(ChallengeDifficulty.MEDIUM, updatedChallenge.getDifficulty());
            verify(challengeRepository, times(1)).findById(1L);
            verify(challengeRepository, times(1)).save(challenge);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when challenge does not exist")
        void updateShouldThrowExceptionWhenChallengeNotFound() {
            Challenge updatedDetails = new Challenge();
            when(challengeRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> challengeService.update(1L, updatedDetails));
            verify(challengeRepository, never()).save(any(Challenge.class));
        }
    }

    @Nested
    @DisplayName("Delete Challenge")
    class DeleteChallenge {
        @Test
        @DisplayName("Should delete a challenge successfully")
        void deleteShouldRemoveChallengeWhenChallengeExists() {
            when(challengeRepository.existsById(1L)).thenReturn(true);
            doNothing().when(challengeRepository).deleteById(1L);

            challengeService.delete(1L);

            verify(challengeRepository, times(1)).existsById(1L);
            verify(challengeRepository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when challenge does not exist")
        void deleteShouldThrowExceptionWhenChallengeNotFound() {
            when(challengeRepository.existsById(1L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> challengeService.delete(1L));
            verify(challengeRepository, never()).deleteById(anyLong());
        }
    }
}
