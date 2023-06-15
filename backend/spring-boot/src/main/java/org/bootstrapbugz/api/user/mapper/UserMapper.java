package org.bootstrapbugz.api.user.mapper;

import java.util.Set;
import org.bootstrapbugz.api.auth.security.UserPrincipal;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  Set<RoleDTO> rolesToRoleDTOs(Set<Role> roles);

  @Mapping(source = "roles", target = "roleDTOs")
  UserDTO userToAdminUserDTO(User user);

  @Mapping(target = "active", ignore = true)
  @Mapping(target = "lock", ignore = true)
  @Mapping(target = "roleDTOs", ignore = true)
  UserDTO userToProfileUserDTO(User user);

  @Mapping(target = "active", ignore = true)
  @Mapping(target = "lock", ignore = true)
  @Mapping(target = "roleDTOs", ignore = true)
  UserDTO userPrincipalToProfileUserDTO(UserPrincipal userPrincipal);

  @Mapping(target = "email", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "lock", ignore = true)
  @Mapping(target = "roleDTOs", ignore = true)
  UserDTO userToSimpleUserDTO(User user);
}
