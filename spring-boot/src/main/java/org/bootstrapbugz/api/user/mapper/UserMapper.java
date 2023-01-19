package org.bootstrapbugz.api.user.mapper;

import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  @Mapping(source = "roles", target = "roleDTOs")
  UserDTO userToUserDTO(User user);
}
