package com.pragma.bootcamp.api.mapper;

import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDTO userCreateDTO);

    User toUser(UserDTO userDTO);

    UserDTO toUserDTO(User user);

    List<UserDTO> toUserDTOList(List<User> users);

}