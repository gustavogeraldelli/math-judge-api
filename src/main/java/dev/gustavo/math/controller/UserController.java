package dev.gustavo.math.controller;

import dev.gustavo.math.controller.doc.IUserController;
import dev.gustavo.math.controller.dto.PageableResponseDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.controller.dto.user.UserSubmissionsResponseDTO;
import dev.gustavo.math.mapper.ProblemMapper;
import dev.gustavo.math.mapper.SubmissionMapper;
import dev.gustavo.math.mapper.UserMapper;
import dev.gustavo.math.service.SubmissionService;
import dev.gustavo.math.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements IUserController {

    private final UserService userService;
    private final SubmissionService submissionService;
    private final UserMapper userMapper;
    private final SubmissionMapper submissionMapper;
    private final ProblemMapper problemMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<UserResponseDTO> findAll(@RequestParam(defaultValue = "0") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        var usersPage = userService.findAll(PageRequest.of(page, size))
                .map(userMapper::toUserResponseDTO);
        return new PageableResponseDTO<>(usersPage);
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO findById(@PathVariable UUID id) {
        return userMapper.toUserResponseDTO(userService.findById(id));
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDTO update(@PathVariable UUID id, @RequestBody UserRequestDTO userUpdateRequest) {
        var updatedUser = userService.update(id, userMapper.toUser(userUpdateRequest));
        return userMapper.toUserResponseDTO(updatedUser);
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    @PreAuthorize("#id == authentication.principal or hasRole('ADMIN')")
    @GetMapping("/{id}/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<UserSubmissionsResponseDTO> listUserSubmissions(@PathVariable UUID id,
                                                                               @RequestParam(defaultValue = "0") Integer page,
                                                                               @RequestParam(defaultValue = "10") Integer size) {
        var userSubmissions = submissionService.listFromUser(
                        userMapper.toUser(id),
                        PageRequest.of(page, size))
                .map(submissionMapper::toUserSubmissionsResponseDTO);
        return new PageableResponseDTO<>(userSubmissions);
    }

    @PreAuthorize("#userId == authentication.principal or hasRole('ADMIN')")
    @GetMapping("/{userId}/problems/{problemId}/submissions")
    @ResponseStatus(HttpStatus.OK)
    public PageableResponseDTO<UserSubmissionsResponseDTO> listUserSubmissionsInProblem(@PathVariable UUID userId,
                                                                                          @PathVariable Long problemId,
                                                                                          @RequestParam(defaultValue = "0") Integer page,
                                                                                          @RequestParam(defaultValue = "10") Integer size) {
        var userSubmissions = submissionService.listFromUserInProblem(
                        userMapper.toUser(userId),
                        problemMapper.toProblem(problemId),
                        PageRequest.of(page, size))
                .map(submissionMapper::toUserSubmissionsResponseDTO);
        return new PageableResponseDTO<>(userSubmissions);
    }

}
