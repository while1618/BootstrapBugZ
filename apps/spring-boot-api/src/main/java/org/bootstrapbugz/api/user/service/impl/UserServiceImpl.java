package org.bootstrapbugz.api.user.service.impl;

import java.util.List;
import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.ErrorDomain;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.user.dto.SimpleUserDto;
import org.bootstrapbugz.api.user.mapper.UserMapper;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final MessageSource messageSource;
  private final UserMapper userMapper;
  private final PasswordEncoder bCryptPasswordEncoder;
  private final ApplicationEventPublisher eventPublisher;
  private final JwtService jwtService;

  public UserServiceImpl(
      UserRepository userRepository,
      MessageSource messageSource,
      UserMapper userMapper,
      PasswordEncoder bCryptPasswordEncoder,
      ApplicationEventPublisher eventPublisher,
      JwtService jwtService) {
    this.userRepository = userRepository;
    this.messageSource = messageSource;
    this.userMapper = userMapper;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.eventPublisher = eventPublisher;
    this.jwtService = jwtService;
  }

  @Override
  public List<SimpleUserDto> findAll() {
    List<User> users = userRepository.findAll();
    if (users.isEmpty())
      throw new ResourceNotFound(
          messageSource.getMessage("users.notFound", null, LocaleContextHolder.getLocale()),
          ErrorDomain.USER);
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
                        messageSource.getMessage(
                            "user.notFound", null, LocaleContextHolder.getLocale()),
                        ErrorDomain.USER));
    return userMapper.userToSimpleUserDto(user);
  }

  @Override
  public SimpleUserDto update(UpdateUserRequest updateUserRequest) {
    User user = AuthUtil.findLoggedUser(userRepository, messageSource);
    user.setFirstName(updateUserRequest.getFirstName());
    user.setLastName(updateUserRequest.getLastName());
    tryToSetUsername(user, updateUserRequest.getUsername());
    tryToSetEmail(user, updateUserRequest.getEmail());
    return userMapper.userToSimpleUserDto(userRepository.save(user));
  }

  private void tryToSetUsername(User user, String username) {
    if (user.getUsername().equals(username)) return;
    if (userRepository.existsByUsername(username))
      throw new BadRequestException(
          messageSource.getMessage("username.exists", null, LocaleContextHolder.getLocale()),
          ErrorDomain.USER);

    user.setUsername(username);
    jwtService.invalidateAllTokens(user.getUsername());
  }

  private void tryToSetEmail(User user, String email) {
    if (user.getEmail().equals(email)) return;
    if (userRepository.existsByEmail(email))
      throw new BadRequestException(
          messageSource.getMessage("email.exists", null, LocaleContextHolder.getLocale()),
          ErrorDomain.USER);

    user.setEmail(email);
    user.setActivated(false);
    jwtService.invalidateAllTokens(user.getUsername());

    String token = jwtService.createToken(user.getUsername(), JwtPurpose.CONFIRM_REGISTRATION);
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, JwtPurpose.CONFIRM_REGISTRATION));
  }

  @Override
  public void changePassword(ChangePasswordRequest changePasswordRequest) {
    User user = AuthUtil.findLoggedUser(userRepository, messageSource);
    if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
      throw new BadRequestException(
          messageSource.getMessage("oldPassword.invalid", null, LocaleContextHolder.getLocale()),
          ErrorDomain.USER);
    changePassword(user, changePasswordRequest.getNewPassword());
  }

  private void changePassword(User user, String password) {
    user.setPassword(bCryptPasswordEncoder.encode(password));
    jwtService.invalidateAllTokens(user.getUsername());
    userRepository.save(user);
  }
}
