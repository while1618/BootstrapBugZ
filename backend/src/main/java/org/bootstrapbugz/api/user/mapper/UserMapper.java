package org.bootstrapbugz.api.user.mapper;

import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  UserResponse userToUserResponse(User user);
}
