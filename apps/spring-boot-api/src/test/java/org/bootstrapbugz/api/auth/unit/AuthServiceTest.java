package org.bootstrapbugz.api.auth.unit;

import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.payload.request.ConfirmRegistrationRequest;
import org.bootstrapbugz.api.auth.payload.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.payload.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.payload.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.payload.request.SignUpRequest;
import org.bootstrapbugz.api.auth.redis.repository.AccessTokenBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenWhitelistRepository;
import org.bootstrapbugz.api.auth.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.service.impl.AuthServiceImpl;
import org.bootstrapbugz.api.auth.service.impl.JwtServiceImpl;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.payload.response.RoleResponse;
import org.bootstrapbugz.api.user.payload.response.UserResponse;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Spy private UserMapperImpl userMapper;
  @Mock private AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenWhitelistRepository refreshTokenWhitelistRepository;
  @Mock private MessageService messageService;
  @Mock private Authentication auth;
  @Mock private SecurityContext securityContext;

  private JwtServiceImpl jwtService;
  private AuthServiceImpl authService;
  private String password;
  private Set<Role> roles;
  private User user;

  @Captor private ArgumentCaptor<User> userArgumentCaptor;

  @BeforeEach
  public void setUp() {
    jwtService =
        new JwtServiceImpl(
            accessTokenBlacklistRepository, userBlacklistRepository,
            refreshTokenWhitelistRepository, messageService);
    authService =
        new AuthServiceImpl(
            userRepository,
            jwtService,
            eventPublisher,
            messageService,
            bCryptPasswordEncoder,
            userMapper);
    password = bCryptPasswordEncoder.encode("qwerty123");
    roles = Set.of(new Role(RoleName.USER));
    user = new User(1L, "Test", "Test", "test", "test@test.com", password, false, true, roles);
  }

  @Test
  void itShouldSignUp() {
    var roleResponses = Set.of(new RoleResponse(RoleName.USER.name()));
    var expectedUserResponse =
        new UserResponse(1L, "Test", "Test", "test", "test@test.com", false, true, roleResponses);
    var signUpRequest =
        new SignUpRequest("Test", "Test", "test", "test@test.com", "qwerty123", "qwerty123");
    when(userRepository.save(any(User.class))).thenReturn(user);
    var actualUserResponse = authService.signUp(signUpRequest);
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void itShouldResendConfirmationEmail() {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void resendConfirmationEmailShouldThrowResourceNotFound_userNotFound() {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void resendConfirmationEmailShouldThrowForbidden_userAlreadyActivated() {
    var resendConfirmationEmailRequest = new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    user.setActivated(true);
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void itShouldConfirmRegistration() {
    var expectedUser =
        new User(1L, "Test", "Test", "test", "test@test.com", password, true, true, roles);
    String token = jwtService.createToken(1L, JwtPurpose.CONFIRM_REGISTRATION);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    authService.confirmRegistration(new ConfirmRegistrationRequest(token));
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalid() {
    String token = jwtService.createToken(1L, JwtPurpose.CONFIRM_REGISTRATION);
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.confirmRegistration(new ConfirmRegistrationRequest(token)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() {
    String token = jwtService.createToken(1L, JwtPurpose.CONFIRM_REGISTRATION);
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    user.setActivated(true);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.confirmRegistration(new ConfirmRegistrationRequest(token)))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void itShouldRefreshToken() {
    var request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    String token = jwtService.createRefreshToken(1L, Collections.emptySet(), "ip1");
    when(refreshTokenWhitelistRepository.existsById(token)).thenReturn(true);
    var refreshTokenRequest = new RefreshTokenRequest(token);
    var refreshTokenResponse = authService.refreshToken(refreshTokenRequest, request);
    assertThat(refreshTokenResponse.getAccessToken()).isNotNull();
    assertThat(refreshTokenResponse.getRefreshToken()).isNotNull();
  }

  @Test
  void itShouldSignOut() {
    TestUtil.setAuth(auth, securityContext, user);
    String token = jwtService.createToken(1L, JwtPurpose.ACCESSING_RESOURCES);
    var request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    request.addHeader(AuthUtil.AUTH_HEADER, token);
    assertDoesNotThrow(() -> authService.signOut(request));
  }

  @Test
  void itShouldSignOutFromAllDevices() {
    TestUtil.setAuth(auth, securityContext, user);
    assertDoesNotThrow(() -> authService.signOutFromAllDevices());
  }

  @Test
  void itShouldForgotPassword() {
    var forgotPasswordRequest = new ForgotPasswordRequest("test@test.com");
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    authService.forgotPassword(forgotPasswordRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() {
    var forgotPasswordRequest = new ForgotPasswordRequest("test@test.com");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.forgotPassword(forgotPasswordRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void itShouldResetPassword() {
    String token = jwtService.createToken(1L, JwtPurpose.FORGOT_PASSWORD);
    var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    authService.resetPassword(resetPasswordRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(
            bCryptPasswordEncoder.matches(
                resetPasswordRequest.getPassword(), userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalid() {
    String token = jwtService.createToken(1L, JwtPurpose.FORGOT_PASSWORD);
    var resetPasswordRequest = new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.resetPassword(resetPasswordRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldReceiveSignedInUser() {
    TestUtil.setAuth(auth, securityContext, user);
    var roleResponses = Set.of(new RoleResponse(RoleName.USER.name()));
    var expectedUserResponse =
        new UserResponse(1L, "Test", "Test", "test", "test@test.com", false, true, roleResponses);
    var actualUserResponse = authService.signedInUser();
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void itShouldCheckUsernameAvailability() {
    when(userRepository.existsByUsername("user")).thenReturn(true);
    assertThat(authService.isUsernameAvailable("user")).isFalse();
  }

  @Test
  void itShouldCheckEmailAvailability() {
    when(userRepository.existsByEmail("available@bootstrapbugz.com")).thenReturn(false);
    assertThat(authService.isEmailAvailable("available@bootstrapbugz.com")).isTrue();
  }
}
