package org.bootstrapbugz.api.auth.business;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.bootstrapbugz.api.auth.request.ForgotPasswordRequest;
import org.bootstrapbugz.api.auth.request.ResendConfirmationEmailRequest;
import org.bootstrapbugz.api.auth.request.ResetPasswordRequest;
import org.bootstrapbugz.api.auth.service.JwtService;
import org.bootstrapbugz.api.auth.service.impl.AuthServiceImpl;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.error.exception.ResourceNotFound;
import org.bootstrapbugz.api.user.mapper.UserMapperImpl;
import org.bootstrapbugz.api.user.model.User;
import org.bootstrapbugz.api.user.repository.RoleRepository;
import org.bootstrapbugz.api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceNotOkTest {
  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @Spy private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private MessageSource messageSource;
  @Mock private JwtService jwtService;
  @Spy private UserMapperImpl userMapper;
  @Mock private Authentication authentication;
  @Mock private SecurityContext securityContext;

  @InjectMocks private AuthServiceImpl authService;

  private User getUser() {
    return new User()
        .setId(1L)
        .setFirstName("User")
        .setLastName("User")
        .setUsername("user")
        .setEmail("user@localhost.com")
        .setPassword(bCryptPasswordEncoder.encode("qwerty123"))
        .setActivated(true);
//        .setRoles(Set.of(new Role(1L, RoleName.USER)));
  }

  private void authentication(boolean withUser) {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    if (withUser) when(authentication.getName()).thenReturn("user");
  }

  @Test
  void confirmRegistration_invalidToken_forbidden() {
    assertThrows(ForbiddenException.class, () -> authService.confirmRegistration(""));
  }

  @Test
  void resendConfirmationEmail_userAlreadyActivated_forbidden() {
    User user = getUser();
    when(userRepository.findByUsernameOrEmail("user@localhost.com", "user@localhost.com"))
        .thenReturn(Optional.ofNullable(user));
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("user@localhost.com");
    assertThrows(
        ForbiddenException.class,
        () -> authService.resendConfirmationEmail(resendConfirmationEmailRequest));
  }

  @Test
  void resendConfirmationEmail_notFound() {
    ResendConfirmationEmailRequest resendConfirmationEmailRequest =
        new ResendConfirmationEmailRequest("notFound");
    assertThrows(
        ResourceNotFound.class,
        () -> authService.resendConfirmationEmail(resendConfirmationEmailRequest));
  }

  @Test
  void forgotPassword_notFound() {
    ForgotPasswordRequest forgotPasswordRequest =
        new ForgotPasswordRequest("notFound@localhost.com");
    assertThrows(ResourceNotFound.class, () -> authService.forgotPassword(forgotPasswordRequest));
  }

  @Test
  void resetPassword_invalidToken_forbidden() {
    ResetPasswordRequest resetPasswordRequest =
        new ResetPasswordRequest("", "qwerty123", "qwerty123");
    assertThrows(ForbiddenException.class, () -> authService.resetPassword(resetPasswordRequest));
  }

  @Test
  void logout_noAuth_userNotFound() {
    authentication(false);
    assertThrows(ResourceNotFound.class, () -> authService.logout(new MockHttpServletRequest()));
  }
}
