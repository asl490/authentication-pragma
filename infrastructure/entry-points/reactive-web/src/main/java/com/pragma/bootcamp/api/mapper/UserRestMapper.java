package com.pragma.bootcamp.api.mapper;

import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.model.enums.ErrorCode;
import com.pragma.bootcamp.model.enums.Role;
import com.pragma.bootcamp.model.exception.UserValidationException;
import com.pragma.bootcamp.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring", imports = Role.class)
public interface UserRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
    User toUser(UserCreateDTO userCreateDTO);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "mapRole")
    User toUser(UserDTO userDTO);

    UserDTO toUserDTO(User user);

    List<UserDTO> toUserDTOList(List<User> users);

    @Named("mapRole")
    static Role mapRole(String role) {
        return Arrays.stream(Role.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new UserValidationException(ErrorCode.ROLE_NOT_FOUND));
    }
}