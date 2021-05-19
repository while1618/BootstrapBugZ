package com.app.api.auth.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.app.api.auth.request.ForgotPasswordRequest;
import com.app.api.auth.request.ResendConfirmationEmailRequest;
import com.app.api.auth.request.ResetPasswordRequest;
import com.app.api.auth.request.SignUpRequest;
import com.app.api.auth.service.impl.AuthServiceImpl;
import com.app.api.jwt.util.JwtPurpose;
import com.app.api.jwt.util.JwtUtilities;
import com.app.api.user.dto.SimpleUserDto;
import com.app.api.user.mapper.UserMapperImpl;
import com.app.api.user.model.Role;
import com.app.api.user.model.RoleName;
import com.app.api.user.model.User;
import com.app.api.user.repository.RoleRepository;
import com.app.api.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
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
class AuthServiceOkTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private MessageSource messageSource;
  @Mock private JwtUtilities jwtUtilities;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;

  @InjectMocks private AuthServiceImpl authService;

  private Role userRole;
  private User user;

  @BeforeEach
  void init() {
    userRole = new Role(1L, RoleName.USER);
    user =
        new User()
            .setId(1L)
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
  void signUp_created() {
    SignUpRequest signUpRequest =
        new SignUpRequest("User", "User", "user", "user@localhost.com", "qwerty123", "qwerty123");
    when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(userRole));
    when(userRepository.save(any(User.class))).thenReturn(user);
    SimpleUserDto createdUser = authService.signUp(signUpRequest);
    assertThat(createdUser).isNotNull();
    assertEquals(createdUser.getUsername(), user.getUsername());
    assertEquals(createdUser.getEmail(), user.getEmail());
  }

  @Test
  void confirmRegistration_noContent() {
    user.setActivated(false);
    LocalDateTime beforeUpdate = user.getUpdatedAt();
    when(userRepository.findByUsername(null)).thenReturn(Optional.ofNullable(user));
    String token = jwtUtilities.createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
    authService.confirmRegistration(token);
    assertTrue(user.isActivated());
    assertNotEquals(beforeUpdate, user.getUpdatedAt());
  }

  @Test
  void resendConfirmationEmail_noContent() {
    user.setActivated(false);
    when(userRepository.findByUsernameOrEmail("user@localhost.com", "user@localhost.com"))
        .thenReturn(Optional.ofNullable(user));
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("user@localhost.com");
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    verify(jwtUtilities, times(1)).createToken(user, JwtPurpose.CONFIRM_REGISTRATION);
  }

  @Test
  void forgotPassword_noContent() {
    when(userRepository.findByEmail("user@localhost.com")).thenReturn(Optional.ofNullable(user));
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("user@localhost.com");
    authService.forgotPassword(forgotPasswordRequest);
    verify(jwtUtilities, times(1)).createToken(user, JwtPurpose.FORGOT_PASSWORD);
  }

  @Test
  void resetPassword_noContent() {
    LocalDateTime beforeUpdate = user.getUpdatedAt();
    when(userRepository.findByUsername(null)).thenReturn(Optional.ofNullable(user));
    String token = jwtUtilities.createToken(user, JwtPurpose.FORGOT_PASSWORD);
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(token, "BlaBla123", "BlaBla123");
    authService.resetPassword(resetPasswordRequest);
    assertTrue(bCryptPasswordEncoder.matches("BlaBla123", user.getPassword()));
    assertFalse(bCryptPasswordEncoder.matches("qwerty123", user.getPassword()));
    assertNotEquals(beforeUpdate, user.getUpdatedAt());
  }

  @Test
  void logout_noContent() {
    authentication();
    when(userRepository.findByUsername("user")).thenReturn(Optional.ofNullable(user));
    LocalDateTime lastLogoutBeforeChange = user.getUpdatedAt();
    authService.logout();
    assertNotEquals(lastLogoutBeforeChange, user.getLastLogout());
  }
}
