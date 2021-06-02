package org.bootstrapbugz.api.user.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bootstrapbugz.api.auth.util.JwtUtilities;
import org.bootstrapbugz.api.user.dto.SimpleUserDto;
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
class UserServiceOkTest {
  @Mock private UserRepository userRepository;
  @Mock private MessageSource messageSource;
  @Spy private UserMapperImpl userMapper;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;
  @Mock private JwtUtilities jwtUtilities;

  @InjectMocks private UserServiceImpl userService;

  private Role userRole;
  private User user;

  @BeforeEach
  void init() {
    userRole = new Role(1L, RoleName.USER);
    user =
        new User()
            .setId(2L)
            .setFirstName("User")
            .setLastName("User")
            .setUsername("user")
            .setEmail("user@localhost.com")
            .setPassword(bCryptPasswordEncoder.encode("qwerty123"))
            .setActivated(true)
            .setRoles(Set.of(userRole));
  }

  private void authentication() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("user");
  }

  @Test
  void findAll_ok() {
    when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
    List<SimpleUserDto> foundUsers = userService.findAll();
    assertThat(foundUsers).isNotNull().hasSize(1);
  }

  @Test
  void findByUsername_ok() {
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    SimpleUserDto foundUser = userService.findByUsername("user");
    assertThat(foundUser).isNotNull();
    assertEquals(foundUser.getUsername(), user.getUsername());
  }

  @Test
  void update_ok() {
    authentication();
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("Test", "Test", "test", "test@localhost.com");
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    when(userRepository.existsByUsername(updateUserRequest.getUsername())).thenReturn(false);
    when(userRepository.existsByEmail(updateUserRequest.getEmail())).thenReturn(false);
    User expectedUser = expectedUser(updateUserRequest);
    when(userRepository.save(any(User.class))).thenReturn(expectedUser);
    SimpleUserDto returnedUser = userService.update(updateUserRequest);
    assertThat(returnedUser).isNotNull();
    assertEquals(returnedUser.getUsername(), user.getUsername());
    assertEquals(returnedUser.getEmail(), user.getEmail());
  }

  private User expectedUser(UpdateUserRequest updateUserRequest) {
    return new User()
        .setId(2L)
        .setFirstName(updateUserRequest.getFirstName())
        .setLastName(updateUserRequest.getLastName())
        .setUsername(updateUserRequest.getUsername())
        .setEmail(updateUserRequest.getEmail())
        .setPassword(bCryptPasswordEncoder.encode("qwerty123"))
        .setActivated(false)
        .setRoles(Set.of(userRole));
  }

  @Test
  void update_noChanges_ok() {
    authentication();
    UpdateUserRequest updateUserRequest =
        new UpdateUserRequest("User", "User", "user", "user@localhost.com");
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    when(userRepository.save(any(User.class))).thenReturn(user);
    SimpleUserDto noChangesUser = userService.update(updateUserRequest);
    assertThat(noChangesUser).isNotNull();
    assertEquals(noChangesUser.getUsername(), user.getUsername());
    assertEquals(noChangesUser.getEmail(), user.getEmail());
  }

  @Test
  void changePassword_noContent() {
    authentication();
    ChangePasswordRequest changePasswordRequest =
        new ChangePasswordRequest("qwerty123", "BlaBla123", "BlaBla123");
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    userService.changePassword(changePasswordRequest);
    assertFalse(
        bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()));
    assertTrue(
        bCryptPasswordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPassword()));
  }
}
