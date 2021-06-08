package org.bootstrapbugz.api.user.business;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.shared.error.exception.BadRequestException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.request.ChangePasswordRequest;
import org.bootstrapbugz.api.user.request.UpdateUserRequest;
import org.bootstrapbugz.api.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceNotOkTest {
  @Mock private UserRepository userRepository;
  @Mock private MessageSource messageSource;
  @Spy private UserMapperImpl userMapper;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;
  @Mock private JwtService jwtService;

  @InjectMocks private UserServiceImpl userService;

  private User user;

  @BeforeEach
  void init() {
    user =
        new User()
            .setId(2L)
            .setFirstName("User")
            .setLastName("User")
            .setUsername("user")
            .setEmail("user@localhost.com")
            .setPassword(bCryptPasswordEncoder.encode("qwerty123"))
            .setActivated(true)
            .setRoles(Set.of(new Role(1L, RoleName.USER)));
  }

  private void authentication(boolean withUser) {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    if (withUser) when(authentication.getName()).thenReturn("user");
  }

  @Test
  void findAll_notFound() {
    assertThrows(ResourceNotFound.class, () -> userService.findAll());
  }

  @Test
  void findByUsername_notFound() {
    assertThrows(ResourceNotFound.class, () -> userService.findByUsername("user"));
  }

  @Test
  void update_noAuth_userNotFound() {
    authentication(false);
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test", "test@localhost.com");
    assertThrows(ResourceNotFound.class, () -> userService.update(updateUserRequest));
  }

  @Test
  void update_usernameExists_badRequest() {
    authentication(true);
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "admin", "test@localhost.com");
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(true);
    assertThrows(BadRequestException.class, () -> userService.update(updateUserRequest));
  }

  @Test
  void update_emailExists_badRequest() {
    authentication(true);
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test", "admin@localhost.com");
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(true);
    assertThrows(BadRequestException.class, () -> userService.update(updateUserRequest));
  }

  @Test
  void changePassword_noAuth_userNotFound() {
    authentication(false);
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "BlaBla123", "BlaBla123");
    assertThrows(ResourceNotFound.class, () -> userService.changePassword(changePasswordRequest));
  }

  @Test
  void changePassword_badRequest() {
    authentication(true);
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("123BlaBla", "BlaBla123", "BlaBla123");
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    assertThrows(
        BadRequestException.class, () -> userService.changePassword(changePasswordRequest));
  }
}
