package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.user.LoginRequestDTO;
import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "rank", constant = "BEGINNER")
    User toUser(UserRequestDTO user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "rank", ignore = true)
    User toUser(LoginRequestDTO loginRequest);

    UserResponseDTO toUserResponseDTO(User user);

    User toUser(UUID id);
}
