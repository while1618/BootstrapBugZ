package org.bootstrapbugz.api.user.mapper;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.bootstrapbugz.api.auth.security.user.details.UserPrincipal;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.dto.RoleDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.GrantedAuthority;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

  @Mapping(source = "roles", target = "roleDTOs")
  UserDTO userToUserDTO(User user);

  @Mapping(source = "enabled", target = "activated")
  @Mapping(source = "accountNonLocked", target = "nonLocked")
  @Mapping(source = "authorities", target = "roleDTOs", qualifiedByName = "authoritiesToRoleDTOs")
  UserDTO userPrincipalToUserDTO(UserPrincipal userPrincipal);

  @Named("authoritiesToRoleDTOs")
  default Set<RoleDTO> authoritiesToRoleDTOs(Collection<? extends GrantedAuthority> authorities) {
    return authorities.stream()
        .map(authority -> new RoleDTO(RoleName.valueOf(authority.getAuthority()).name()))
        .collect(Collectors.toSet());
  }
}
