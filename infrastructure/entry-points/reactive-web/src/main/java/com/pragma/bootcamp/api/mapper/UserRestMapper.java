package com.pragma.bootcamp.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.pragma.bootcamp.api.dto.UserCreateDTO;
import com.pragma.bootcamp.api.dto.UserDTO;
import com.pragma.bootcamp.model.user.User;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    User toUser(UserCreateDTO userCreateDTO);

    User toUser(UserDTO userDTO);

    UserDTO toUserDTO(User user);

    List<UserDTO> toUserDTOList(List<User> users);

}