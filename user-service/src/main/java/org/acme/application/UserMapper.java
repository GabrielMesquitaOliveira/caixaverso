package org.acme.application;

import org.acme.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto dto);
}
