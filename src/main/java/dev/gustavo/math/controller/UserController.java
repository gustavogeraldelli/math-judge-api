package dev.gustavo.math.controller;

import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.controller.dto.user.UserSubmissionsResponseDTO;
import dev.gustavo.math.mapper.ChallengeMapper;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.mapper.UserMapper;
import dev.gustavo.math.service.SubmissionService;
import dev.gustavo.math.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SubmissionService submissionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<UserResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        var usersPage = userService.findAll(PageRequest.of(page, size))
                .map(UserMapper.INSTANCE::toUserResponseDTO);
        return new PageableResponseDTO<>(usersPage);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO findById(@PathVariable UUID id) {
        return UserMapper.INSTANCE.toUserResponseDTO(userService.findById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO update(@PathVariable UUID id, @RequestBody UserRequestDTO userUpdateRequest) {
        var updatedUser = userService.update(id, UserMapper.INSTANCE.toUser(userUpdateRequest));
        return UserMapper.INSTANCE.toUserResponseDTO(updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    @GetMapping("/{id}/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<UserSubmissionsResponseDTO> listUserSubmissions(@PathVariable UUID id,
                                                                               @RequestParam(defaultValue = "0") Integer page,
                                                                               @RequestParam(defaultValue = "10") Integer size) {
        var userSubmissions = submissionService.listFromUser(
                        UserMapper.INSTANCE.toUser(id),
                        PageRequest.of(page, size))
                .map(SubmissionMapper.INSTANCE::toUserSubmissionsResponseDTO);
        return new PageableResponseDTO<>(userSubmissions);
    }

    @GetMapping("/{userId}/challenges/{challengeId}/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<UserSubmissionsResponseDTO> listUserSubmissionsInChallenge(@PathVariable UUID userId,
                                                                                          @PathVariable Long challengeId,
                                                                                          @RequestParam(defaultValue = "0") Integer page,
                                                                                          @RequestParam(defaultValue = "10") Integer size) {
        var userSubmissions = submissionService.listFromUserInChallenge(
                        UserMapper.INSTANCE.toUser(userId),
                        ChallengeMapper.INSTANCE.toChallenge(challengeId),
                        PageRequest.of(page, size))
                .map(SubmissionMapper.INSTANCE::toUserSubmissionsResponseDTO);
        return new PageableResponseDTO<>(userSubmissions);
    }

}
