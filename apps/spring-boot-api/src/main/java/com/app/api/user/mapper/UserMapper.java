package com.app.api.user.mapper;

import com.app.api.user.dto.SimpleUserDto;
import com.app.api.user.dto.UserDto;
import com.app.api.user.model.User;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
  SimpleUserDto userToSimpleUserDto(User user);

  List<SimpleUserDto> usersToSimpleUserDtos(List<User> users);

  List<UserDto> usersToUserDtos(List<User> users);
}
