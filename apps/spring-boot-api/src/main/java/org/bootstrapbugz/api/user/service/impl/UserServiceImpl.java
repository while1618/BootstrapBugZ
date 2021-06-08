package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.dto.SimpleUserDto;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final MessageService messageService;
  private final UserMapper userMapper;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final JwtService jwtService;

  public UserServiceImpl(
      UserRepository userRepository,
      MessageService messageService,
      UserMapper userMapper,
      PasswordEncoder bCryptPasswordEncoder,
      ApplicationEventPublisher eventPublisher,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.messageService = messageService;
    this.userMapper = userMapper;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.eventPublisher = eventPublisher;
    this.jwtService = jwtService;
  }

  @Override
  public List<SimpleUserDto> findAll() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty())
      throw new ResourceNotFound(messageService.getMessage("users.notFound"), ErrorDomain.USER);
    return userMapper.usersToSimpleUserDtos(users);
  }

  @Override
  public SimpleUserDto findByUsername(String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new ResourceNotFound(
                        messageService.getMessage("user.notFound"), ErrorDomain.USER));
    return userMapper.userToSimpleUserDto(user);
  }

  @Override
  public SimpleUserDto update(UpdateUserRequest updateUserRequest) {
    User user = AuthUtil.findLoggedUser(userRepository, messageService);
    user.setFirstName(updateUserRequest.getFirstName());
    user.setLastName(updateUserRequest.getLastName());
    tryToSetUsername(user, updateUserRequest.getUsername());
    tryToSetEmail(user, updateUserRequest.getEmail());
    return userMapper.userToSimpleUserDto(userRepository.save(user));
  }

  private void tryToSetUsername(User user, String username) {
    if (user.getUsername().equals(username)) return;
    if (userRepository.existsByUsername(username))
      throw new BadRequestException(messageService.getMessage("username.exists"), ErrorDomain.USER);

    user.setUsername(username);
    jwtService.invalidateAllTokens(user.getUsername());
  }

  private void tryToSetEmail(User user, String email) {
    if (user.getEmail().equals(email)) return;
    if (userRepository.existsByEmail(email))
      throw new BadRequestException(messageService.getMessage("email.exists"), ErrorDomain.USER);

    user.setEmail(email);
    user.setActivated(false);
    jwtService.invalidateAllTokens(user.getUsername());

    String token = jwtService.createToken(user.getUsername(), JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = AuthUtil.findLoggedUser(userRepository, messageService);
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
      throw new BadRequestException(
          messageService.getMessage("oldPassword.invalid"), ErrorDomain.USER);
    user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()));
    jwtService.invalidateAllTokens(user.getUsername());
    userRepository.save(user);
  }
}
