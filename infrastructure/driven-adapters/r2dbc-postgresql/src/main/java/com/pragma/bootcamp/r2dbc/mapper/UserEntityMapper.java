package com.pragma.bootcamp.r2dbc.mapper;

import com.pragma.bootcamp.model.enums.Role;
import com.pragma.bootcamp.model.user.User;
import com.pragma.bootcamp.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {

    @Mapping(source = "user.role", target = "roleId", qualifiedByName = "roleToId")
    UserEntity toEntity(User user);

    @Mapping(source = "roleId", target = "role", qualifiedByName = "idToRole")
    User toUser(UserEntity userEntity);

    @Named("roleToId")
    default Long roleToId(Role role) {
        return role != null ? role.getId() : null;
    }

    @Named("idToRole")
    default Role idToRole(Long roleId) {
        return roleId != null ? Role.fromId(roleId) : null;
    }
}