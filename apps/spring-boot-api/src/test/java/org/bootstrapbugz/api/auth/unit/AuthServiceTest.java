package org.bootstrapbugz.api.auth.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.bootstrapbugz.api.auth.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.redis.repository.JwtBlacklistRepository;
import org.bootstrapbugz.api.auth.redis.repository.RefreshTokenRepository;
import org.bootstrapbugz.api.auth.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.RefreshTokenRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.request.SignUpRequest;
import org.bootstrapbugz.api.auth.response.RefreshTokenResponse;
import org.bootstrapbugz.api.auth.service.impl.AuthServiceImpl;
import org.bootstrapbugz.api.auth.service.impl.JwtServiceImpl;
import org.bootstrapbugz.api.auth.util.AuthUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFoundException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.shared.util.TestUtil;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.Role;
import org.bootstrapbugz.api.user.model.Role.RoleName;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.bootstrapbugz.api.user.response.UserResponse;
import org.bootstrapbugz.api.user.response.RoleResponse;
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

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
  @Mock private UserRepository userRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Spy private UserMapperImpl userMapper;
  @Mock private JwtBlacklistRepository jwtBlacklistRepository;
  @Mock private UserBlacklistRepository userBlacklistRepository;
  @Mock private RefreshTokenRepository refreshTokenRepository;
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
            jwtBlacklistRepository,
            userBlacklistRepository,
            refreshTokenRepository,
            messageService);
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
  void itShouldRefreshToken() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    String token = jwtService.createRefreshToken("user", "ip1");
    when(refreshTokenRepository.existsById(JwtUtil.removeTokenTypeFromToken(token)))
        .thenReturn(true);
    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(token);
    RefreshTokenResponse refreshTokenResponse =
        authService.refreshToken(refreshTokenRequest, request);
    assertThat(refreshTokenResponse.getToken()).isNotNull().startsWith(JwtUtil.TOKEN_TYPE);
    assertThat(refreshTokenResponse.getRefreshToken()).isNotNull().startsWith(JwtUtil.TOKEN_TYPE);
  }

  @Test
  void itShouldSignUp() {
    Set<RoleResponse> roles = Set.of(new RoleResponse(RoleName.USER.name()));
    UserResponse expectedUserResponse =
        new UserResponse(1L, "Test", "Test", "test", "test@test.com", false, true, roles);
    SignUpRequest signUpRequest =
        new SignUpRequest("Test", "Test", "test", "test@test.com", "qwerty123", "qwerty123");
    when(userRepository.save(any(User.class))).thenReturn(user);
    UserResponse actualUserResponse = authService.signUp(signUpRequest);
    assertThat(actualUserResponse).isEqualTo(expectedUserResponse);
  }

  @Test
  void itShouldConfirmRegistration() {
    User expectedUser =
        new User(1L, "Test", "Test", "test", "test@test.com", password, true, true, roles);
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("test", JwtPurpose.CONFIRM_REGISTRATION));
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    authService.confirmRegistration(token);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue()).isEqualTo(expectedUser);
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_invalid() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("test", JwtPurpose.CONFIRM_REGISTRATION));
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.confirmRegistration(token))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void confirmRegistrationShouldThrowForbidden_userAlreadyActivated() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("test", JwtPurpose.CONFIRM_REGISTRATION));
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    user.setActivated(true);
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.confirmRegistration(token))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void itShouldResendConfirmationEmail() {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("test");
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
    authService.resendConfirmationEmail(resendConfirmationEmailRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void resendConfirmationEmailShouldThrowResourceNotFound_userNotFound() {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void resendConfirmationEmailShouldThrowForbidden_userAlreadyActivated() {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("test");
    when(messageService.getMessage("user.activated")).thenReturn("User already activated.");
    user.setActivated(true);
    when(userRepository.findByUsernameOrEmail("test", "test")).thenReturn(Optional.of(user));
    assertThatThrownBy(() -> authService.resendConfirmationEmail(resendConfirmationEmailRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("User already activated.");
  }

  @Test
  void itShouldForgotPassword() {
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("test@test.com");
    when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
    authService.forgotPassword(forgotPasswordRequest);
    verify(eventPublisher, times(1)).publishEvent(any(OnSendJwtEmail.class));
  }

  @Test
  void forgotPasswordShouldThrowResourceNotFound_userNotFound() {
    ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest("test@test.com");
    when(messageService.getMessage("user.notFound")).thenReturn("User not found.");
    assertThatThrownBy(() -> authService.forgotPassword(forgotPasswordRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("User not found.");
  }

  @Test
  void itShouldResetPassword() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("test", JwtPurpose.FORGOT_PASSWORD));
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
    authService.resetPassword(resetPasswordRequest);
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(
            bCryptPasswordEncoder.matches(
                resetPasswordRequest.getPassword(), userArgumentCaptor.getValue().getPassword()))
        .isTrue();
  }

  @Test
  void resetPasswordShouldThrowForbidden_invalid() {
    String token =
        JwtUtil.removeTokenTypeFromToken(
            jwtService.createToken("test", JwtPurpose.FORGOT_PASSWORD));
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest(token, "qwerty1234", "qwerty1234");
    when(messageService.getMessage("token.invalid")).thenReturn("Invalid token.");
    assertThatThrownBy(() -> authService.resetPassword(resetPasswordRequest))
        .isInstanceOf(ForbiddenException.class)
        .hasMessage("Invalid token.");
  }

  @Test
  void itShouldLogout() {
    TestUtil.setAuth(auth, securityContext, user);
    String token = jwtService.createToken("test", JwtPurpose.ACCESSING_RESOURCES);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("x-forwarded-for", "ip1");
    request.addHeader(AuthUtil.AUTH_HEADER, token);
    assertDoesNotThrow(() -> authService.logout(request));
  }

  @Test
  void itShouldLogoutFromAllDevices() {
    TestUtil.setAuth(auth, securityContext, user);
    assertDoesNotThrow(() -> authService.logoutFromAllDevices());
  }
}
