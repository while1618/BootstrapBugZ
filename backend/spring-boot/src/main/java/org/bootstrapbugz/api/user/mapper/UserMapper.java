package org.bootstrapbugz.api.user.mapper;

import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(source = "roles", target = "roleDTOs")
  UserDTO userToAdminUserDTO(User user);

  @Mapping(target = "activated", ignore = true)
  @Mapping(target = "nonLocked", ignore = true)
  @Mapping(target = "roleDTOs", ignore = true)
  UserDTO userToProfileUserDTO(User user);

  @Mapping(target = "activated", ignore = true)
  @Mapping(target = "nonLocked", ignore = true)
  @Mapping(target = "roleDTOs", ignore = true)
  UserDTO userPrincipalToProfileUserDTO(UserPrincipal userPrincipal);

  @Mapping(target = "email", ignore = true)
  @Mapping(target = "activated", ignore = true)
  @Mapping(target = "nonLocked", ignore = true)
  @Mapping(target = "roleDTOs", ignore = true)
  UserDTO userToSimpleUserDTO(User user);
}
