package dev.gustavo.math.mapper;

import dev.gustavo.math.controller.dto.user.UserRequestDTO;
import dev.gustavo.math.controller.dto.user.UserResponseDTO;
import dev.gustavo.math.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "rank", constant = "BEGINNER")
    User toUser(UserRequestDTO user);

    UserResponseDTO toUserResponseDTO(User user);
}
